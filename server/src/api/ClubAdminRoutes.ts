import express from "express";
import { authenticateToken, verifyIsClubAdmin } from "../middlewares";
import { prisma } from "../lib/prisma";
import {
  INVALID_REQUEST_CODE,
  NOT_FOUND_CODE,
  OK_CODE,
} from "../lib/StatusCodes";
import { AdminType } from "@prisma/client";

const router = express.Router();

router.get(
  "/:id/members",
  authenticateToken,
  verifyIsClubAdmin,
  async (req, res) => {
    const userId = req.body.user.id;
    const clubId = parseInt(req.params.id);

    if (!clubId) return res.sendStatus(INVALID_REQUEST_CODE);

    const members = await prisma.clubMember.findMany({
      where: { club_id: clubId },
      include: { user: true },
    });

    const clubAdmins = await prisma.clubAdmin.findMany({
      where: { club_id: clubId },
    });

    const club = await prisma.club.findFirst({
      where: { id: clubId },
    });

    if (!club) return res.sendStatus(NOT_FOUND_CODE);

    const memberList = members.map((x) => {
      return {
        userId: x.user_id,
        isApproved: x.is_approved,
        isClubAdmin: clubAdmins.some((admin) => admin.user_id === x.user_id),
        isClubCreator: clubAdmins.some(
          (admin) =>
            admin.user_id === x.user_id && admin.position === AdminType.Owner
        ),
        firstName: x.user.first_name,
        lastName: x.user.last_name,
        email: x.user.email,
      };
    });

    res.status(OK_CODE).json({
      memberList,
      isICreator: clubAdmins.some(
        (x) => x.user_id === userId && x.position === AdminType.Owner
      ),
      isIAdmin: true,
    });
  }
);

router.put(
  "/:id/members/:userId/approve",
  authenticateToken,
  verifyIsClubAdmin,
  async (req, res) => {
    const clubId = parseInt(req.params.id);
    const targetUserId = parseInt(req.params.userId);

    if (!clubId || !targetUserId) return res.sendStatus(INVALID_REQUEST_CODE);

    const member = await prisma.clubMember.findFirst({
      where: { club_id: clubId, user_id: targetUserId },
    });

    if (!member) return res.sendStatus(NOT_FOUND_CODE);

    await prisma.clubMember.update({
      where: { id: member.id },
      data: {
        is_approved: true,
      },
    });

    res.sendStatus(OK_CODE);
  }
);

router.delete(
  "/:id/members/:userId",
  authenticateToken,
  verifyIsClubAdmin,
  async (req, res) => {
    const clubId = parseInt(req.params.id);
    const targetUserId = parseInt(req.params.userId);

    if (!clubId || !targetUserId) return res.sendStatus(INVALID_REQUEST_CODE);

    await prisma.clubMember.deleteMany({
      where: { club_id: clubId, user_id: targetUserId },
    });

    await prisma.clubAdmin.deleteMany({
      where: { club_id: clubId, user_id: targetUserId },
    });

    res.sendStatus(OK_CODE);
  }
);

router.put(
  "/:id/members/:userId/promote",
  authenticateToken,
  verifyIsClubAdmin,
  async (req, res) => {
    const clubId = parseInt(req.params.id);
    const targetUserId = parseInt(req.params.userId);

    if (!clubId || !targetUserId) return res.sendStatus(INVALID_REQUEST_CODE);

    const clubAdmin = await prisma.clubAdmin.findFirst({
      where: { club_id: clubId, user_id: targetUserId },
    });

    if (!clubAdmin) {
      await prisma.clubAdmin.create({
        data: {
          club_id: clubId,
          user_id: targetUserId,
          position: AdminType.Admin,
        },
      });
    }

    res.sendStatus(OK_CODE);
  }
);

router.put(
  "/:id/members/:userId/demote",
  authenticateToken,
  verifyIsClubAdmin,
  async (req, res) => {
    const clubId = parseInt(req.params.id);
    const targetUserId = parseInt(req.params.userId);

    if (!clubId || !targetUserId) return res.sendStatus(INVALID_REQUEST_CODE);

    await prisma.clubAdmin.deleteMany({
      where: { club_id: clubId, user_id: targetUserId },
    });

    res.sendStatus(OK_CODE);
  }
);

export default router;
