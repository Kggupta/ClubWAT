import express from "express";
import { prisma } from "../lib/prisma";
import {
  AdminType,
  Club,
  ClubAdmin,
  ClubCategory,
  ClubMember,
  Prisma,
} from "@prisma/client";
import {
  CONFLICT_CODE,
  INTERNAL_ERROR_CODE,
  INVALID_REQUEST_CODE,
  NOT_FOUND_CODE,
  OK_CODE,
} from "../lib/StatusCodes";
import { authenticateToken, verifyIsClubAdmin } from "../middlewares";

const router = express.Router();

interface ClubDetails extends Club {
  categories: number[];
  position?: string;
}

type ClubCategoryWithoutId = {
  club_id: number;
  category_id: number;
};

type ChosenClub = {
  id: string;
};

type ClubForYouItem = {
  id: number;
  title: string;
  description: string;
  common_interest_count: number;
};

async function addClubCategories(clubId: number, categories: number[]) {
  if (!categories.length) return;

  let clubCategories: ClubCategoryWithoutId[] = [];
  categories.forEach((category_id: number) => {
    clubCategories.push({ club_id: clubId, category_id });
  });

  await prisma.clubCategory.createMany({ data: clubCategories });
}

router.post<ClubDetails, void>("/", authenticateToken, async (req, res) => {
  try {
    if (
      !req.body.title ||
      !req.body.description ||
      (!req.body.membership_fee && req.body.membership_fee !== 0) ||
      !req.body.categories ||
      !req.body.position
    ) {
      return res.sendStatus(INVALID_REQUEST_CODE);
    }

    const club = await prisma.club.create({
      data: {
        title: req.body.title,
        description: req.body.description,
        membership_fee: req.body.membership_fee,
        is_approved: false,
      },
    });

    await Promise.all([
      prisma.clubAdmin.create({
        data: {
          club_id: club.id,
          user_id: req.body.user.id,
          position: AdminType.Owner,
        },
      }),
      addClubCategories(club.id, req.body.categories),
      prisma.clubMember.create({
        data: {
          user_id: req.body.user.id,
          club_id: club.id,
          is_approved: true,
        },
      }),
    ]);

    res.sendStatus(OK_CODE);
  } catch (error) {
    res.sendStatus(INTERNAL_ERROR_CODE);
  }
});

