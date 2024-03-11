-- AlterTable
ALTER TABLE "Club" ADD COLUMN     "created_by_user_id" INTEGER NOT NULL DEFAULT 41;

-- AlterTable
ALTER TABLE "Notification" ALTER COLUMN "create_date" SET DEFAULT NOW();

-- AddForeignKey
ALTER TABLE "Club" ADD CONSTRAINT "Club_created_by_user_id_fkey" FOREIGN KEY ("created_by_user_id") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
