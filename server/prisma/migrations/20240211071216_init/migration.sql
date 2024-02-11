-- CreateTable
CREATE TABLE "ClubAdmin" (
    "id" SERIAL NOT NULL,
    "admin_id" INTEGER NOT NULL,
    "club_id" INTEGER NOT NULL,
    "position" TEXT NOT NULL,

    CONSTRAINT "ClubAdmin_pkey" PRIMARY KEY ("id")
);

-- AddForeignKey
ALTER TABLE "ClubAdmin" ADD CONSTRAINT "ClubAdmin_admin_id_fkey" FOREIGN KEY ("admin_id") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "ClubAdmin" ADD CONSTRAINT "ClubAdmin_club_id_fkey" FOREIGN KEY ("club_id") REFERENCES "Club"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
