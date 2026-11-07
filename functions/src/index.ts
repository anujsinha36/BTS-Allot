/**
 * Import function triggers from their respective submodules:
 *
 * import {onCall} from "firebase-functions/v2/https";
 * import {onDocumentWritten} from "firebase-functions/v2/firestore";
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

import {setGlobalOptions} from "firebase-functions";
import {onSchedule} from "firebase-functions/v2/scheduler";
// import * as logger from "firebase-functions/logger";
import * as admin from "firebase-admin";
export * from "./notifications";

import {DutyTemplate} from "./models";
import {
  getNextMonth,
  getWeekdayNumber,
  getDatesForWeekday,
  formatDate,
} from "./dateUtils";


admin.initializeApp();


setGlobalOptions({maxInstances: 10});

export const createDuties = onSchedule(
  {
    schedule: "0 0 25 * *",
    timeZone: "Asia/Kolkata",
  },
  async () => {
    const db = admin.firestore();
    const nextMonth = getNextMonth();

    const snapshot = await db.collection("templates").get();
    const batch = db.batch();
    let totalDutiesCreated = 0;

    for (const doc of snapshot.docs) {
      const template = doc.data() as DutyTemplate;

      const weekday = getWeekdayNumber(template.dayOfWeek);

      const dates = getDatesForWeekday(
        nextMonth,
        weekday
      );

      for (const date of dates) {
        const dateStr = formatDate(date);
        const meetingNameSlug = template.duty.meetingName.replace(/\s+/g, "_");
        const docId = `${dateStr}_${meetingNameSlug}`;

        const docRef = db.collection("duties").doc(docId);

        const duty = {
          id: docRef.id,
          date: formatDate(date),
          btsReservedCount: 0,
          duty: template.duty,
          isBatch: true, // Mark as batch created
        };

        batch.set(docRef, duty);
        totalDutiesCreated++;
      }
    }
    if (totalDutiesCreated > 0) {
      await batch.commit();
      // Send ONE single summary notification for the whole batch
      try {
        const message = {
          notification: {
            title: "Monthly Duties Created!",
            body: "Duties for next month have been posted on the calendar.",
          },
          data: {
            destination: "calendar",
          },
          topic: "new_duties",
        };
        await admin.messaging().send(message);
        console.log("Successfully sent batch summary notification.");
      } catch (error) {
        console.error("Error sending batch summary notification:", error);
      }
    }
  }
);
