import bcrypt from "bcrypt";

export default class PasswordHashingService {
  constructor() {}

  static hashPassword(password: string): string {
    if (!process.env.SALT_EXPONENT)
      throw new Error("MISSING SALT_EXPONENT ENVIRONMENT VARIABLE.");

    // The salt will be > 32 bytes, 2^10 rounds
    const salt = bcrypt.genSaltSync(parseInt(process.env.SALT_EXPONENT, 10));

    return bcrypt.hashSync(password, salt);
  }

  static compareHash(password: string, hashedPassword: string): boolean {
    return bcrypt.compareSync(password, hashedPassword);
  }
}
