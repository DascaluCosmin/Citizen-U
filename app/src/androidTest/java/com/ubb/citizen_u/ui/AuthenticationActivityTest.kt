package com.ubb.citizen_u.ui

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ubb.citizen_u.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4ClassRunner::class)
class AuthenticationActivityTest {

    companion object {
        private const val VALID_EMAIL = "das.cosmin2000@gmail.com"
        private const val VALID_PASSWORD = "cosmin123"
    }

    @get:Rule
    var activityScenarioRule = activityScenarioRule<AuthenticationActivity>()

    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    @Test
    fun shouldDisplayActivity() {
        onView(withId(R.id.layout_welcome))
            .check(matches(isDisplayed()))

        Thread.sleep(3000L)

        onView(withId(R.id.welcome_textview))
            .check(matches(isDisplayed()))
        onView(withId(R.id.welcome_textview))
            .check(matches(withText(R.string.welcome_sign_in_textview)))
        onView(withId(R.id.sign_in_button))
            .check(matches(isDisplayed()))
    }

    @Test
    fun shouldSuccessfullyLoginIn() {
        onView(withId(R.id.layout_welcome))
            .check(matches(isDisplayed()))

        Thread.sleep(3000L)

        onView(withId(R.id.email_textfield))
            .check(matches(isDisplayed()))

        onView(isEditTextInLayout(R.id.email_textfield))
            .perform(typeText(VALID_EMAIL))
        onView(isEditTextInLayout(R.id.password_textfield))
            .perform(typeText(VALID_PASSWORD))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.sign_in_button))
            .perform(click())

        Thread.sleep(3000L)

        onView(withId(R.id.sign_out_button))
            .perform(scrollTo(), click())
    }

    private fun isEditTextInLayout(parentViewId: Int): Matcher<View> {
        return Matchers.allOf(isDescendantOfA(withId(parentViewId)),
            withClassName(Matchers.endsWith("EditText")))
    }
}