-- CreateTable
CREATE TABLE "ClubDiscussion" (
    "id" SERIAL NOT NULL,
    "user_id" INTEGER NOT NULL,
    "club_id" INTEGER NOT NULL,
    "message" TEXT NOT NULL,
    "create_date" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "ClubDiscussion_pkey" PRIMARY KEY ("id")
);

-- AddForeignKey
ALTER TABLE "ClubDiscussion" ADD CONSTRAINT "ClubDiscussion_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "ClubDiscussion" ADD CONSTRAINT "ClubDiscussion_club_id_fkey" FOREIGN KEY ("club_id") REFERENCES "Club"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
