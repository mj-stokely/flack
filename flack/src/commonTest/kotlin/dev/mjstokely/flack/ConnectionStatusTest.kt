package dev.mjstokely.flack

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ConnectionStatusTest {
    
    @Test
    fun testDefaultValues() {
        val status = ConnectionStatus()
        assertFalse(status.isConnected)
        assertNull(status.ssid)
        assertNull(status.signalStrength)
    }
    
    @Test
    fun testCustomValues() {
        val status = ConnectionStatus(
            isConnected = true,
            ssid = "TestNetwork",
            signalStrength = 75
        )
        
        assertTrue(status.isConnected)
        assertEquals("TestNetwork", status.ssid)
        assertEquals(75, status.signalStrength)
    }
    
    @Test
    fun testCopy() {
        val original = ConnectionStatus(
            isConnected = true,
            ssid = "TestNetwork",
            signalStrength = 75
        )
        
        val copy = original.copy(signalStrength = 80)
        
        assertTrue(copy.isConnected)
        assertEquals("TestNetwork", copy.ssid)
        assertEquals(80, copy.signalStrength)
    }
}