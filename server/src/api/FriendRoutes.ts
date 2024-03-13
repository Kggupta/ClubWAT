import express from "express";
import { authenticateToken } from "../middlewares";
import { prisma } from "../lib/prisma";
import {
  OK_CODE,
  INTERNAL_ERROR_CODE,
  NOT_FOUND_CODE,
  INVALID_REQUEST_CODE,
} from "../lib/StatusCodes";

const router = express.Router();

router.get("/", authenticateToken, async (req, res) => {
  const userId = req.body.user.id;

  if (!userId) {
    return res.sendStatus(INVALID_REQUEST_CODE);
  }

  try {
    const friends = await prisma.friend.findMany({
      where: {
        OR: [{ source_friend_id: userId }, { destination_friend_id: userId }],
        is_accepted: true,
      },
      include: { source_friend: true, destination_friend: true },
    });

    let friendList = friends
      .map((x) => x.source_friend)
      .concat(friends.map((x) => x.destination_friend))
      .filter((x) => x.id !== userId);

    return res.status(OK_CODE).json(friendList);
  } catch (error) {
    return res.sendStatus(INTERNAL_ERROR_CODE);
  }
});

router.get("/requests", authenticateToken, async (req, res) => {
  const userId = req.body.user.id;

  if (!userId) {
    return res.sendStatus(INVALID_REQUEST_CODE);
  }

  try {
    const friends = await prisma.friend.findMany({
      where: {
        destination_friend_id: userId,
        is_accepted: false,
      },
      include: { source_friend: true, destination_friend: true },
    });

    let friendList = friends
      .map((x) => x.source_friend)
      .concat(friends.map((x) => x.destination_friend))
      .filter((x) => x.id !== userId);

    return res.status(OK_CODE).json(friendList);
  } catch (error) {
    return res.sendStatus(INTERNAL_ERROR_CODE);
  }
});

router.post("/", authenticateToken, async (req, res) => {
  const userId = req.body.user.id;
  const friendEmail = req.body.email;
  const friend = await prisma.user.findFirst({
    where: { email: friendEmail },
  });

  if (!friend) {
    return res.sendStatus(NOT_FOUND_CODE);
  }

  if (!userId || !friendEmail || userId === friend.id) {
    return res.sendStatus(INVALID_REQUEST_CODE);
  }

  const friendId = friend?.id;

  // check if a friend relationship already exists
  const existingFriend = await prisma.friend.findFirst({
    where: {
      OR: [
        {
          source_friend_id: userId,
          destination_friend_id: friendId,
        },
        {
          source_friend_id: friendId,
          destination_friend_id: userId,
        },
      ],
    },
  });

  if (existingFriend) {
    return res.sendStatus(INVALID_REQUEST_CODE);
  }

  try {
    await prisma.friend.create({
      data: {
        source_friend_id: userId,
        destination_friend_id: friendId,
        is_accepted: false,
      },
    });
    return res.sendStatus(OK_CODE);
  } catch (error) {
    return res.sendStatus(INTERNAL_ERROR_CODE);
  }
});

router.put("/approve-request", authenticateToken, async (req, res) => {
  const userId = req.body.user.id;
  const friendId = req.body.friend_id;

  if (!userId || !friendId || userId === friendId) {
    return res.sendStatus(INVALID_REQUEST_CODE);
  }

  try {
    await prisma.friend.update({
      where: {
        source_friend_id_destination_friend_id: {
          source_friend_id: userId,
          destination_friend_id: friendId,
        },
      },
      data: {
        is_accepted: true,
      },
    });
  } catch (error) {
    return res.sendStatus(INTERNAL_ERROR_CODE);
  }
  return res.sendStatus(OK_CODE);
});

router.delete("/:id", authenticateToken, async (req, res) => {
  const friendId = Number(req.params.id);

  if (!friendId) {
    return res.sendStatus(INVALID_REQUEST_CODE);
  }
  await prisma.friend.delete({
    where: {
      source_friend_id_destination_friend_id: {
        source_friend_id: req.body.user.id,
        destination_friend_id: friendId,
      },
    },
  });

  return res.sendStatus(OK_CODE);
});

export default router;
