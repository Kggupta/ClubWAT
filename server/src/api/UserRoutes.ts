import express from "express";
import jwt from "jsonwebtoken";
import { prisma } from "../lib/prisma";
import { User } from "@prisma/client";
import { authenticateToken } from "../middlewares";

const router = express.Router();

type LoginRequest = { email: string; password: string };
type LoginResponse = { data: string };

router.post<LoginRequest, LoginResponse>("/login", async (req, res) => {
  const email: string = req.body.email;
  const password: string = req.body.password;
  if (!email || !password) return res.sendStatus(400);

  try {
    const foundUser: User = await prisma.user.findUniqueOrThrow({
      where: {
        email: email,
        password: password,
      },
    });

    res.status(200);
    const token = jwt.sign(
      foundUser,
      process.env.ACCESS_TOKEN_SECRET as string
    );

    res.json({ data: token });
  } catch (error) {
    res.sendStatus(401);
  }
});

type RegisterRequest = {
  email: string;
  password: string;
  first_name: string;
  last_name: string;
};
type RegisterResponse = { data: string };

router.post<RegisterRequest, RegisterResponse>(
  "/register",
  async (req, res) => {
    const registerRequest: RegisterRequest = req.body as RegisterRequest;
    if (
      !registerRequest.email ||
      !registerRequest.password ||
      !registerRequest.first_name ||
      !registerRequest.last_name ||
      !registerRequest.email.endsWith("@uwaterloo.ca")
    )
      return res.sendStatus(400);

    const doesUserExist = await prisma.user.count({
      where: { email: registerRequest.email },
    });
    if (doesUserExist > 0) return res.status(409);

    const userToCreate = { admin_flag: false, ...registerRequest };

    const createdUser: User = await prisma.user.create({ data: userToCreate });

    res.status(200);
    const token = jwt.sign(
      createdUser,
      process.env.ACCESS_TOKEN_SECRET as string
    );

    res.json({ data: token });
  }
);

router.get("/val", authenticateToken, (req, res) => {
  res.json(req.body.user);
});
export default router;
