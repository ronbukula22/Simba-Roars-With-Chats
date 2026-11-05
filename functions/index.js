
const { onDocumentCreated } = require("firebase-functions/v2/firestore");

const { initializeApp } = require("firebase-admin/app");
const { getFirestore } = require("firebase-admin/firestore");
const { getMessaging } = require("firebase-admin/messaging");

initializeApp();

const db = getFirestore();

exports.sendChatNotification = onDocumentCreated(
  "chats/{chatRoomId}/messages/{messageId}",
  async (event) => {
    try {
      // Get the newly created message.
      const messageData = event.data.data();

      const senderId = messageData.sender_Id;
      const receiverId = messageData.receiver_Id;
      const messageText = String(messageData.Message || "");

      if (!senderId || !receiverId) {
        console.log("Message is missing sender or receiver ID.");
        return;
      }

      // Get the recipient's FCM token.
      const receiverDoc = await db
        .collection("users")
        .doc(receiverId)
        .get();

      if (!receiverDoc.exists) {
        console.log("Recipient user does not exist.");
        return;
      }

      const receiverData = receiverDoc.data();
      const receiverToken = receiverData.fcmToken;

      if (!receiverToken) {
        console.log("Recipient has no FCM token.");
        return;
      }

      // Get the sender's display name.
      const senderDoc = await db
        .collection("users")
        .doc(senderId)
        .get();

      const senderData = senderDoc.exists ? senderDoc.data() : {};

      const senderName =
        senderData.name ||
        senderData.displayName ||
        senderData.username ||
        "New message";

      // Send a data-only notification to Android.
      const response = await getMessaging().send({
        token: receiverToken,

        data: {
          senderId: String(senderId),
          senderName: String(senderName),
          chatId: String(event.params.chatRoomId),
          message: messageText || "You received a new message."
        },

        android: {
          priority: "high"
        }
      });

      console.log("Chat notification sent:", response);

    } catch (error) {
      console.error("Error sending chat notification:", error);
    }
  }
);
