import {onDocumentCreated} from "firebase-functions/v2/firestore";
import * as admin from "firebase-admin";

/**
 * Triggered automatically whenever a new duty
 document is created in Firestore.
 * Sends a push notification to all users subscribed to the
 'new_duties' FCM topic.
 */
export const sendDutyNotification = onDocumentCreated(
  "duties/{dutyId}",
  async (event) => {
    const snapshot = event.data;
    if (!snapshot) {
      console.log("No snapshot data found for trigger");
      return;
    }

    const dutyData = snapshot.data();
    // Skip individual notification for batch-created duties
    if (dutyData?.isBatch) {
      console.log("Skipping individual notification for batch duty:",
        event.params.dutyId);
      return;
    }
    const meetingName = dutyData?.duty?.meetingName || "New Duty";
    const date = dutyData?.date || "";

    // notification payload is the visible alert displayed on phone
    const message = {
      notification: {
        title: "New Duty Created!",
        body: date ?
          `${meetingName} on ${date} has been posted.` :
          `A new duty "${meetingName}" has been posted.`,
      },
      data: {
        // Custom payload so the Android app knows where to navigate
        // when tapped (invisible key value pairs)
        destination: "calendar",
        dutyId: event.params.dutyId,
      },
      topic: "new_duties",
    };

    try {
      const response = await admin.messaging().send(message);
      console.log("Successfully sent FCM notification:", response);
    } catch (error) {
      console.error("Error sending FCM notification:", error);
    }
  }
);

// implement for batch and non batch notifications
