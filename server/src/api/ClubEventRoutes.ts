import express from "express";
import { prisma } from "../lib/prisma";
import {
  INTERNAL_ERROR_CODE,
  INVALID_REQUEST_CODE,
  OK_CODE,
  CREATED_CODE,
  NOT_FOUND_CODE,
} from "../lib/StatusCodes";
import { authenticateToken, verifyIsClubAdmin } from "../middlewares";
import { Event } from "@prisma/client";
import EmailService from "../lib/EmailService";
const clubEventRoutes = express.Router({ mergeParams: true });

type EventsQuery = {
  id?: string; // This is clubId
  eventId?: string;
};

export type EventsResponse = {
  data: Event[];
};

clubEventRoutes.get<EventsQuery, EventsResponse>(
  "/:eventId?",
  authenticateToken,
  verifyIsClubAdmin,
  async (req, res) => {
    try {
      if (req.params.id && req.params.eventId) {
        const clubId = Number(req.params.id);
        const eventId = Number(req.params.eventId);
        const events = await prisma.event.findMany({
          where: {
            id: eventId,
            club_id: clubId,
          },
        });
        res.status(OK_CODE).json({ data: events });
      } else if (req.params.id) {
        const clubId = Number(req.params.id);
        const events = await prisma.event.findMany({
          where: {
            club_id: clubId,
          },
        });
        res.status(OK_CODE).json({ data: events });
      } else {
        res.sendStatus(INVALID_REQUEST_CODE);
      }
    } catch (error) {
      res.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

clubEventRoutes.post<EventsQuery, Event>(
  "/",
  authenticateToken,
  verifyIsClubAdmin,
  async (req, res) => {
    try {
      const clubId = Number(req.params.id);
      const {
        title,
        description,
        start_date,
        end_date,
        location,
        private_flag,
      } = req.body;
      if (
        !title ||
        !description ||
        !start_date ||
        !end_date ||
        !clubId ||
        !location ||
        typeof private_flag !== "boolean"
      ) {
        return res.sendStatus(INVALID_REQUEST_CODE);
      }

      const club = await prisma.club.findFirst({ where: { id: clubId } });

      if (!club) return res.sendStatus(NOT_FOUND_CODE);

      const event = await prisma.event.create({
        data: {
          title,
          description,
          start_date,
          end_date,
          location,
          private_flag,
          club_id: clubId,
        },
      });

      const joinedUsers = (
        await prisma.clubMember.findMany({
          where: { club_id: clubId, is_approved: true },
          include: { user: true },
        })
      ).map((x) => x.user);

      const notificationData = joinedUsers.map((x) => {
        return {
          source_user_id: req.body.user.id,
          destination_user_id: x.id,
          event_id: event.id,
          create_date: new Date(),
          content: `Upcoming event: ${event.title}`,
        };
      });

      if (joinedUsers.length > 0) {
        await prisma.notification.createMany({
          data: notificationData,
        });

        await EmailService.newEventEmail(
          joinedUsers.map((x) => x.email),
          event,
          club
        );
      }

      res.status(CREATED_CODE).json(event);
    } catch (error) {
      res.sendStatus(INTERNAL_ERROR_CODE);
      console.log(error);
    }
  }
);

clubEventRoutes.put<EventsQuery, Event>(
  "/:eventId",
  authenticateToken,
  verifyIsClubAdmin,
  async (req, res) => {
    try {
      const eventId = Number(req.params.eventId);
      const { title, description, start_date, end_date, location } = req.body;
      const updatedEvent = await prisma.event.update({
        where: { id: eventId },
        data: { title, description, start_date, end_date, location },
      });
      res.status(OK_CODE).json(updatedEvent);
    } catch (error) {
      res.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

clubEventRoutes.delete<EventsQuery, void>(
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

export default clubEventRoutes;
