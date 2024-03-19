import express from "express";
import { authenticateToken } from "../middlewares";
import {
  INVALID_REQUEST_CODE,
  NOT_FOUND_CODE,
  OK_CODE,
} from "../lib/StatusCodes";
import { prisma } from "../lib/prisma";

const router = express.Router();
enum LikeTypes {
  like = "like",
  unlike = "unlike",
}

router.put("/club/:clubId/:type", authenticateToken, async (req, res) => {
  const { clubId, type } = req.params;

  if (!clubId || !type || isNaN(Number(clubId)))
    return res.sendStatus(INVALID_REQUEST_CODE);
  if (type != LikeTypes.like && type != LikeTypes.unlike)
    return res.sendStatus(INVALID_REQUEST_CODE);

  await prisma.clubLike.deleteMany({
    where: { user_id: req.body.user.id },
  });

  if (type === LikeTypes.unlike) return res.sendStatus(OK_CODE);

  try {
    await prisma.clubLike.create({
      data: { user_id: req.body.user.id, club_id: Number(clubId) },
    });

    res.sendStatus(OK_CODE);
  } catch (error) {
    res.sendStatus(NOT_FOUND_CODE);
  }
});

router.put("/event/:eventId/:type", authenticateToken, async (req, res) => {
  const { eventId, type } = req.params;

  if (!eventId || !type || isNaN(Number(eventId)))
    return res.sendStatus(INVALID_REQUEST_CODE);
  if (type != LikeTypes.like && type != LikeTypes.unlike)
    return res.sendStatus(INVALID_REQUEST_CODE);

  await prisma.eventLike.deleteMany({
    where: { user_id: req.body.user.id },
  });

  if (type === LikeTypes.unlike) return res.sendStatus(OK_CODE);

  try {
    await prisma.eventLike.create({
      data: { user_id: req.body.user.id, event_id: Number(eventId) },
    });

    res.sendStatus(OK_CODE);
  } catch (error) {
    res.sendStatus(NOT_FOUND_CODE);
  }
});

export default router;
