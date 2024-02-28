import express from "express";

import MessageResponse from "../interfaces/MessageResponse";
import userRoutes from "./UserRoutes";
import clubRoutes from "./ClubRoutes";
import eventRoutes from "./EventRoutes";
import categoryRoutes from "./CategoryRoutes";
import clubAdminRoutes from "./ClubAdminRoutes";
import clubDiscussion from "./ClubDiscussionRoutes";

const router = express.Router();

router.get<{}, MessageResponse>("/", (req, res) => {
  res.json({
    message: "API - 👋🌎🌍🌏",
  });
});

router.use("/user", userRoutes);
router.use("/club", clubRoutes);
router.use("/club/:id/event", eventRoutes);
router.use("/club/admin", clubAdminRoutes);
router.use("/category", categoryRoutes);
router.use("/club/discussion", clubDiscussion);

export default router;
