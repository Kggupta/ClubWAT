-- AlterTable
ALTER TABLE "Club" ALTER COLUMN "created_by_user_id" DROP DEFAULT;

-- AlterTable
ALTER TABLE "Notification" ALTER COLUMN "create_date" SET DEFAULT NOW();
