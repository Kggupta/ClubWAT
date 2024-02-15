import express from "express";

import MessageResponse from "../interfaces/MessageResponse";
import userRoutes from "./UserRoutes";
import clubRoutes from "./ClubRoutes";
import categoryRoutes from "./CategoryRoutes";
import adminRoutes from "./AdminRoutes";
import clubDiscussion from "./ClubDiscussionRoutes";

const router = express.Router();

router.get<{}, MessageResponse>("/", (req, res) => {
  res.json({
    message: "API - 👋🌎🌍🌏",
  });
});

router.use("/user", userRoutes);
router.use("/club", clubRoutes);
router.use("/club/admin", adminRoutes);
router.use("/category", categoryRoutes);
router.use("/club/discussion", clubDiscussion);

export default router;
