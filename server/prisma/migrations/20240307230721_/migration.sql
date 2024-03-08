-- AlterTable
ALTER TABLE "Event" ADD COLUMN     "location" TEXT NOT NULL DEFAULT 'Remote',
ADD COLUMN     "private_flag" BOOLEAN NOT NULL DEFAULT false;
