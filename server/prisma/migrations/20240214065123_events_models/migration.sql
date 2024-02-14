-- CreateTable
CREATE TABLE "Events" (
    "id" SERIAL NOT NULL,
    "title" TEXT NOT NULL,
    "description" TEXT NOT NULL,
    "start_date" TIMESTAMP(3) NOT NULL,
    "end_date" TIMESTAMP(3) NOT NULL,
    "club_id" INTEGER NOT NULL,

    CONSTRAINT "Events_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "AttendingEvents" (
    "id" SERIAL NOT NULL,
    "user_email" TEXT NOT NULL,
    "event_id" INTEGER NOT NULL,

    CONSTRAINT "AttendingEvents_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "BookmarkedEvents" (
    "id" SERIAL NOT NULL,
    "user_id" INTEGER NOT NULL,
    "event_id" INTEGER NOT NULL,

    CONSTRAINT "BookmarkedEvents_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "FeedbackEvents" (
    "id" SERIAL NOT NULL,
    "event_id" INTEGER NOT NULL,
    "feedback" TEXT NOT NULL,
    "created" TIMESTAMP(3) NOT NULL,
    "user_email" TEXT NOT NULL,

    CONSTRAINT "FeedbackEvents_pkey" PRIMARY KEY ("id")
);

-- AddForeignKey
ALTER TABLE "Events" ADD CONSTRAINT "Events_club_id_fkey" FOREIGN KEY ("club_id") REFERENCES "Club"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "AttendingEvents" ADD CONSTRAINT "AttendingEvents_user_email_fkey" FOREIGN KEY ("user_email") REFERENCES "User"("email") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "AttendingEvents" ADD CONSTRAINT "AttendingEvents_event_id_fkey" FOREIGN KEY ("event_id") REFERENCES "Events"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "BookmarkedEvents" ADD CONSTRAINT "BookmarkedEvents_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "BookmarkedEvents" ADD CONSTRAINT "BookmarkedEvents_event_id_fkey" FOREIGN KEY ("event_id") REFERENCES "Events"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "FeedbackEvents" ADD CONSTRAINT "FeedbackEvents_user_email_fkey" FOREIGN KEY ("user_email") REFERENCES "User"("email") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "FeedbackEvents" ADD CONSTRAINT "FeedbackEvents_event_id_fkey" FOREIGN KEY ("event_id") REFERENCES "Events"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
