# ClubWAT

## General Information
ClubWAT is a mobile application for the University of Waterloo to manage all student clubs and events. This application was coded with Kotlin, TypeScript, and Node.

### Features
1. **User Authentication:** As demonstrated in the demo the user sees the login screen on startup of the application and can login with the email/password. The user can create their account with basic details and receives an email with a six digit code that they enter in the application to complete the registration and verify their identity.
2. **User Interface:** The system uses the popular four tab bottom bar layout that a user can click any tab to go to that view. The tab bar is visible at all times during the user session.
3. **Club and Event Browsing:** Clicking on the search tab in the bottom bar will go to a view that lets you search for clubs or events based on title or description. It sorts results alphabetically and each item is clickable to view more information about the club or event.
4. **Event Bookmarking and Scheduling:** From the event detail view a user can click the bookmark button in the top row of buttons, this will save the event to their home page to ensure they don't miss it. If they click the '+' button, it will mark in the system that the user is attending the event, making it visible in the home page and optionally prompting to add the event to their phone calendar (google calendar for example) automatically so they can get notified about when the event is starting.
5. **Club Registration and Management:** From the club details view, a user can register for a club by clicking the '+' button. If the club doesn't have a membership fee they will instantly get full access to the club. If it does have a membership fee, a club admin can click the settings icon in the top right of the club details view and manage members. This shows a full list of club members and allows the club admin to mark the user as 'Paid' for when they paid their membership fee. It also lets them remove a user from the club or promote them to a club admin. Note only club owners (the user who created the club) can promote users.
6. **Notifications and Alerts:** The application has many options to send emails to users for notifications and alerts. There is also an inbox view by clicking the inbox icon on the top right of the home page view. The user can manage whether they want to receive email notifications through the profile page and clicking edit profile. System events such as a new event being created, a club join request being approved, and sharing an event or club will send an email to the relevant user.
7. **Feedback System:** Users can 'like' or 'unlike' clubs and events. Liking a club/event will add to the like counter of that club/event. This is how users will give feedback on clubs and events.
8. **Personalized Recommendations:** In the bottom navigation bar, the user can click the 'For You' page which will suggest clubs based on the user's interests in a list. The user configures their interests through the profile view and selecting the edit interests option, then entering their relevant interests.
9. **Event Creation and Management:** Club admins can create an event for their club through the club settings page (settings button on the top right corner of the club details view). They enter all the event details and click the submit button. They can also edit an event by going to the event detail view and clicking the edit icon on the top right of the view.
10. **Interactive Club Discussions:** Users can share clubs/events with their friends by clicking the paper airplane icon in the club/event detail view. They manage their friends in the user profile view and clicking 'Manage Friends'. Users can also send messages in the dedicated club group chat if they're an approved member of the club. Club admins can delete any message in the club discussion, but regular members can only delete their own messages.
11. **Security:** Passwords are hashed with 2^11 rounds and 32bytes of salt which is significantly more than the original target of 1000 rounds and 32 bits of salt. Passwords are also required to be strong with more than 8 characters, uppercase, lowercase, and symbol characters.
12. **Privacy:** The user can go to their profile tab and either click the download data button which sends them a file with all of their stored data in the application. They can also scroll down to the 'Danger Zone' and click the 'Delete Account' button to delete all of their data from the application.
13. **Usability:** We used a bottom navigation bar layout in our application, which is visible at all times once the user has logged into the application. Therefore, it will always be possible for the user to get to the For You page within three clicks.
14. **Safety:** We limited the application to only users with a valid **@uwaterloo.ca** email by using the popular code verification technique that's used in many enterprise applications. Since the user needs to enter a code that's sent to their email, and the API requires the email is an **@uwaterloo.ca** domain, the user will never be able to complete the registratiion unless they have access to that **@uwaterloo.ca** email.

### Team

| Name                | Quest ID | Github                              |
| ------------------- | -------- | ----------------------------------- |
| Keshav Gupta        | k44gupta | https://github.com/Kggupta/         |
| Maryam Afshar       | m5afshar | https://github.com/mary1afshar      |
| Anjali Gupta        | a378gupt | https://github.com/anjalig21        |
| Isshana Mohanakumar | imohanak | https://github.com/iissh            |
| Abhinit Patil       | a33patil | https://github.com/abhinit75        |
| Abhinav Gupta       | a363gupt | https://github.com/AbhinavGupta2002 |

## Startup

### Server

1. Initialize node

```
cd server
npm i
```

2. Add a `.env.prod` or `.env.dev`
3. Build

```
npm run build
npx prisma generate
```

4. Run the app

```
npm run prod/dev
```

### Application

1. Gradle Sync and Build
2. Run the application. The app was designed with Pixel 4a, but it works with other models.
