/*
  Warnings:

  - You are about to drop the `EventFeedback` table. If the table is not empty, all the data it contains will be lost.

*/
-- DropForeignKey
ALTER TABLE "EventFeedback" DROP CONSTRAINT "EventFeedback_event_id_fkey";

-- DropForeignKey
ALTER TABLE "EventFeedback" DROP CONSTRAINT "EventFeedback_user_email_fkey";

-- DropTable
DROP TABLE "EventFeedback";

-- CreateTable
CREATE TABLE "EventLike" (
    "id" SERIAL NOT NULL,
    "user_id" INTEGER NOT NULL,
    "event_id" INTEGER NOT NULL,

    CONSTRAINT "EventLike_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "ClubLike" (
    "id" SERIAL NOT NULL,
    "user_id" INTEGER NOT NULL,
    "club_id" INTEGER NOT NULL,

    CONSTRAINT "ClubLike_pkey" PRIMARY KEY ("id")
);

-- AddForeignKey
ALTER TABLE "EventLike" ADD CONSTRAINT "EventLike_event_id_fkey" FOREIGN KEY ("event_id") REFERENCES "Event"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "EventLike" ADD CONSTRAINT "EventLike_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "ClubLike" ADD CONSTRAINT "ClubLike_club_id_fkey" FOREIGN KEY ("club_id") REFERENCES "Club"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "ClubLike" ADD CONSTRAINT "ClubLike_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;
