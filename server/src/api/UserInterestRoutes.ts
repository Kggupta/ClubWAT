import express from "express";
import { authenticateToken } from "../middlewares";
import { prisma } from "../lib/prisma";
import { INTERNAL_ERROR_CODE, INVALID_REQUEST_CODE, OK_CODE } from "../lib/StatusCodes";
import { Category, UserInterest } from "@prisma/client";

const router = express.Router();

type InterestsResponse = {
  faculties: Category[],
  ethnicities: Category[],
  religions: Category[],
  programs: Category[],
  hobbies: Category[]
}

type UserInterestsRequest = {
  faculty: number,
  ethnicity: number,
  religion: number,
  program: number,
  hobbies: number[]
}

type UserInterestsResponse = {
  interests: UserInterest[]
}

type UserInterestWithoutId = {
  user_id: number,
  category_id: number
}

router.get<void, InterestsResponse>("/all", authenticateToken, async (req, res) => {
  try {
    const promises = [
      prisma.category.findMany({
        where: {type: 'faculty'}
      }),
      prisma.category.findMany({
        where: {type: 'ethnicity'}
      }),
      prisma.category.findMany({
        where: {type: 'religion'}
      }),
      prisma.category.findMany({
        where: {type: 'program'}
      }),
      prisma.category.findMany({
        where: {type: 'hobby'}
      })
    ];

    const [faculties, ethnicities, religions, programs, hobbies] = await Promise.all(promises);

    res.status(OK_CODE).json({faculties, ethnicities, religions, programs, hobbies});
  } catch(error) {
    res.sendStatus(INTERNAL_ERROR_CODE);
  }
});

router.get<void, UserInterestsResponse>("/", authenticateToken, async (req, res) => {
  try {
    const userId = req.body.user.id;

    const interests: UserInterest[] = await prisma.userInterest.findMany({
      where: {
        user_id: userId
      },
      include: {
        category: true
      }
    })

    res.status(OK_CODE).json({interests});
  } catch(error) {
    res.sendStatus(INTERNAL_ERROR_CODE);
  }
});

router.put<UserInterestsRequest, void>("/", authenticateToken, async (req, res) => {
  try {

    if (!req.body.faculty || !req.body.ethnicity || !req.body.religion
          || !req.body.program || !req.body.hobbies) {
      return res.sendStatus(INVALID_REQUEST_CODE);
    }

    const userId = req.body.user.id;

    await prisma.userInterest.deleteMany({
      where: {
        user_id: userId
      }
    })

    let hobbyQueries: UserInterestWithoutId[] = []
    
    req.body.hobbies.forEach((hobbyId: number) => {
      hobbyQueries.push({
        user_id: userId,
        category_id: hobbyId
      })
    })

    await prisma.userInterest.createMany({
      data: [
        {
          user_id: userId,
          category_id: req.body.faculty
        },
        {
          user_id: userId,
          category_id: req.body.ethnicity
        },
        {
          user_id: userId,
          category_id: req.body.religion
        },
        {
          user_id: userId,
          category_id: req.body.program
        },
        ...hobbyQueries
      ]
    })

    res.status(OK_CODE).json();
  } catch(error) {
    res.sendStatus(INTERNAL_ERROR_CODE);
  }
});

export default router;
