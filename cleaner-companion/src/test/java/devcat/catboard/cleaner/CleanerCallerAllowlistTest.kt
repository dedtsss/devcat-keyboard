package devcat.catboard.cleaner

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CleanerCallerAllowlistTest {
    @Test fun acceptsReleaseAndDebugKeyboardIds() {
        assertTrue(CleanerCallerAllowlist.isAllowed(setOf("helium314.keyboard")))
        assertTrue(CleanerCallerAllowlist.isAllowed(setOf("helium314.keyboard.debug")))
    }

    @Test fun rejectsUnrelatedPackages() {
        assertFalse(CleanerCallerAllowlist.isAllowed(setOf("devcat.catboard")))
        assertFalse(CleanerCallerAllowlist.isAllowed(setOf("devcat.catboard.debug")))
        assertFalse(CleanerCallerAllowlist.isAllowed(emptySet()))
    }
}
