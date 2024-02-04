import { prisma } from "./lib/prisma.js";

await prisma.user.create({
  data: {
    email: "test@uwaterloo.ca",
    first_name: "test",
    last_name: "test",
    admin_flag: false,
    password: "xyz",
  },
});

console.log(
  await prisma.user.findFirstOrThrow({ where: { email: "test@uwaterloo.ca" } })
);

await prisma.user.delete({ where: { email: "test@uwaterloo.ca" } });
