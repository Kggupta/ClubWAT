/*
  Warnings:

  - You are about to drop the column `created_by_user_id` on the `Club` table. All the data in the column will be lost.
  - The `position` column on the `ClubAdmin` table would be dropped and recreated. This will lead to data loss if there is data in the column.

*/
-- CreateEnum
CREATE TYPE "AdminType" AS ENUM ('Owner', 'Admin');

-- DropForeignKey
ALTER TABLE "Club" DROP CONSTRAINT "Club_created_by_user_id_fkey";

-- AlterTable
ALTER TABLE "Club" DROP COLUMN "created_by_user_id";

-- AlterTable
ALTER TABLE "ClubAdmin" DROP COLUMN "position",
ADD COLUMN     "position" "AdminType" NOT NULL DEFAULT 'Owner';

-- AlterTable
ALTER TABLE "Notification" ALTER COLUMN "create_date" SET DEFAULT NOW();
