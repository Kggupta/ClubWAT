import { NextFunction, Request, Response } from "express";

import ErrorResponse from "./interfaces/ErrorResponse";
import jwt from "jsonwebtoken";

export function resourceNotFound(
  req: Request,
  res: Response,
  next: NextFunction
) {
  res.status(404);
  const error = new Error(`Requested resource was not found.`);
  next(error);
}

export function errorHandler(
  err: Error,
  req: Request,
  res: Response<ErrorResponse>,
  next: NextFunction
) {
  const statusCode = res.statusCode !== 200 ? res.statusCode : 500;
  res.status(statusCode);
  res.json({
    message: err.message,
    stack: process.env.ENVIRONMENT === "PRODUCTION" ? undefined : err.stack,
  });
}

export function authenticateToken(
  req: Request,
  res: Response,
  next: NextFunction
) {
  const authHeader = req.headers["authorization"];
  const token = authHeader && authHeader.split(" ")[1];
  if (!token) return res.sendStatus(401);

  jwt.verify(token, process.env.ACCESS_TOKEN_SECRET as string, (err, user) => {
    if (err) return res.sendStatus(401);
    req.body.user = user;
    next();
  });
}
