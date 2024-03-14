import express from "express";
import { authenticateToken, verifyIsSuperAdmin } from "../middlewares";
import {
  CREATED_CODE,
  INVALID_REQUEST_CODE,
  NOT_FOUND_CODE,
  OK_CODE,
} from "../lib/StatusCodes";
import { prisma } from "../lib/prisma";

const router = express.Router();

router.get("", authenticateToken, async (req, res) => {
  let spotlight = await prisma.event.findFirst({
    where: { spotlight_flag: true },
    include: { userReadSpotlights: true },
  });

  if (!spotlight) return res.sendStatus(NOT_FOUND_CODE);

  if (spotlight.userReadSpotlights.some((x) => x.user_id === req.body.user.id))
    return res.sendStatus(NOT_FOUND_CODE);

  res.status(OK_CODE).json(spotlight);
});

router.post("", authenticateToken, verifyIsSuperAdmin, async (req, res) => {
  const { title, description, startDate, endDate, location } = req.body;

  if (!title || !description || !startDate || !endDate || !location)
    return res.sendStatus(INVALID_REQUEST_CODE);

  try {
    await prisma.event.deleteMany({
      where: { spotlight_flag: true },
    });

    await prisma.event.create({
      data: {
        club_id: parseInt(process.env.WUSA_CLUB_ID as unknown as string),
        title: title,
        description,
        start_date: startDate,
        end_date: endDate,
        location,
        spotlight_flag: true,
      },
    });

    res.sendStatus(CREATED_CODE);
  } catch (error) {
    console.log(error);
    res.sendStatus(INVALID_REQUEST_CODE);
  }
});

router.put("/:id/dismiss", authenticateToken, async (req, res) => {
  const spotlightId = Number(req.params.id);
  if (!spotlightId) return res.sendStatus(INVALID_REQUEST_CODE);

  const spotlight = await prisma.event.findUnique({
    where: { id: spotlightId, spotlight_flag: true },
  });

  if (!spotlight) return res.sendStatus(NOT_FOUND_CODE);

  await prisma.userReadSpotlight.create({
    data: {
      user_id: req.body.user.id,
      event_id: spotlightId,
    },
  });

  res.sendStatus(OK_CODE);
});

export default router;
