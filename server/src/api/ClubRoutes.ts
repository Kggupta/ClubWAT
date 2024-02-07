import express from "express";
import { prisma } from "../lib/prisma";
import { Category, Club, ClubCategory } from "@prisma/client";
import {
  CONFLICT_CODE,
  CREATED_CODE,
  INTERNAL_ERROR_CODE,
  INVALID_REQUEST_CODE,
  NOT_FOUND_CODE,
  OK_CODE,
  UNAUTHORIZED_CODE,
} from "../lib/StatusCodes";

interface ClubWithCategories extends Club {
    categories?: string[]
}

type ClubCategoryWithoutId {
    club_id: number,
    category_id: number
} 

const router = express.Router();

type ClubResponse = {data: ClubWithCategories[]}

router.get<void, ClubResponse>("/", async (req, res) => {
    try {
        let clubs: ClubWithCategories[] = await prisma.club.findMany();

        if (req.query.withCategories === 'true') {
            for (let i = 0; i < clubs.length; ++i) {
                let categories: string[] = [];
                const categoryIds: ClubCategory[] = await prisma.clubCategory.findMany({
                    where: {club_id: clubs[i].id}
                });

                for (let j = 0; j < categoryIds.length; ++j) {
                    const category: Category[] = await prisma.category.findMany({
                        where: { id: categoryIds[j].category_id }
                    });
                    categories.push(category[0].name);
                }

                clubs[i].categories = categories;
            }
        }

        res.json({ data: clubs }).status(OK_CODE);
    } catch (error) {
        res.sendStatus(INTERNAL_ERROR_CODE);
    }
});

router.post<ClubWithCategories, void>("/", async (req, res) => {
    try {
        if (!req.body.title || !req.body.description || !req.body.membership_fee) {
            return res.sendStatus(INVALID_REQUEST_CODE);
        }

        const club = await prisma.club.create({
            data: {
                title: req.body.title,
                description: req.body.description,
                membership_fee: req.body.membership_fee
            }
        });

        if (req.body.categories) {
            let clubCategories: ClubCategoryWithoutId[] = [];
            req.body.categories.forEach((category_id: number) => {
                clubCategories.push({ club_id: club.id, category_id });
            });
            await prisma.clubCategory.createMany({ data: clubCategories });
        }

        res.sendStatus(OK_CODE);
    } catch (error) {
        res.sendStatus(INTERNAL_ERROR_CODE);
    }
});

export default router;
