package com.example.simbachat

import com.example.simbachat.dashboard.Message
import com.example.simbachat.util.ChatUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatRoomUnitTest {

    @Test
    fun testRoomMessageValidation_validContent() {
        val validMsg = "Welcome to Joburg Vibes room!"
        assertTrue(ChatUtils.isMessageValid(validMsg))
    }

    @Test
    fun testRoomMessageValidation_invalidContent() {
        assertFalse(ChatUtils.isMessageValid(""))
        assertFalse(ChatUtils.isMessageValid("    "))
    }

    @Test
    fun testGetAvatarInitials_twoWordName() {
        val name = "Thabo Nkosi"
        assertEquals("TN", ChatUtils.getAvatarInitials(name))
    }

    @Test
    fun testGetAvatarInitials_singleWordNameOrId() {
        assertEquals("S", ChatUtils.getAvatarInitials("simba"))
        assertEquals("U", ChatUtils.getAvatarInitials(""))
        assertEquals("U", ChatUtils.getAvatarInitials(null))
    }

    @Test
    fun testGetRoomHandle_positionBasedTitles() {
        val senderId = "A1B2C3D4"
        assertEquals("Pride #A1B2", ChatUtils.getRoomHandle(0, senderId))
        assertEquals("Mane #A1B2", ChatUtils.getRoomHandle(1, senderId))
        assertEquals("Roar #A1B2", ChatUtils.getRoomHandle(2, senderId))
        assertEquals("Cub #A1B2", ChatUtils.getRoomHandle(3, senderId))
    }

    @Test
    fun testFormatTimestamp_defaultFallback() {
        assertEquals("10:00", ChatUtils.formatTimestamp(0))
        assertEquals("10:00", ChatUtils.formatTimestamp(-100))
    }

    @Test
    fun testRoomMessageObject_creation() {
        val roomMsg = Message(
            sender_Id = "user789",
            receiver_Id = "joburg_vibes_room",
            Message = "Group message content",
            timestamp = 1700000000000L
        )

        assertNotNull(roomMsg)
        assertEquals("user789", roomMsg.sender_Id)
        assertEquals("joburg_vibes_room", roomMsg.receiver_Id)
        assertEquals("Group message content", roomMsg.Message)
        assertEquals(1700000000000L, roomMsg.timestamp)
    }
}
