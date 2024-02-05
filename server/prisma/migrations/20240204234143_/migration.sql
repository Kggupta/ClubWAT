-- CreateTable
CREATE TABLE "UserEmailVerification" (
    "id" SERIAL NOT NULL,
    "email" TEXT NOT NULL,
    "code" INTEGER NOT NULL,

    CONSTRAINT "UserEmailVerification_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "UserEmailVerification_email_key" ON "UserEmailVerification"("email");
