import express from "express";
import { prisma } from "../lib/prisma";
import { Club, ClubAdmin, ClubCategory } from "@prisma/client";
import {
  INTERNAL_ERROR_CODE,
  INVALID_REQUEST_CODE,
  OK_CODE
} from "../lib/StatusCodes";
import { authenticateToken, verifyIsClubAdmin } from "../middlewares";

const router = express.Router();

interface ClubWithCategories extends Club {
    categories?: ClubCategory[]
}

interface ClubDetails extends Club {
    categories: number[],
    position?: string
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

type ClubStatus = {
    status?: string
}

type includeQuery = {
    categories: {
        select: {
            category: {
                select: {
                    id: true,
                    type: true,
                    name: true
                }
            }
        }
    }
}

async function addClubCategories(clubId: number, categories: number[]) {
    if (!categories.length) return;

    let clubCategories: ClubCategoryWithoutId[] = [];
    categories.forEach((category_id: number) => {
        clubCategories.push({ club_id: clubId, category_id });
    });

    await prisma.clubCategory.createMany({ data: clubCategories });
}

router.get("/:param?", authenticateToken, async (req, res) => {
    try {
        const param = req.params.param;

        if (param === 'approved' || param === 'not-approved') {
            let query: { where?: { is_approved: boolean }; include?: includeQuery } = {};
            query.where = { is_approved: param === 'approved' };

            if (req.query.withCategories === 'true') {
                query.include = {
                    categories: {
                        select: {
                            category: {
                                select: {
                                    id: true,
                                    type: true,
                                    name: true
                                }
                            }
                        }
                    }
                };
            }

            let clubs: ClubWithCategories[] = await prisma.club.findMany(query);
            return res.json({ data: clubs }).status(OK_CODE);
        } else if (param && !isNaN(Number(param))) {
            const clubId = Number(param);
            const admins: ClubAdmin[] = await prisma.clubAdmin.findMany({
                where: {
                    club_id: clubId
                },
                include: {
                    user: true
                }
            });

            return res.json({ data: admins }).status(OK_CODE);
        } else if (param) {
            return res.sendStatus(INVALID_REQUEST_CODE);
        } else {
            let clubs: ClubWithCategories[] = await prisma.club.findMany({
                include: {
                    categories: true
                }
            })
            return res.json({ data: clubs }).status(OK_CODE);
        }
    } catch (error) {
        res.sendStatus(INTERNAL_ERROR_CODE);
    }
});

router.post<ClubDetails, void>("/", authenticateToken, async (req, res) => {
    try {
        if (!req.body.title || !req.body.description ||
            !req.body.membership_fee || !req.body.categories || !req.body.position) {
            return res.sendStatus(INVALID_REQUEST_CODE);
        }

        const club = await prisma.club.create({
            data: {
                title: req.body.title,
                description: req.body.description,
                membership_fee: req.body.membership_fee,
                is_approved: false
            }
        });

        await Promise.all([
            prisma.clubAdmin.create({
                data: {
                    club_id: club.id,
                    user_id: req.body.user.id,
                    position: req.body.position
                }
            }),
            addClubCategories(club.id, req.body.categories)
        ]);

        res.sendStatus(OK_CODE);
    } catch (error) {
        res.sendStatus(INTERNAL_ERROR_CODE);
    }
});

router.put<ClubDetails, void>("/:id", authenticateToken, verifyIsClubAdmin, async (req, res) => {
    try {
        if (!req.body.title || !req.body.description ||
            !req.body.membership_fee || !req.body.categories || !req.body.is_approved) {
            return res.sendStatus(INVALID_REQUEST_CODE);
        }

        const clubId = Number(req.params.id);

        Promise.all([
            prisma.club.update({
                where: {
                    id: clubId
                },
                data: {
                    title: req.body.title,
                    description: req.body.description,
                    membership_fee: req.body.membership_fee,
                    is_approved: req.body.is_approved
                }
            }),
            (async () => {
                await prisma.clubCategory.deleteMany({
                    where: { club_id: clubId }
                });
                addClubCategories(clubId, req.body.categories);
            })()
        ]);

        res.sendStatus(OK_CODE);
    } catch (error) {
        res.sendStatus(INTERNAL_ERROR_CODE);
    }
});

router.delete<ChosenClub, void>("/:id", authenticateToken, verifyIsClubAdmin, async (req, res) => {
    try {
        const clubId = Number(req.params.id);
        
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
        ]);

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