router.put<ClubDetails, void>(
  "/:id",
  authenticateToken,
  verifyIsClubAdmin,
  async (req, res) => {
    try {
      if (
        !req.body.title ||
        !req.body.description ||
        (!req.body.membership_fee && req.body.membership_fee !== 0) ||
        !req.body.categories ||
        req.body.categories.length > 5
      ) {
        return res.sendStatus(INVALID_REQUEST_CODE);
      }

      const clubId = Number(req.params.id);

      await prisma.club.update({
        where: {
          id: clubId,
        },
        data: {
          title: req.body.title,
          description: req.body.description,
          membership_fee: req.body.membership_fee,
        },
      });

      await prisma.clubCategory.deleteMany({
        where: { club_id: clubId },
      });

      await addClubCategories(clubId, req.body.categories);

      res.sendStatus(OK_CODE);
    } catch (error) {
      console.log(error);
      res.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

router.delete<ChosenClub, void>(
  "/:id",
  authenticateToken,
  verifyIsClubAdmin,
  async (req, res) => {
    try {
      const clubId = Number(req.params.id);

      await Promise.all([
        prisma.clubCategory.deleteMany({
          where: {
            club_id: clubId,
          },
        }),
        prisma.clubAdmin.deleteMany({
          where: {
            club_id: clubId,
          },
        }),
      ]);

      await prisma.club.delete({
        where: {
          id: clubId,
        },
      });

      res.sendStatus(OK_CODE);
    } catch (error) {
      res.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

router.get("/search", authenticateToken, async (req, res) => {
  const query: string = (req.query.searchQuery ?? "") as string;

  const clubs: Club[] = await prisma.club.findMany({
    where: {
      title: { startsWith: "%" + query, mode: "insensitive" },
    },
    orderBy: { title: "asc" },
  });

  res.status(OK_CODE).json(clubs);
});

router.get("/my-clubs", authenticateToken, async (req, res) => {
  const userId = req.body.user.id;

  const clubs: Club[] = (
    await prisma.clubMember.findMany({
      where: {
        user_id: userId,
      },
      include: { club: true },
      orderBy: { club: { title: "asc" } },
    })
  ).map((x) => x.club);

  res.status(OK_CODE).json(clubs);
});

router.put("/:id/manage-membership", authenticateToken, async (req, res) => {
  const clubId = parseInt(req.params.id, 10);
  const userId = parseInt(req.body.user.id || -1, 10);

  const clubExists = await prisma.club.findUnique({ where: { id: clubId } });
  if (!clubExists) return res.sendStatus(NOT_FOUND_CODE);

  const userMembershipStatus = await prisma.clubMember.findFirst({
    where: { user_id: userId, club_id: clubId },
  });

  const approvalStatus = clubExists.membership_fee <= 0;

  if (!userMembershipStatus) {
    await prisma.clubMember.create({
      data: { user_id: userId, club_id: clubId, is_approved: approvalStatus },
    });
  } else {
    await prisma.clubMember.deleteMany({
      where: { user_id: userId, club_id: clubId },
    });
    await prisma.clubAdmin.deleteMany({
      where: { user_id: userId, club_id: clubId },
    });
  }

  res.sendStatus(OK_CODE);
});

router.get<void, ClubForYouItem[]>(
  "/for-you",
  authenticateToken,
  async (req, res) => {
    const userId = req.body.user.id;

    // This query performs significantly better when using raw sql
    // Prisma has query cleaning by default
    const data = (await prisma.$queryRaw(
      Prisma.sql`SELECT c.id, c.title, c.description, COUNT(cc.category_id) AS common_interest_count
    FROM "public"."Club" c 
    JOIN "public"."ClubCategory" cc ON c.id = cc.club_id 
    JOIN "public"."UserInterest" ui ON cc.category_id = ui.category_id 
    WHERE ui.user_id = ${userId} 
    GROUP BY c.id, c.title 
    ORDER BY COUNT(cc.category_id) DESC
    LIMIT 15;`
    )) as ClubForYouItem[];

    data.map((entry) => {
      entry.common_interest_count = Number(entry.common_interest_count);
    });

    res.status(OK_CODE).json(data);
  }
);

router.get("/:id", authenticateToken, async (req, res) => {
  const clubId = parseInt(req.params.id, 10);
  const club = await prisma.club.findUnique({
    where: { id: clubId },
    include: {
      admins: true,
      club_members: true,
      events: true,
      categories: {
        select: {
          category: {
            select: {
              id: true,
              type: true,
              name: true,
            },
          },
        },
      },
    },
  });

  if (!club) return res.sendStatus(NOT_FOUND_CODE);

  const isJoined = club?.club_members.some(
    (x: ClubMember) =>
      x.club_id === clubId && x.user_id === req.body.user.id && x.is_approved
  );

  const isJoinPending = club?.club_members.some(
    (x: ClubMember) =>
      x.club_id === clubId && x.user_id === req.body.user.id && !x.is_approved
  );

  const categories = club.categories.map((x) => x.category);

  club.categories = [];
  res.status(OK_CODE).json({
    id: club.id,
    title: club.title,
    description: club.description,
    membershipFee: club.membership_fee,
    events: club.events.filter((x) => {
      return isJoined || !x.private_flag;
    }),
    members: club.club_members.map((x) => {
      return { userId: x.user_id, isApproved: x.is_approved };
    }),
    isClubAdmin: club.admins.some((x) => x.user_id === req.body.user.id),
    adminIds: club.admins.map((x) => x.user_id),
    categories: categories,
    isJoined,
    isJoinPending,
    isCreator: club.admins.some(
      (x) =>
        x.club_id === clubId &&
        x.user_id === req.body.user.id &&
        x.position === AdminType.Owner
    ),
  });
});

export default router;
