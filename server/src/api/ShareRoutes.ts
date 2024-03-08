import express from "express";
import { authenticateToken } from "../middlewares";
import {
  CONFLICT_CODE,
  INVALID_REQUEST_CODE,
  NOT_FOUND_CODE,
  OK_CODE,
} from "../lib/StatusCodes";
import { prisma } from "../lib/prisma";
import { User } from "@prisma/client";
import EmailService from "../lib/EmailService";

const router = express.Router();

router.put("/club", authenticateToken, async (req, res) => {
  const sourceUser = req.body.user as User;
  const destinationUserId = req.body.destinationUserId;
  const clubId = req.body.clubId;

  if (!destinationUserId || !clubId)
    return res.sendStatus(INVALID_REQUEST_CODE);

  if (sourceUser === destinationUserId) return res.sendStatus(CONFLICT_CODE);

  const destinationUser = await prisma.user.findFirst({
    where: { id: destinationUserId },
  });
  if (!destinationUser) return res.sendStatus(NOT_FOUND_CODE);

  const sharedClub = await prisma.club.findFirst({ where: { id: clubId } });
  if (!sharedClub) return res.sendStatus(NOT_FOUND_CODE);

  await EmailService.sendClubShareEmail(
    destinationUser.email,
    sharedClub,
    sourceUser
  );

  await prisma.notification.create({
    data: {
      source_user_id: sourceUser.id,
      destination_user_id: destinationUserId,
      club_id: clubId,
      content: `${sharedClub.title} was shared with you!`,
    },
  });

  res.sendStatus(OK_CODE);
});

router.delete("/:id", authenticateToken, async (req, res) => {
  const notificationId = parseInt(req.params.id, 10);
  if (!notificationId) return res.sendStatus(INVALID_REQUEST_CODE);

  const { count } = await prisma.notification.deleteMany({
    where: { id: notificationId },
  });

  if (count <= 0) return res.sendStatus(NOT_FOUND_CODE);

  res.sendStatus(OK_CODE);
});

router.get("", authenticateToken, async (req, res) => {
  const userId = req.body.user.id;

  const notifications = await prisma.notification.findMany({
    where: { destination_user_id: userId },
    include: { source_user: true },
  });

  res.status(OK_CODE).json(notifications);
});

router.delete("/clear", authenticateToken, async (req, res) => {
  const userId = req.body.user.id;

  await prisma.notification.deleteMany({
    where: { destination_user_id: userId },
  });

  res.sendStatus(OK_CODE);
});

export default router;
