-- AlterTable
ALTER TABLE "Friend" ADD COLUMN     "is_accepted" BOOLEAN NOT NULL DEFAULT false;

-- AlterTable
ALTER TABLE "Notification" ALTER COLUMN "create_date" SET DEFAULT NOW();
