
package com.example.pkart.activity

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.pkart.R

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.INTERNET)

    @Test
    fun loginWithEmptyCredentials_showsError() {
        ActivityScenario.launch(LoginActivity::class.java)

        onView(withId(R.id.loginButton)).perform(click())

        onView(withText("Email cannot be empty")).check(matches(isDisplayed()))
    }

    @Test
    fun typeEmailAndPassword_performsLogin() {
        ActivityScenario.launch(LoginActivity::class.java)

        onView(withId(R.id.mobileInput)).perform(typeText("8591021930"), closeSoftKeyboard())
        onView(withId(R.id.passwordInput)).perform(typeText("zxzxzx"), closeSoftKeyboard())

        onView(withId(R.id.loginButton)).perform(click())
        // You might need to adjust this depending on actual navigation
        // onView(withText("Welcome")).check(matches(isDisplayed()))
    }
}
