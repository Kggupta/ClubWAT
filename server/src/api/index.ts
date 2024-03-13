import express from "express";

import MessageResponse from "../interfaces/MessageResponse";
import userRoutes from "./UserRoutes";
import clubRoutes from "./ClubRoutes";
import eventRoutes from "./EventRoutes";
import clubEventRoutes from "./ClubEventRoutes";
import categoryRoutes from "./CategoryRoutes";
import clubAdminRoutes from "./ClubAdminRoutes";
import clubDiscussion from "./ClubDiscussionRoutes";
import shareRoutes from "./ShareRoutes";
import friendRoutes from "./FriendRoutes";
import spotlightRoutes from "./SpotlightRoutes";

const router = express.Router();

router.get<{}, MessageResponse>("/", (req, res) => {
  res.json({
    message: "API - 👋🌎🌍🌏",
  });
});

router.use("/user", userRoutes);
router.use("/club", clubRoutes);
router.use("/club/:id/event", clubEventRoutes);
router.use("/club/admin", clubAdminRoutes);
router.use("/category", categoryRoutes);
router.use("/club/discussion", clubDiscussion);
router.use("/event", eventRoutes);
router.use("/share", shareRoutes);
router.use("/friend", friendRoutes);
router.use("/spotlight", spotlightRoutes);

export default router;
