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

type UserInterestWithoutId = {
  user_id: number,
  category_id: number
}

interface UserInterestExtension extends UserInterest {
  category: Category
}

router.get<void, InterestsResponse>("/all", authenticateToken, async (req, res) => {
  try {
    const categories: Category[] = await prisma.category.findMany();

    const faculties: Category[] = categories.filter(category => category.type === 'faculty');
    const ethnicities: Category[] = categories.filter(category => category.type === 'ethnicity');
    const religions: Category[] = categories.filter(category => category.type === 'religion');
    const programs: Category[] = categories.filter(category => category.type === 'program');
    const hobbies: Category[] = categories.filter(category => category.type === 'hobby');

    res.status(OK_CODE).json({faculties, ethnicities, religions, programs, hobbies});
  } catch(error) {
    res.sendStatus(INTERNAL_ERROR_CODE);
  }
});

router.get<void, any[]>("/", authenticateToken, async (req, res) => {
  try {
    const userId = req.body.user.id;

    const interests: UserInterestExtension[] = await prisma.userInterest.findMany({
      where: {
        user_id: userId
      },
      include: {
        category: true
      }
    })
    
    res.status(OK_CODE).json(interests.map(x => x.category));
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

    res.status(OK_CODE);
  } catch(error) {
    res.sendStatus(INTERNAL_ERROR_CODE);
  }
});

export default router;
