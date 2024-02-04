import express from "express";

import MessageResponse from "../interfaces/MessageResponse";
import userRoutes from "./UserRoutes";

const router = express.Router();

router.get<{}, MessageResponse>("/", (req, res) => {
  res.json({
    message: "API - 👋🌎🌍🌏",
  });
});

router.use("/user", userRoutes);

export default router;
