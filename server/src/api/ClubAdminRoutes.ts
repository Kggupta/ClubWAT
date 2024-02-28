import express from "express";
import { prisma } from "../lib/prisma";
import { ClubAdmin } from "@prisma/client";
import {
  INTERNAL_ERROR_CODE,
  INVALID_REQUEST_CODE,
  OK_CODE,
} from "../lib/StatusCodes";
import { authenticateToken } from "../middlewares";

const router = express.Router();

type ClubAdminResponse = {
  data: ClubAdmin[];
};

type ChosenAdmin = {
  id: string;
};

type ChosenClubAdmin = {
  clubId: string;
  userId: string;
};

router.get<ChosenAdmin, ClubAdminResponse>(
  "/:id",
  authenticateToken,
  async (req, res) => {
    try {
      const userId = Number(req.params.id);
      if (!userId) {
        return res.sendStatus(INVALID_REQUEST_CODE);
      }

      const admins: ClubAdmin[] = await prisma.clubAdmin.findMany({
        where: {
          user_id: userId,
        },
        include: {
          club: true,
        },
      });

      res.json({ data: admins }).status(OK_CODE);
    } catch (error) {
      res.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

router.post<ClubAdmin, void>("/", authenticateToken, async (req, res) => {
  try {
    if (!req.body.user_id || !req.body.club_id || !req.body.position) {
      return res.sendStatus(INVALID_REQUEST_CODE);
    }

    await prisma.clubAdmin.create({
      data: {
        user_id: req.body.user_id,
        club_id: req.body.club_id,
        position: req.body.position,
      },
    });

    res.sendStatus(OK_CODE);
  } catch (error) {
    res.sendStatus(INTERNAL_ERROR_CODE);
  }
});

router.delete<ChosenClubAdmin, void>(
  "/:clubId/:userId",
  authenticateToken,
  async (req, res) => {
    try {
      const clubId = Number(req.params.clubId);
      const userId = Number(req.params.userId);

      if (!clubId || !userId) return res.sendStatus(INVALID_REQUEST_CODE);

      await prisma.clubAdmin.deleteMany({
        where: {
          user_id: userId,
          club_id: clubId,
        },
      });

      res.sendStatus(OK_CODE);
    } catch (error) {
      res.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

export default router;
