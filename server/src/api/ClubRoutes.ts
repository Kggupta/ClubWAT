import express from "express";
import { prisma } from "../lib/prisma";
import { Category, Club, ClubAdmin, ClubCategory } from "@prisma/client";
import {
  INTERNAL_ERROR_CODE,
  INVALID_REQUEST_CODE,
  OK_CODE
} from "../lib/StatusCodes";

const router = express.Router();

interface ClubWithCategories extends Club {
    categories?: string[]
}

interface ClubWithCategoryIds extends Club {
    categories: number[]
}

type ClubCategoryWithoutId = {
    club_id: number,
    category_id: number
}

type ClubResponse = {
    data: ClubWithCategories[]
}

type ChosenClub = {
    id: string
}

type ClubAdminResponse = {
    data: ClubAdmin[]
}

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

router.get<ChosenClub, ClubAdminResponse>("/:id", async (req, res) => {
    try {
        const clubId = Number(req.params.id);
        if (!clubId) {
            return res.sendStatus(INVALID_REQUEST_CODE);
        }

        const admins: ClubAdmin[] = await prisma.clubAdmin.findMany({
            where: {
                club_id: clubId
            }
        });

        res.json({ data: admins }).status(OK_CODE);
    } catch (error) {
        res.sendStatus(INTERNAL_ERROR_CODE);
    }
});

router.post<ClubWithCategoryIds, void>("/", async (req, res) => {
    try {
        if (!req.body.title || !req.body.description || !req.body.membership_fee || !req.body.categories) {
            return res.sendStatus(INVALID_REQUEST_CODE);
        }

        const club = await prisma.club.create({
            data: {
                title: req.body.title,
                description: req.body.description,
                membership_fee: req.body.membership_fee
            }
        });

        if (req.body.categories.length) {
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

router.delete<ChosenClub, void>("/:id", async (req, res) => {
    try {
        const clubId = Number(req.params.id);
        if (!clubId) return res.sendStatus(INVALID_REQUEST_CODE);
        
        await Promise.all([
            prisma.clubCategory.deleteMany({
                where: {
                  club_id: clubId
                }
            }),
            prisma.clubAdmin.deleteMany({
                where: {
                    club_id: clubId
                }
            })
        ])

        await prisma.club.delete({
            where: {
              id: clubId
            }
        });

        res.sendStatus(OK_CODE);
    } catch (error) {
        res.sendStatus(INTERNAL_ERROR_CODE);
    }
});

export default router;
