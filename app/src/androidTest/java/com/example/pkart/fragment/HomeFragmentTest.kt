
package com.example.pkart.fragment

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pkart.MainActivity
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import com.example.pkart.R
import androidx.test.espresso.action.ViewActions.click

@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {

    @Test
    fun testHomeFragmentDisplaysProducts() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.homeFragment)).perform(click())
        onView(withText("Latest Products")).check(matches(isDisplayed()))
    }
}
