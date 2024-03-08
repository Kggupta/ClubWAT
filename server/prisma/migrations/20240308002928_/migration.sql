-- CreateTable
CREATE TABLE "Notification" (
    "id" SERIAL NOT NULL,
    "destination_user_id" INTEGER NOT NULL,
    "source_user_id" INTEGER NOT NULL,
    "club_id" INTEGER,
    "event_id" INTEGER,
    "content" TEXT NOT NULL,

    CONSTRAINT "Notification_pkey" PRIMARY KEY ("id")
);

-- AddForeignKey
ALTER TABLE "Notification" ADD CONSTRAINT "Notification_source_user_id_fkey" FOREIGN KEY ("source_user_id") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Notification" ADD CONSTRAINT "Notification_destination_user_id_fkey" FOREIGN KEY ("destination_user_id") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Notification" ADD CONSTRAINT "Notification_club_id_fkey" FOREIGN KEY ("club_id") REFERENCES "Club"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Notification" ADD CONSTRAINT "Notification_event_id_fkey" FOREIGN KEY ("event_id") REFERENCES "Event"("id") ON DELETE SET NULL ON UPDATE CASCADE;
