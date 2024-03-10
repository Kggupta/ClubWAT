-- AlterTable
ALTER TABLE "Notification" ALTER COLUMN "create_date" SET DEFAULT NOW();

-- CreateTable
CREATE TABLE "Friend" (
    "id" SERIAL NOT NULL,
    "source_friend_id" INTEGER NOT NULL,
    "destination_friend_id" INTEGER NOT NULL,

    CONSTRAINT "Friend_pkey" PRIMARY KEY ("id")
);

-- AddForeignKey
ALTER TABLE "Friend" ADD CONSTRAINT "Friend_source_friend_id_fkey" FOREIGN KEY ("source_friend_id") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Friend" ADD CONSTRAINT "Friend_destination_friend_id_fkey" FOREIGN KEY ("destination_friend_id") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
