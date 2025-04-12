package com.example.pkart.activity

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class AddressActivityTest {

    @Test
    fun testAddressScreenLoads() {
        ActivityScenario.launch(AddressActivity::class.java)
        onView(withText("Save Address")).check(matches(isDisplayed()))
    }
}
