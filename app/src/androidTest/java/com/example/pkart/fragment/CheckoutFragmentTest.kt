
package com.example.pkart.fragment

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pkart.MainActivity
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.action.ViewActions.click
import com.example.pkart.R

@RunWith(AndroidJUnit4::class)
class CheckoutFragmentTest {

    @Test
    fun testCheckoutFragmentLoadsProperly() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.cartFragment)).perform(click())
        onView(withId(R.id.btnCod)).perform(click())
        onView(withText("Delivery Address")).check(matches(isDisplayed()))
    }
}
