package com.example.pkart.fragment

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pkart.MainActivity
import com.example.pkart.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CartFragmentUITest {

    @Test
    fun testEditAddressButtonClickable() {
        ActivityScenario.launch(MainActivity::class.java)

        // Give the app time to load
        Thread.sleep(2000)

        // 🔁 Replace this with the actual ID of the button/nav that opens CartFragment
        onView(withId(R.id.moreFragment)).perform(click())



    }
}
