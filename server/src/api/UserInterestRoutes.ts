import express from "express";
import { authenticateToken } from "../middlewares";
import { prisma } from "../lib/prisma";
import { INTERNAL_ERROR_CODE, INVALID_REQUEST_CODE, OK_CODE } from "../lib/StatusCodes";
import { Category } from "@prisma/client";

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

type UserInterestWithoutId = {
  user_id: number,
  category_id: number
}

type UserInterestFiltered = {
  category_id: number,
  category: {
    type: string,
    name: string
  }
}

router.get<void, InterestsResponse>("/all", authenticateToken, async (req, res) => {
  try {
    const categories = await prisma.category.findMany();

    const faculties: Category[] = [];
    const ethnicities: Category[] = [];
    const religions: Category[] = [];
    const programs: Category[] = [];
    const hobbies: Category[] = [];

    categories.forEach(category => {
      switch (category.type) {
        case 'faculty':
          faculties.push(category);
          break;
        case 'ethnicity':
          ethnicities.push(category);
          break;
        case 'religion':
          religions.push(category);
          break;
        case 'program':
          programs.push(category);
          break;
        case 'hobby':
          hobbies.push(category);
          break;
        default:
          break;
      }
    });

    res.status(OK_CODE).json({faculties, ethnicities, religions, programs, hobbies});
  } catch(error) {
    res.sendStatus(INTERNAL_ERROR_CODE);
  }
});

router.get<void, UserInterestFiltered[]>("/", authenticateToken, async (req, res) => {
  try {
    const userId = req.body.user.id;

    const interests: UserInterestFiltered[] = await prisma.userInterest.findMany({
      where: {
        user_id: userId
      },
      select: {
        category_id: true,
        category: {
          select: {
            type: true,
            name: true
          }
        }
      }
    })

    res.status(OK_CODE).json(interests);
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
