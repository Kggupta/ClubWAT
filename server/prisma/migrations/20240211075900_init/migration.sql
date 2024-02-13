/*
  Warnings:

  - You are about to drop the column `admin_id` on the `ClubAdmin` table. All the data in the column will be lost.
  - Added the required column `user_id` to the `ClubAdmin` table without a default value. This is not possible if the table is not empty.

*/
-- DropForeignKey
ALTER TABLE "ClubAdmin" DROP CONSTRAINT "ClubAdmin_admin_id_fkey";

-- AlterTable
ALTER TABLE "ClubAdmin" DROP COLUMN "admin_id",
ADD COLUMN     "user_id" INTEGER NOT NULL;

-- AddForeignKey
ALTER TABLE "ClubAdmin" ADD CONSTRAINT "ClubAdmin_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
