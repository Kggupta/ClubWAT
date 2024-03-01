/*
  Warnings:

  - You are about to drop the `AttendingEvents` table. If the table is not empty, all the data it contains will be lost.
  - You are about to drop the `BookmarkedEvents` table. If the table is not empty, all the data it contains will be lost.
  - You are about to drop the `Events` table. If the table is not empty, all the data it contains will be lost.
  - You are about to drop the `FeedbackEvents` table. If the table is not empty, all the data it contains will be lost.

*/
-- DropForeignKey
ALTER TABLE "AttendingEvents" DROP CONSTRAINT "AttendingEvents_event_id_fkey";

-- DropForeignKey
ALTER TABLE "AttendingEvents" DROP CONSTRAINT "AttendingEvents_user_email_fkey";

-- DropForeignKey
ALTER TABLE "BookmarkedEvents" DROP CONSTRAINT "BookmarkedEvents_event_id_fkey";

-- DropForeignKey
ALTER TABLE "BookmarkedEvents" DROP CONSTRAINT "BookmarkedEvents_user_id_fkey";

-- DropForeignKey
ALTER TABLE "Events" DROP CONSTRAINT "Events_club_id_fkey";

-- DropForeignKey
ALTER TABLE "FeedbackEvents" DROP CONSTRAINT "FeedbackEvents_event_id_fkey";

-- DropForeignKey
ALTER TABLE "FeedbackEvents" DROP CONSTRAINT "FeedbackEvents_user_email_fkey";

-- DropTable
DROP TABLE "AttendingEvents";

-- DropTable
DROP TABLE "BookmarkedEvents";

-- DropTable
DROP TABLE "Events";

-- DropTable
DROP TABLE "FeedbackEvents";

-- CreateTable
CREATE TABLE "Event" (
    "id" SERIAL NOT NULL,
    "title" TEXT NOT NULL,
    "description" TEXT NOT NULL,
    "start_date" TIMESTAMP(3) NOT NULL,
    "end_date" TIMESTAMP(3) NOT NULL,
    "club_id" INTEGER NOT NULL,

    CONSTRAINT "Event_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "EventAttendance" (
    "id" SERIAL NOT NULL,
    "user_id" INTEGER NOT NULL,
    "event_id" INTEGER NOT NULL,

    CONSTRAINT "EventAttendance_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "EventBookmark" (
    "id" SERIAL NOT NULL,
    "user_id" INTEGER NOT NULL,
    "event_id" INTEGER NOT NULL,

    CONSTRAINT "EventBookmark_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "EventFeedback" (
    "id" SERIAL NOT NULL,
    "event_id" INTEGER NOT NULL,
    "feedback" TEXT NOT NULL,
    "created" TIMESTAMP(3) NOT NULL,
    "user_email" TEXT NOT NULL,

    CONSTRAINT "EventFeedback_pkey" PRIMARY KEY ("id")
);

-- AddForeignKey
ALTER TABLE "Event" ADD CONSTRAINT "Event_club_id_fkey" FOREIGN KEY ("club_id") REFERENCES "Club"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "EventAttendance" ADD CONSTRAINT "EventAttendance_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "EventAttendance" ADD CONSTRAINT "EventAttendance_event_id_fkey" FOREIGN KEY ("event_id") REFERENCES "Event"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "EventBookmark" ADD CONSTRAINT "EventBookmark_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "EventBookmark" ADD CONSTRAINT "EventBookmark_event_id_fkey" FOREIGN KEY ("event_id") REFERENCES "Event"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "EventFeedback" ADD CONSTRAINT "EventFeedback_user_email_fkey" FOREIGN KEY ("user_email") REFERENCES "User"("email") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "EventFeedback" ADD CONSTRAINT "EventFeedback_event_id_fkey" FOREIGN KEY ("event_id") REFERENCES "Event"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
