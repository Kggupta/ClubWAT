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

  private static async sendBccEmails(
    emails: string[],
    content: string,
    title: string
  ) {
    if (emails.length === 0) return;
    await transporter.sendMail({
      from: {
        name: "ClubWAT",
        address: process.env.EMAIL as string,
      },
      bcc: emails,
      subject: title,
      text: content,
    });
  }

  static async sendDownloadDataEmail(user: User) {
    await transporter.sendMail({
      from: {
        name: "ClubWAT",
        address: process.env.EMAIL as string,
      },
      to: user.email,
      subject: "Downloaded Data",
      text: "Attached is your user data file.",
      attachments: [{ filename: "Data.json", content: JSON.stringify(user) }],
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

  static async sendClubJoinRequestApprovedEmail(email: string, club: Club) {
    await this.sendEmail(
      email,
      `Your request to join ${club.title} was accepted!\n\nCheck it out in your 'My Clubs' list on ClubWAT!`,
      "Club Join Request Approved"
    );
  }

  static async newEventEmail(emails: string[], event: Event, club: Club) {
    await this.sendBccEmails(
      emails,
      `${club.title} has an upcoming event: ${event.title}!\n\nCheck out your inbox on ClubWAT to learn more!\n\nYou're getting this email because you're a member of this club.`,
      "Upcoming Event!"
    );
  }
}
