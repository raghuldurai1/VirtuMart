
package com.example.pkart.activity

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class HelpActivityTest {

    @Test
    fun testHelpScreenLoads() {
        ActivityScenario.launch(HelpActivity::class.java)
        onView(withText("Help and Support")).check(matches(isDisplayed()))
    }
}
