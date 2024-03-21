import express from "express";
import { authenticateToken, verifyIsClubAdmin } from "../middlewares";
import {
  INTERNAL_ERROR_CODE,
  INVALID_REQUEST_CODE,
  NOT_FOUND_CODE,
  OK_CODE,
} from "../lib/StatusCodes";
import { prisma } from "../lib/prisma";
import { Event } from "@prisma/client";

const eventRoutes = express.Router({ mergeParams: true });

eventRoutes.get<void, Event[]>(
  "/search",
  authenticateToken,
  async (req, res) => {
    try {
      const query: string = (req.query.searchQuery ?? "") as string;

      let events: Event[] = await prisma.event.findMany({
        where: {
          title: { startsWith: "%" + query, mode: "insensitive" },
        },
        orderBy: { title: "asc" },
      });

      const userClubs = await prisma.clubMember.findMany({
        where: {
          user_id: req.body.user.id,
          is_approved: true,
        },
      });

      res
        .status(OK_CODE)
        .json(
          events.filter(
            (x) =>
              !x.private_flag ||
              userClubs.some((club) => club.club_id === x.club_id)
          )
        );
    } catch (error) {
      res.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

eventRoutes.get("/:eventId/details", authenticateToken, async (req, res) => {
  const eventId = parseInt(req.params.eventId);

  if (!eventId) return res.sendStatus(INVALID_REQUEST_CODE);

  const event = await prisma.event.findUnique({
    where: { id: eventId },
    include: { event_attendance: true, event_bookmark: true, likes: true },
  });

  if (!event) return res.sendStatus(NOT_FOUND_CODE);

  res.status(OK_CODE).json({
    likeCount: event.likes.length,
    isClientLikedEvent: event.likes.some((x) => x.user_id === req.body.user.id),
    isAttending: event.event_attendance.some(
      (x) => x.user_id === req.body.user.id
    ),
    isBookmarked: event.event_bookmark.some(
      (x) => x.user_id === req.body.user.id
    ),
    ...event,
  });
});

eventRoutes.put(
  "/:eventId/manage-attendance",
  authenticateToken,
  async (req, res) => {
    const eventId = parseInt(req.params.eventId);

    if (!eventId) return res.sendStatus(INVALID_REQUEST_CODE);

    const event = await prisma.event.findUnique({
      where: { id: eventId },
      include: { event_attendance: true },
    });

    if (!event) return res.sendStatus(NOT_FOUND_CODE);

    if (event.event_attendance.some((x) => x.user_id === req.body.user.id)) {
      await prisma.eventAttendance.deleteMany({
        where: { user_id: req.body.user.id },
      });
    } else {
      await prisma.eventAttendance.create({
        data: { user_id: req.body.user.id, event_id: eventId },
      });
    }

    res.sendStatus(OK_CODE);
  }
);

eventRoutes.put(
  "/:eventId/manage-bookmark",
  authenticateToken,
  async (req, res) => {
    const eventId = parseInt(req.params.eventId);

    if (!eventId) return res.sendStatus(INVALID_REQUEST_CODE);

    const event = await prisma.event.findUnique({
      where: { id: eventId },
      include: { event_bookmark: true },
    });

    if (!event) return res.sendStatus(NOT_FOUND_CODE);

    if (event.event_bookmark.some((x) => x.user_id === req.body.user.id)) {
      await prisma.eventBookmark.deleteMany({
        where: { user_id: req.body.user.id },
      });
    } else {
      await prisma.eventBookmark.create({
        data: { user_id: req.body.user.id, event_id: eventId },
      });
    }

    res.sendStatus(OK_CODE);
  }
);

enum MyEventType {
  Attend = 0,
  Bookmark,
  Joined,
}

type MyEvent = {
  event: Event;
  type: MyEventType;
};

type MyEventResponse = {
  data: MyEvent[];
};

eventRoutes.get<void, MyEventResponse>(
  "/my-events",
  authenticateToken,
  async (req, res) => {
    const userId = req.body.user.id;
    try {
      let myEvents: MyEvent[] = [];
      myEvents = (
        await prisma.eventAttendance.findMany({
          where: { user_id: userId },
          include: { event: true },
        })
      ).map((x) => {
        return { event: x.event, type: MyEventType.Attend };
      });

      const bookmarkedEvents = (
        await prisma.eventBookmark.findMany({
          where: { user_id: userId },
          include: { event: true },
        })
      ).map((x) => {
        return { event: x.event, type: MyEventType.Bookmark };
      });

      myEvents = myEvents.concat(bookmarkedEvents);

      const joinedClubs = (
        await prisma.clubMember.findMany({
          where: { user_id: userId, is_approved: true },
        })
      ).map((x) => x.club_id);

      const eventsForJoinedClubs = (
        await prisma.event.findMany({
          where: { club_id: { in: joinedClubs } },
        })
      ).map((x) => {
        return { event: x, type: MyEventType.Joined };
      });

      myEvents = myEvents.concat(eventsForJoinedClubs);

      myEvents = myEvents.filter(
        (event, index) =>
          index ===
          myEvents.findIndex((other) => event.event.id === other.event.id)
      );

      myEvents = myEvents.filter(
        (x) =>
          !x.event.private_flag ||
          (x.event.private_flag && joinedClubs.includes(x.event.club_id))
      );

      res.status(OK_CODE).json({ data: myEvents });
    } catch (error) {
      res.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

eventRoutes.delete(
  "/:eventId",
  authenticateToken,
  verifyIsClubAdmin,
  async (req, res) => {
    try {
      const eventId = Number(req.params.eventId);
      await prisma.event.delete({
        where: { id: eventId },
      });
      res.sendStatus(OK_CODE);
    } catch (error) {
      res.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

export default eventRoutes;
