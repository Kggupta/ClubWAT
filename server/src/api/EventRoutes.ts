import express from "express";
import { EventsResponse } from "./ClubEventRoutes";
import { authenticateToken } from "../middlewares";
import { INTERNAL_ERROR_CODE, OK_CODE } from "../lib/StatusCodes";
import { prisma } from "../lib/prisma";
import { Event } from "@prisma/client";

const eventRoutes = express.Router({ mergeParams: true });

eventRoutes.get<void, EventsResponse>(
  "/",
  authenticateToken,
  async (req, res) => {
    try {
      const events: Event[] = await prisma.event.findMany();
      res.status(OK_CODE).json({ data: events });
    } catch (error) {
      res.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

eventRoutes.get<void, EventsResponse>(
  "/search",
  authenticateToken,
  async (req, res) => {
    try {
      const query: string = (req.query.searchQuery ?? "") as string;

      const events: Event[] = await prisma.event.findMany({
        where: {
          title: { startsWith: "%" + query, mode: "insensitive" },
        },
        orderBy: { title: "asc" },
      });

      res.status(OK_CODE).json({ data: events });
    } catch (error) {
      res.sendStatus(INTERNAL_ERROR_CODE);
    }
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

      const bookmarkedEvents = (
        await prisma.eventBookmark.findMany({
          where: { user_id: userId },
          include: { event: true },
        })
      ).map((x) => {
        return { event: x.event, type: MyEventType.Bookmark };
      });

      myEvents = myEvents.concat(bookmarkedEvents);

      res.status(OK_CODE).json({ data: myEvents });
    } catch (error) {
      res.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

export default eventRoutes;
