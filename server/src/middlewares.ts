import { NextFunction, Request, Response } from "express";
import { prisma } from "./lib/prisma";

import ErrorResponse from "./interfaces/ErrorResponse";
import jwt from "jsonwebtoken";
import {
  INTERNAL_ERROR_CODE,
  INVALID_REQUEST_CODE,
  NOT_FOUND_CODE,
  OK_CODE,
  UNAUTHORIZED_CODE,
} from "./lib/StatusCodes";
import { ClubAdmin } from "@prisma/client";

export function resourceNotFound(
  req: Request,
  res: Response,
  next: NextFunction
) {
  res.status(NOT_FOUND_CODE);
  const error = new Error(`Requested resource was not found.`);
  next(error);
}

export function errorHandler(
  err: Error,
  req: Request,
  res: Response<ErrorResponse>,
  next: NextFunction
) {
  const statusCode =
    res.statusCode !== OK_CODE ? res.statusCode : INTERNAL_ERROR_CODE;
  res.status(statusCode);
  res.json({
    message: err.message,
    stack: process.env.ENVIRONMENT === "PRODUCTION" ? undefined : err.stack,
  });
}

export function authenticateToken(
  req: Request | any,
  res: Response,
  next: NextFunction
) {
  const authHeader = req.headers["authorization"];
  const token = authHeader && authHeader.split(" ")[1];
  if (!token) return res.sendStatus(UNAUTHORIZED_CODE);
  jwt.verify(
    token,
    process.env.ACCESS_TOKEN_SECRET as string,
    (err: any, user: any) => {
      if (err) return res.sendStatus(UNAUTHORIZED_CODE);
      req.body.user = user;
      next();
    }
  );
}

export async function verifyIsClubAdmin(
  req: Request | any,
  res: Response,
  next: NextFunction
) {
  const clubId = Number(req.params.id);
  if (!clubId) return res.sendStatus(INVALID_REQUEST_CODE);

  const isAdmin: ClubAdmin | null = await prisma.clubAdmin.findFirst({
    where: {
      user_id: req.body.user.id,
      club_id: clubId,
    },
  });

  if (!isAdmin) return res.sendStatus(UNAUTHORIZED_CODE);

  next();
}
