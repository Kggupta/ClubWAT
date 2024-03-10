import { Club, Event, User } from "@prisma/client";
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

export default class EmailService {
  private static async sendEmail(
    email: string,
    content: string,
    title: string
  ) {
    await transporter.sendMail({
      from: {
        name: "ClubWAT",
        address: process.env.EMAIL as string,
      },
      to: email,
      subject: title,
      text: content,
    });
  }

  static async sendVerificationEmail(email: string, code: number) {
    await this.sendEmail(
      email,
      `Your verification code is: ${code.toString()}`,
      "Email Verification Code"
    );
    console.log(`CODE SENT TO ${email}: ${code}`);
  }

  static async sendEventShareEmail(email: string, event: Event, source: User) {
    await this.sendEmail(
      email,
      `${source.first_name} ${source.last_name} shared ${event.title}, with you!\n\nCheck out your inbox on ClubWAT to learn more!`,
      "Event Shared With You"
    );
    console.log(`EVENT SENT TO ${email} : ${event.title}`);
  }

  static async sendClubShareEmail(email: string, club: Club, source: User) {
    await this.sendEmail(
      email,
      `${source.first_name} ${source.last_name} shared ${club.title}, with you!\n\nCheck out your inbox on ClubWAT to learn more!`,
      "Club Shared With You"
    );
    console.log(`CLUB SENT TO ${email} : ${club.title}`);
  }
}
