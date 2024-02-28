import express from "express";
import { prisma } from "../lib/prisma";
import {
  INTERNAL_ERROR_CODE,
  INVALID_REQUEST_CODE,
  OK_CODE,
  CREATED_CODE,
} from "../lib/StatusCodes";
import { authenticateToken, verifyIsClubAdmin } from "../middlewares";
const eventRoutes = express.Router({ mergeParams: true });

type EventsQuery = {
  id?: string; // This is clubId
  eventId?: string;
};

type Events = {
  id: number;
  title: string;
  description: string;
  start_date: Date;
  end_date: Date;
};

type EventsResponse = {
  data: Events[];
};

eventRoutes.get<EventsQuery, EventsResponse>(
  "/:eventId?",
  authenticateToken,
  verifyIsClubAdmin,
  async (req, res) => {
    try {
      if (req.params.id && req.params.eventId) {
        const clubId = Number(req.params.id);
        const eventId = Number(req.params.eventId);
        const events = await prisma.events.findMany({
          where: {
            id: eventId,
            club_id: clubId,
          },
        });
        res.status(OK_CODE).json({ data: events });
      } else {
        const events = await prisma.events.findMany();
        res.status(OK_CODE).json({ data: events });
      }
    } catch (error) {
      res.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

eventRoutes.post<EventsQuery, Events>(
  "/",
  authenticateToken,
  verifyIsClubAdmin,
  async (req, res) => {
    try {
      const club_Id = Number(req.params.id);
      const { title, description, start_date, end_date } = req.body;
      if (!title || !description || !start_date || !end_date || !club_Id) {
        return res.sendStatus(INVALID_REQUEST_CODE);
      }
      const event = await prisma.events.create({
        data: {
          title,
          description,
          start_date,
          end_date,
          club_id: club_Id,
        },
      });
      res.status(CREATED_CODE).json(event);
    } catch (error) {
      res.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

eventRoutes.put<EventsQuery, Events>(
  "/:eventId",
  authenticateToken,
  verifyIsClubAdmin,
  async (req, res) => {
    try {
      const eventId = Number(req.params.eventId);
      const { title, description, start_date, end_date } = req.body;
      const updatedEvent = await prisma.events.update({
        where: { id: eventId },
        data: { title, description, start_date, end_date },
      });
      res.status(OK_CODE).json(updatedEvent);
    } catch (error) {
      res.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

eventRoutes.delete<EventsQuery, void>(
  "/:eventId",
  authenticateToken,
  verifyIsClubAdmin,
  async (req, res) => {
    try {
      const eventId = Number(req.params.eventId);
      await prisma.events.delete({
        where: { id: eventId },
      });
      res.sendStatus(OK_CODE);
    } catch (error) {
      res.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

export default eventRoutes;
