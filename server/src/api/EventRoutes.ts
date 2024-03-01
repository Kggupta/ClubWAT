import express from "express";
import { EventsResponse } from "./ClubEventRoutes";
import { authenticateToken } from "../middlewares";
import { INTERNAL_ERROR_CODE, OK_CODE } from "../lib/StatusCodes";
import { prisma } from "../lib/prisma";
import { Events } from "@prisma/client";

const eventRoutes = express.Router({ mergeParams: true });

eventRoutes.get<void, EventsResponse>("/", authenticateToken, async (req, res) => {
  try {
      const events: Events[] = await prisma.events.findMany();
      res.status(OK_CODE).json({ data: events });
  } catch (error) {
    res.sendStatus(INTERNAL_ERROR_CODE);
  }
});

eventRoutes.get<void, EventsResponse>("/search", authenticateToken, async (req, res) => {
  try {
    const query: string = (req.query.searchQuery ?? "") as string;

    const events: Events[] = await prisma.events.findMany({
      where: {
        title: { startsWith: "%" + query, mode: "insensitive" },
      },
      orderBy: { title: "asc" }
    });
  
    res.status(OK_CODE).json({data: events});
  } catch (error) {
    res.sendStatus(INTERNAL_ERROR_CODE);
  }
});

eventRoutes.get<void, EventsResponse>("/my-events", authenticateToken, async (req, res) => {
  try {
    /*add the code here*/
  } catch (error) {
    res.sendStatus(INTERNAL_ERROR_CODE);
  }
});

export default eventRoutes;