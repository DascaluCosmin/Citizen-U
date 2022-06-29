package com.ubb.citizen_u.ui

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
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
        private const val VALID_EMAIL_REGISTRATION = "das.CoSmiN3000@gmail.com"
        private const val VALID_PASSWORD = "cosmin123"
        private const val WRONG_EMAIL = "aaaaa@gmail.com"
        private const val WRONG_PASSWORD = "someWrongPassword!@"
    }

    @get:Rule
    var activityScenarioRule = activityScenarioRule<AuthenticationActivity>()

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
    fun shouldSuccessfullyLogin() {
        onView(withId(R.id.layout_welcome))
            .check(matches(isDisplayed()))

        Thread.sleep(3000L)

        onView(isEditTextInLayout(R.id.email_textfield))
            .check(matches(isDisplayed()))
            .perform(typeText(VALID_EMAIL))

        onView(isEditTextInLayout(R.id.password_textfield))
            .check(matches(isDisplayed()))
            .perform(typeText(VALID_PASSWORD))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.sign_in_button))
            .perform(click())

        Thread.sleep(3000L)

        onView(withId(R.id.layout_signed_in))
            .check(matches(isDisplayed()))

        onView(withId(R.id.sign_out_button))
            .perform(scrollTo(), click())
    }

    @Test
    fun shouldFailLoginIfEmailIsEmpty() {
        onView(withId(R.id.layout_welcome))
            .check(matches(isDisplayed()))

        Thread.sleep(3000L)

        onView(isEditTextInLayout(R.id.email_textfield))
            .check(matches(isDisplayed()))
            .perform(typeText(""))

        onView(isEditTextInLayout(R.id.password_textfield))
            .check(matches(isDisplayed()))
            .perform(typeText(VALID_PASSWORD))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.sign_in_button))
            .perform(click())

        Thread.sleep(1000L)

        onView(withId(R.id.layout_signed_in))
            .check(doesNotExist())
    }


    @Test
    fun shouldFailLoginIfPasswordIsEmpty() {
        onView(withId(R.id.layout_welcome))
            .check(matches(isDisplayed()))

        Thread.sleep(3000L)

        onView(isEditTextInLayout(R.id.email_textfield))
            .check(matches(isDisplayed()))
            .perform(typeText(VALID_EMAIL))

        onView(isEditTextInLayout(R.id.password_textfield))
            .check(matches(isDisplayed()))
            .perform(typeText(""))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.sign_in_button))
            .perform(click())

        Thread.sleep(1000L)

        onView(withId(R.id.layout_signed_in))
            .check(doesNotExist())
    }


    @Test
    fun shouldFailLoginIfEmailIsBlank() {
        onView(withId(R.id.layout_welcome))
            .check(matches(isDisplayed()))

        Thread.sleep(3000L)

        onView(isEditTextInLayout(R.id.email_textfield))
            .check(matches(isDisplayed()))
            .perform(typeText("      "))

        onView(isEditTextInLayout(R.id.password_textfield))
            .check(matches(isDisplayed()))
            .perform(typeText(VALID_PASSWORD))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.sign_in_button))
            .perform(click())

        Thread.sleep(1000L)

        onView(withId(R.id.layout_signed_in))
            .check(doesNotExist())
    }


    @Test
    fun shouldFailLoginIfPasswordIsBlank() {
        onView(withId(R.id.layout_welcome))
            .check(matches(isDisplayed()))

        Thread.sleep(3000L)

        onView(isEditTextInLayout(R.id.email_textfield))
            .check(matches(isDisplayed()))
            .perform(typeText(VALID_EMAIL))

        onView(isEditTextInLayout(R.id.password_textfield))
            .check(matches(isDisplayed()))
            .perform(typeText("        "))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.sign_in_button))
            .perform(click())

        Thread.sleep(1000L)

        onView(withId(R.id.layout_signed_in))
            .check(doesNotExist())
    }

    @Test
    fun shouldFailLoginIfTheCredentialsAreWrong() {
        onView(withId(R.id.layout_welcome))
            .check(matches(isDisplayed()))

        Thread.sleep(3000L)

        onView(isEditTextInLayout(R.id.email_textfield))
            .check(matches(isDisplayed()))
            .perform(typeText(WRONG_EMAIL))

        onView(isEditTextInLayout(R.id.password_textfield))
            .check(matches(isDisplayed()))
            .perform(typeText(WRONG_PASSWORD))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.sign_in_button))
            .perform(click())

        Thread.sleep(1000L)

        onView(withId(R.id.layout_signed_in))
            .check(doesNotExist())
    }

    @Test
    fun shouldGoToRegisterSection() {
        onView(withId(R.id.layout_welcome))
            .check(matches(isDisplayed()))

        Thread.sleep(3000L)

        onView(withId(R.id.register_button))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.layout_register))
            .check(matches(isDisplayed()))
    }

    @Test
    fun shouldGoToSecondStepOfRegistration() {
        onView(withId(R.id.layout_welcome))
            .check(matches(isDisplayed()))

        Thread.sleep(3000L)

        onView(withId(R.id.register_button))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(isEditTextInLayout(R.id.email_textfield))
            .check(matches(isDisplayed()))
            .perform(typeText(VALID_EMAIL_REGISTRATION))

        onView(isEditTextInLayout(R.id.password_textfield))
            .check(matches(isDisplayed()))
            .perform(typeText(VALID_PASSWORD))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.create_account_button))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.layout_identity_information))
            .check(matches(isDisplayed()))
    }

    @Test
    fun shouldNotGoToSecondStepOfRegistrationIfEmailIsBlank() {
        onView(withId(R.id.layout_welcome))
            .check(matches(isDisplayed()))

        Thread.sleep(3000L)

        onView(withId(R.id.register_button))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(isEditTextInLayout(R.id.email_textfield))
            .check(matches(isDisplayed()))
            .perform(typeText(""))

        onView(isEditTextInLayout(R.id.password_textfield))
            .check(matches(isDisplayed()))
            .perform(typeText(VALID_PASSWORD))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.create_account_button))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.layout_identity_information))
            .check(doesNotExist())
    }

    @Test
    fun shouldNotGoToSecondStepOfRegistrationIfPasswordIsBlank() {
        onView(withId(R.id.layout_welcome))
            .check(matches(isDisplayed()))

        Thread.sleep(3000L)

        onView(withId(R.id.register_button))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(isEditTextInLayout(R.id.email_textfield))
            .check(matches(isDisplayed()))
            .perform(typeText(VALID_EMAIL_REGISTRATION))

        onView(isEditTextInLayout(R.id.password_textfield))
            .check(matches(isDisplayed()))
            .perform(typeText(""))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.create_account_button))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.layout_identity_information))
            .check(doesNotExist())
    }

    @Test
    fun shouldNotGoToSecondStepOfRegistrationIfPasswordContainsLessThan3Characters() {
        onView(withId(R.id.layout_welcome))
            .check(matches(isDisplayed()))

        Thread.sleep(3000L)

        onView(withId(R.id.register_button))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(isEditTextInLayout(R.id.email_textfield))
            .check(matches(isDisplayed()))
            .perform(typeText(VALID_EMAIL_REGISTRATION))

        onView(isEditTextInLayout(R.id.password_textfield))
            .check(matches(isDisplayed()))
            .perform(typeText("ab"))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.create_account_button))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.layout_identity_information))
            .check(doesNotExist())
    }

    @Test
    fun shouldGoToSecondStepOfRegistrationIfPasswordContainsExactly3Characters() {
        onView(withId(R.id.layout_welcome))
            .check(matches(isDisplayed()))

        Thread.sleep(3000L)

        onView(withId(R.id.register_button))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(isEditTextInLayout(R.id.email_textfield))
            .check(matches(isDisplayed()))
            .perform(typeText(VALID_EMAIL_REGISTRATION))

        onView(isEditTextInLayout(R.id.password_textfield))
            .check(matches(isDisplayed()))
            .perform(typeText("abc"))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.create_account_button))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.layout_identity_information))
            .check(matches(isDisplayed()))
    }

    @Test
    fun shouldGoToSecondStepOfRegistrationIfPasswordContainsMoreThan3Characters() {
        onView(withId(R.id.layout_welcome))
            .check(matches(isDisplayed()))

        Thread.sleep(3000L)

        onView(withId(R.id.register_button))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(isEditTextInLayout(R.id.email_textfield))
            .check(matches(isDisplayed()))
            .perform(typeText(VALID_EMAIL_REGISTRATION))

        onView(isEditTextInLayout(R.id.password_textfield))
            .check(matches(isDisplayed()))
            .perform(typeText("abcd"))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.create_account_button))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.layout_identity_information))
            .check(matches(isDisplayed()))
    }


    private fun isEditTextInLayout(parentViewId: Int): Matcher<View> {
        return Matchers.allOf(isDescendantOfA(withId(parentViewId)),
            withClassName(Matchers.endsWith("EditText")))
    }
}