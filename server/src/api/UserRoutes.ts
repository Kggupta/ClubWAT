import express from "express";
import jwt from "jsonwebtoken";
import { prisma } from "../lib/prisma";
import { User, UserEmailVerification } from "@prisma/client";
import { authenticateToken } from "../middlewares";
import {
  CONFLICT_CODE,
  CREATED_CODE,
  INTERNAL_ERROR_CODE,
  INVALID_REQUEST_CODE,
  NOT_FOUND_CODE,
  OK_CODE,
  UNAUTHORIZED_CODE,
} from "../lib/StatusCodes";
import sendEmail from "../lib/EmailService";
import { passwordStrength } from "check-password-strength";

const router = express.Router();

type LoginRequest = { email: string; password: string };
type LoginResponse = { data: string };

router.post<LoginRequest, LoginResponse>("/login", async (req, res) => {
  const email: string = req.body.email;
  const password: string = req.body.password;
  if (!email || !password) return res.sendStatus(INVALID_REQUEST_CODE);

  try {
    const foundUser: User = await prisma.user.findUniqueOrThrow({
      where: {
        email: email,
        password: password,
      },
    });

    res.status(OK_CODE);
    const token = jwt.sign(
      foundUser,
      process.env.ACCESS_TOKEN_SECRET as string
    );

    res.json({ data: token });
  } catch (error) {
    res.sendStatus(UNAUTHORIZED_CODE);
  }
});

type EmailVerificationRequest = {
  email: string;
};

router.post<EmailVerificationRequest, void>(
  "/registration-email-verification",
  async (req, res) => {
    const verificationRequest: EmailVerificationRequest =
      req.body as EmailVerificationRequest;
    if (
      !verificationRequest.email ||
      !verificationRequest.email.endsWith("@uwaterloo.ca")
    )
      return res.sendStatus(INVALID_REQUEST_CODE);

    const doesUserAccountExist: number = await prisma.user.count({
      where: {
        email: verificationRequest.email,
      },
    });

    if (doesUserAccountExist > 0) return res.sendStatus(CONFLICT_CODE);

    const verificationCode = Math.floor(100000 + Math.random() * 900000);

    try {
      await sendEmail(verificationRequest.email, verificationCode);

      await prisma.userEmailVerification.deleteMany({
        where: { email: verificationRequest.email },
      });
      console.log(verificationCode);
      await prisma.userEmailVerification.create({
        data: { email: verificationRequest.email, code: verificationCode },
      });

      res.sendStatus(OK_CODE);
    } catch (error) {
      res.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

type RegistrationRequest = {
  email: string;
  password: string;
  first_name: string;
  last_name: string;
  code: number;
};
type RegistrationResponse = {
  data: string;
};

router.post<RegistrationRequest, RegistrationResponse>(
  "/register",
  async (req, res) => {
    const registrationRequest: RegistrationRequest =
      req.body as RegistrationRequest;
    if (
      !registrationRequest.email ||
      !registrationRequest.email.endsWith("@uwaterloo.ca") ||
      !registrationRequest.password ||
      !registrationRequest.first_name ||
      !registrationRequest.last_name ||
      !registrationRequest.code
    )
      return res.sendStatus(INVALID_REQUEST_CODE);

    const strength = passwordStrength(registrationRequest.password);

    if (strength.contains.length !== 4 || strength.length < 8)
      return res.sendStatus(INVALID_REQUEST_CODE);

    const doesUserAccountExist: number = await prisma.user.count({
      where: {
        email: registrationRequest.email,
      },
    });

    if (doesUserAccountExist > 0) return res.sendStatus(CONFLICT_CODE);

    const userVerification: UserEmailVerification | null =
      await prisma.userEmailVerification.findFirst({
        where: { email: registrationRequest.email },
      });

    if (!userVerification) return res.sendStatus(NOT_FOUND_CODE);

    if (userVerification.code !== registrationRequest.code)
      return res.sendStatus(INVALID_REQUEST_CODE);

    await prisma.userEmailVerification.delete({
      where: { email: registrationRequest.email },
    });

    const createdUser: User = await prisma.user.create({
      data: {
        admin_flag: false,
        email: registrationRequest.email,
        first_name: registrationRequest.first_name,
        last_name: registrationRequest.last_name,
        password: registrationRequest.password,
      },
    });

    const token: string = jwt.sign(
      createdUser,
      process.env.ACCESS_TOKEN_SECRET as string
    );

    res.status(CREATED_CODE);
    res.json({ data: token });
  }
);

type UserRequest = {
  id: number;
};

type UserDetailsResponse = {
  email: string;
  first_name: string;
  last_name: string;
};

router.get<UserRequest, UserDetailsResponse>(
  "/:id",
  authenticateToken,
  async (req, res) => {
    const userId = Number(req.params.id);
    if (!userId) {
      return res.sendStatus(INVALID_REQUEST_CODE);
    }

    const user = await prisma.user.findFirst({
      where: { id: userId },
      select: { email: true, first_name: true, last_name: true },
    });

    if (!user) {
      return res.sendStatus(NOT_FOUND_CODE);
    }

    res.status(OK_CODE).json(user);
  }
);

export default router;
