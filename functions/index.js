const functions = require("firebase-functions/v1");
const admin = require("firebase-admin");
admin.initializeApp();

// Firestore trigger: Runs when a new term is added to "terms" collection.
exports.notifyAddTerm = functions
  .region("europe-west1")
  .firestore
  .document("terms/{termId}")
  .onCreate(async (snap, context) => {
    try {
      const term = snap.data();
      const message = {
        topic: "all_users",
        notification: {
          title: "New Term Added!",
          body: `Term: ${term.title || "No title"} at ${term.location || "No location"}`,
        },
        data: {
          title: "New Term Added!",
          body: `Term: ${term.title || "No title"} at ${term.location || "No location"}`,
        },
      };

      const response = await admin.messaging().send(message);
      console.log("Notification sent to topic:", response);
      return null;
    } catch (error) {
      console.error("Error sending notification to topic:", error);
      return null;
    }
  });
