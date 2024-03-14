/*
  Warnings:

  - You are about to drop the column `spotlight_id` on the `UserReadSpotlight` table. All the data in the column will be lost.
  - You are about to drop the `Spotlight` table. If the table is not empty, all the data it contains will be lost.
  - Added the required column `event_id` to the `UserReadSpotlight` table without a default value. This is not possible if the table is not empty.

*/
-- DropForeignKey
ALTER TABLE "UserReadSpotlight" DROP CONSTRAINT "UserReadSpotlight_spotlight_id_fkey";

-- AlterTable
ALTER TABLE "UserReadSpotlight" DROP COLUMN "spotlight_id",
ADD COLUMN     "event_id" INTEGER NOT NULL;

-- DropTable
DROP TABLE "Spotlight";

-- AddForeignKey
ALTER TABLE "UserReadSpotlight" ADD CONSTRAINT "UserReadSpotlight_event_id_fkey" FOREIGN KEY ("event_id") REFERENCES "Event"("id") ON DELETE CASCADE ON UPDATE CASCADE;
