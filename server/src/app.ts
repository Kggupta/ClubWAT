import express from "express";
import morgan from "morgan";
import helmet from "helmet";
import cors from "cors";

import * as middlewares from "./middlewares";
import api from "./api";

require("dotenv").config();

const app = express();

if (!process.env.WUSA_CLUB_ID) {
  throw new Error("WUSA_CLUB_ID .env is missing");
}

app.use(morgan("dev"));
app.use(helmet());
app.use(cors());
app.use(express.json());

app.use("/api/v1", api);

app.use(middlewares.resourceNotFound);
app.use(middlewares.errorHandler);
app.use(middlewares.authenticateToken);
export default app;
