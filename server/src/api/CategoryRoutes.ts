import express from "express";
import { prisma } from "../lib/prisma";
import {
  INTERNAL_ERROR_CODE,
  INVALID_REQUEST_CODE,
  OK_CODE
} from "../lib/StatusCodes";
import { authenticateToken } from "../middlewares";
const router = express.Router();

type CategoryDetails = {
    type: string
    name: string
}

type CategoryResponse = {
    id: number,
    type: string,
    name: string
}

router.get<void, CategoryResponse[]>("/", authenticateToken, async (req, res) => {
    try {
        let query = {}

        let categories: CategoryResponse[] = await prisma.category.findMany(query)
        res.status(OK_CODE).json(categories);

    } catch (error) {
        res.sendStatus(INTERNAL_ERROR_CODE);
    }
});

router.post<CategoryDetails, void>("/", authenticateToken, async (req, res) => {
    try {
        if (!req.body.type || !req.body.name) {
            return res.sendStatus(INVALID_REQUEST_CODE);
        }

        await Promise.all([
            prisma.category.create({
                data: {
                    type: req.body.type,
                    name: req.body.name
                }
            }),
        ]);

        res.sendStatus(OK_CODE);
    } catch (error) {
        res.sendStatus(INTERNAL_ERROR_CODE);
    }
});

export default router;