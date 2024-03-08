-- AlterTable
ALTER TABLE "Notification" ADD COLUMN     "create_date" TIMESTAMP(3) NOT NULL DEFAULT NOW();
