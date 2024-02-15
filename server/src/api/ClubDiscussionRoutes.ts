import express from "express";
import { prisma } from "../lib/prisma";
import { ClubDiscussion, User } from "@prisma/client";
import {
  INTERNAL_ERROR_CODE,
  INVALID_REQUEST_CODE,
  NOT_FOUND_CODE,
  OK_CODE,
  UNAUTHORIZED_CODE,
} from "../lib/StatusCodes";
import { authenticateToken, verifyIsClubAdmin } from "../middlewares";

const router = express.Router();

type ClubDiscussionResponse = {
  data: ClubDiscussion[];
};

type ClubDiscussionDetails = {
  club_id: number;
  user_id: number;
  message: string;
};

type ClubDiscussionWithUserInfo = ClubDiscussion & { user: User };

router.get<{ clubId: string }, ClubDiscussionResponse>(
  "/:clubId",
  authenticateToken,
  async (req, res) => {
    try {
      const clubId = Number(req.params.clubId);
      if (!clubId) {
        return res.sendStatus(INVALID_REQUEST_CODE);
      }

      const discussions: ClubDiscussionWithUserInfo[] =
        await prisma.clubDiscussion.findMany({
          where: {
            club_id: clubId,
          },
          include: {
            user: true,
          },
          orderBy: {
            create_date: "desc",
          },
        });

      res.json({ data: discussions }).status(OK_CODE);
    } catch (error) {
      res.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

router.post<void, void>("/:cludId", authenticateToken, async (req, res) => {
  try {
    const { club_id, message }: ClubDiscussionDetails = req.body;
    const user_id = req.body.user.id;

    if (!user_id || !club_id || !message) {
      return res.sendStatus(INVALID_REQUEST_CODE);
    }

    await prisma.clubDiscussion.create({
      data: {
        user_id,
        club_id,
        message,
        create_date: new Date(),
      },
    });
    res.sendStatus(OK_CODE);
  } catch (error) {
    res.sendStatus(INTERNAL_ERROR_CODE);
  }
});

router.delete<{ clubId: string; messageId: string }, void>(
  "/:clubId/:messageId",
  authenticateToken,
  async (req, res) => {
    try {
      const { clubId, messageId } = req.params;
      const userId = req.body.user.id;

      const message = await prisma.clubDiscussion.findUnique({
        where: {
          id: Number(messageId),
        },
      });

      if (!message) {
        return res.sendStatus(NOT_FOUND_CODE);
      }

      const isAdmin = await prisma.clubAdmin.findFirst({
        where: {
          club_id: Number(clubId),
          user_id: userId,
        },
      });

      if (!isAdmin && message.user_id !== userId) {
        return res.sendStatus(UNAUTHORIZED_CODE);
      }

      await prisma.clubDiscussion.delete({
        where: {
          id: Number(messageId),
        },
      });

      res.sendStatus(OK_CODE);
    } catch (error) {
      res.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

export default router;
