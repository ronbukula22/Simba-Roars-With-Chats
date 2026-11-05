package com.example.simbachat

import com.example.simbachat.dashboard.Message
import com.example.simbachat.util.ChatUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatUnitTest {

    @Test
    fun testGenerateChatRoomId_orderIndependence() {
        val userA = "user_123"
        val userB = "user_456"

        val roomId1 = ChatUtils.generateChatRoomId(userA, userB)
        val roomId2 = ChatUtils.generateChatRoomId(userB, userA)

        assertEquals("user_123_user_456", roomId1)
        assertEquals(roomId1, roomId2)
    }

    @Test
    fun testGenerateChatRoomId_blankInputReturnsEmpty() {
        assertEquals("", ChatUtils.generateChatRoomId("", "userB"))
        assertEquals("", ChatUtils.generateChatRoomId("userA", "   "))
    }

    @Test
    fun testIsMessageValid_validMessageReturnsTrue() {
        assertTrue(ChatUtils.isMessageValid("Hello world!"))
        assertTrue(ChatUtils.isMessageValid("Hi"))
    }

    @Test
    fun testIsMessageValid_emptyOrWhitespaceReturnsFalse() {
        assertFalse(ChatUtils.isMessageValid(""))
        assertFalse(ChatUtils.isMessageValid("   "))
        assertFalse(ChatUtils.isMessageValid(null))
    }

    @Test
    fun testMessageModel_instantiation() {
        val timestamp = System.currentTimeMillis()
        val msg = Message(
            sender_Id = "sender123",
            receiver_Id = "receiver456",
            Message = "Hello from unit test",
            timestamp = timestamp
        )

        assertNotNull(msg)
        assertEquals("sender123", msg.sender_Id)
        assertEquals("receiver456", msg.receiver_Id)
        assertEquals("Hello from unit test", msg.Message)
        assertEquals(timestamp, msg.timestamp)
    }
}
