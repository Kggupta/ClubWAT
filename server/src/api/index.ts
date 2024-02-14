import express from "express";

import MessageResponse from "../interfaces/MessageResponse";
import userRoutes from "./UserRoutes";
import clubRoutes from './ClubRoutes';
import clubCategoryRoutes from './ClubCategoryRoutes';
import adminRoutes from './AdminRoutes';

const router = express.Router();

router.get<{}, MessageResponse>("/", (req, res) => {
  res.json({
    message: "API - 👋🌎🌍🌏",
  });
});

router.use("/user", userRoutes);
router.use("/club", clubRoutes);
router.use("/club/admin", adminRoutes);
router.use("/category", clubCategoryRoutes);


export default router;
