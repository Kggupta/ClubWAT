import express from "express";
import { prisma } from "../lib/prisma";
import {
  Category,
  Club,
  ClubAdmin,
  ClubCategory,
  ClubMember,
} from "@prisma/client";
import {
  INTERNAL_ERROR_CODE,
  INVALID_REQUEST_CODE,
  NOT_FOUND_CODE,
  OK_CODE,
} from "../lib/StatusCodes";
import { authenticateToken, verifyIsClubAdmin } from "../middlewares";

const router = express.Router();

interface ClubWithCategories extends Club {
  categories?: ClubCategory[];
}

interface ClubDetails extends Club {
  categories: number[];
  position?: string;
}

type ClubCategoryWithoutId = {
  club_id: number;
  category_id: number;
};

type ClubResponse = {
  data: ClubWithCategories[];
};

type ChosenClub = {
  id: string;
};

type ClubSearch = {
  searchQuery?: string;
};

type ClubAdminResponse = {
  data: ClubAdmin[];
};

type ClubParam = {
  param: string;
};

type includeQuery = {
  categories: {
    select: {
      category: {
        select: {
          id: true;
          type: true;
          name: true;
        };
      };
    };
  };
};

async function addClubCategories(clubId: number, categories: number[]) {
  if (!categories.length) return;

  let clubCategories: ClubCategoryWithoutId[] = [];
  categories.forEach((category_id: number) => {
    clubCategories.push({ club_id: clubId, category_id });
  });

  await prisma.clubCategory.createMany({ data: clubCategories });
}

router.get<ClubParam, ClubResponse>(
  "/status/:param?",
  authenticateToken,
  async (req, res) => {
    try {
      const param = req.params.param;
      let query: {
        where?: { id?: number; is_approved?: boolean };
        include?: includeQuery;
      } = {};

      if (req.query.withCategories === "true") {
        query.include = {
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
        };
      }

      if (param === "approved" || param === "not-approved") {
        query.where = { is_approved: param === "approved" };
        let clubs: ClubWithCategories[] = await prisma.club.findMany(query);

        return res.json({ data: clubs }).status(OK_CODE);
      } else if (param && !isNaN(Number(param))) {
        const clubId = Number(param);
        query.where = { id: clubId };
        const club: Club | null = await prisma.club.findFirst(query);

        if (club == null) {
          return res.sendStatus(NOT_FOUND_CODE);
        }

        return res.json({ data: [club] }).status(OK_CODE);
      } else if (param) {
        return res.sendStatus(INVALID_REQUEST_CODE);
      } else {
        let clubs: ClubWithCategories[] = await prisma.club.findMany(query);
        return res.json({ data: clubs }).status(OK_CODE);
      }
    } catch (error) {
      res.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

router.get<ChosenClub, ClubAdminResponse>(
  "/:id/admins",
  authenticateToken,
  async (req, res) => {
    try {
      const param = req.params.id;
      const clubId = Number(param);
      const admins: ClubAdmin[] = await prisma.clubAdmin.findMany({
        where: {
          club_id: clubId,
        },
        include: {
          user: true,
        },
      });

      return res.json({ data: admins }).status(OK_CODE);
    } catch (error) {
      res.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

router.post<ClubDetails, void>("/", authenticateToken, async (req, res) => {
  try {
    if (
      !req.body.title ||
      !req.body.description ||
      !req.body.membership_fee ||
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
          position: req.body.position,
        },
      }),
      addClubCategories(club.id, req.body.categories),
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
        !req.body.membership_fee ||
        !req.body.categories ||
        !req.body.is_approved
      ) {
        return res.sendStatus(INVALID_REQUEST_CODE);
      }

      const clubId = Number(req.params.id);

      Promise.all([
        prisma.club.update({
          where: {
            id: clubId,
          },
          data: {
            title: req.body.title,
            description: req.body.description,
            membership_fee: req.body.membership_fee,
            is_approved: req.body.is_approved,
          },
        }),
        (async () => {
          await prisma.clubCategory.deleteMany({
            where: { club_id: clubId },
          });
          addClubCategories(clubId, req.body.categories);
        })(),
      ]);

      res.sendStatus(OK_CODE);
    } catch (error) {
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
  }

  res.sendStatus(OK_CODE);
});

router.get("/:id", authenticateToken, async (req, res) => {
  const clubId = parseInt(req.params.id, 10);
  const club = await prisma.club.findUnique({
    where: { id: clubId },
    include: {
      admins: true,
      club_members: true,
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
    members: club.club_members.map((x) => {
      return { userId: x.user_id, isApproved: x.is_approved };
    }),
    adminIds: club.admins.map((x) => x.user_id),
    categories: categories,
    isJoined,
    isJoinPending,
  });
});

export default router;
