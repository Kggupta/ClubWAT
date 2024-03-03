import nodemailer from "nodemailer";

const transporter = nodemailer.createTransport({
  service: "gmail",
  host: "smtp.gmail.com",
  port: 587,
  secure: false,
  auth: {
    user: process.env.EMAIL,
    pass: process.env.EMAIL_APP_PASSWORD,
  },
});

export default async function sendEmail(email: string, code: number) {
  await transporter.sendMail({
    from: {
      name: "ClubWAT",
      address: process.env.EMAIL as string,
    },
    to: email,
    subject: "Email Verification Code",
    text: `Your verification code is: ${code.toString()}`,
  });
  console.log(`CODE SENT TO ${email}: ${code}`);
}
