import express from "express";
import { authenticateToken } from "../middlewares";
import { prisma } from "../lib/prisma";
import { OK_CODE } from "../lib/StatusCodes";

const router = express.Router();

router.get("", authenticateToken, async (req, res) => {
  const userId = req.body.user.id;

  const friends = await prisma.friend.findMany({
    where: {
      OR: [{ source_friend_id: userId }, { destination_friend_id: userId }],
    },
    include: { source_friend: true, destination_friend: true },
  });

  let friendList = friends
    .map((x) => x.source_friend)
    .concat(friends.map((x) => x.destination_friend))
    .filter((x) => x.id !== userId);

  res.status(OK_CODE).json(friendList);
});

export default router;
