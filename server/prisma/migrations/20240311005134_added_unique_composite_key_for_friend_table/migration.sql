/*
  Warnings:

  - A unique constraint covering the columns `[source_friend_id,destination_friend_id]` on the table `Friend` will be added. If there are existing duplicate values, this will fail.

*/
-- AlterTable
ALTER TABLE "Notification" ALTER COLUMN "create_date" SET DEFAULT NOW();

-- CreateIndex
CREATE UNIQUE INDEX "Friend_source_friend_id_destination_friend_id_key" ON "Friend"("source_friend_id", "destination_friend_id");
