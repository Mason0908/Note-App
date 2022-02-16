package com.example.noteapp


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class AddMultipleNoteTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun addMultipleNoteTest() {
        val floatingActionButton = onView(
            allOf(
                withId(R.id.btnAdd), withContentDescription("add note"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        floatingActionButton.perform(click())

        val appCompatEditText = onView(
            allOf(
                withId(R.id.noteTitle),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText.perform(replaceText("1"), closeSoftKeyboard())

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.noteBody),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatEditText2.perform(replaceText("body 1"), closeSoftKeyboard())

        val actionMenuItemView = onView(
            allOf(
                withId(R.id.saveChanges), withContentDescription("Save"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.toolbar),
                        2
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        actionMenuItemView.perform(click())

        val floatingActionButton2 = onView(
            allOf(
                withId(R.id.btnAdd), withContentDescription("add note"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        floatingActionButton2.perform(click())

        val appCompatEditText3 = onView(
            allOf(
                withId(R.id.noteTitle),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText3.perform(replaceText("2"), closeSoftKeyboard())

        val actionMenuItemView2 = onView(
            allOf(
                withId(R.id.saveChanges), withContentDescription("Save"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.toolbar),
                        2
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        actionMenuItemView2.perform(click())

        val floatingActionButton3 = onView(
            allOf(
                withId(R.id.btnAdd), withContentDescription("add note"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        floatingActionButton3.perform(click())

        val appCompatEditText4 = onView(
            allOf(
                withId(R.id.noteTitle),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText4.perform(replaceText("3"), closeSoftKeyboard())

        val appCompatEditText5 = onView(
            allOf(
                withId(R.id.noteBody),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatEditText5.perform(replaceText("herhe is note"), closeSoftKeyboard())

        val actionMenuItemView3 = onView(
            allOf(
                withId(R.id.saveChanges), withContentDescription("Save"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.toolbar),
                        2
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        actionMenuItemView3.perform(click())

        val textView = onView(
            allOf(
                withId(R.id.nTitle), withText("1"),
                withParent(withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))),
                isDisplayed()
            )
        )
        textView.check(matches(withText("1")))

        val textView2 = onView(
            allOf(
                withId(R.id.nTitle), withText("2"),
                withParent(withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("2")))

        val textView3 = onView(
            allOf(
                withId(R.id.nTitle), withText("3"),
                withParent(withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))),
                isDisplayed()
            )
        )
        textView3.check(matches(withText("3")))

        val recyclerView = onView(
            allOf(
                withId(R.id.noteBoard),
                childAtPosition(
                    withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                    1
                )
            )
        )
        recyclerView.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        val textView4 = onView(
            allOf(
                withId(R.id.noteDisplay), withText("body 1"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()
            )
        )
        textView4.check(matches(withText("body 1")))

        val textView5 = onView(
            allOf(
                withText("1"),
                withParent(
                    allOf(
                        withId(R.id.toolbar),
                        withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView5.check(matches(withText("1")))

        val appCompatImageButton = onView(
            allOf(
                withContentDescription("Navigate up"),
                childAtPosition(
                    allOf(
                        withId(R.id.toolbar),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatImageButton.perform(click())

        val recyclerView2 = onView(
            allOf(
                withId(R.id.noteBoard),
                childAtPosition(
                    withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                    1
                )
            )
        )
        recyclerView2.perform(actionOnItemAtPosition<ViewHolder>(1, click()))

        val textView6 = onView(
            allOf(
                withId(R.id.noteDisplay),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()
            )
        )
        textView6.check(matches(withText("")))

        val textView7 = onView(
            allOf(
                withText("2"),
                withParent(
                    allOf(
                        withId(R.id.toolbar),
                        withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView7.check(matches(withText("2")))

        val appCompatImageButton2 = onView(
            allOf(
                withContentDescription("Navigate up"),
                childAtPosition(
                    allOf(
                        withId(R.id.toolbar),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatImageButton2.perform(click())

        val recyclerView3 = onView(
            allOf(
                withId(R.id.noteBoard),
                childAtPosition(
                    withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                    1
                )
            )
        )
        recyclerView3.perform(actionOnItemAtPosition<ViewHolder>(2, click()))

        val textView8 = onView(
            allOf(
                withId(R.id.noteDisplay), withText("herhe is note"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()
            )
        )
        textView8.check(matches(withText("herhe is note")))

        val textView9 = onView(
            allOf(
                withText("3"),
                withParent(
                    allOf(
                        withId(R.id.toolbar),
                        withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView9.check(matches(withText("3")))

        val actionMenuItemView4 = onView(
            allOf(
                withId(R.id.editNote), withContentDescription("Edit"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.toolbar),
                        2
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        actionMenuItemView4.perform(click())

        val appCompatEditText6 = onView(
            allOf(
                withId(R.id.noteBody), withText("herhe is note"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatEditText6.perform(click())

        val appCompatEditText7 = onView(
            allOf(
                withId(R.id.noteBody), withText("herhe is note"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatEditText7.perform(replaceText("not fngfkjherhe is note"))

        val appCompatEditText8 = onView(
            allOf(
                withId(R.id.noteBody), withText("not fngfkjherhe is note"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatEditText8.perform(closeSoftKeyboard())

        val actionMenuItemView5 = onView(
            allOf(
                withId(R.id.saveChanges), withContentDescription("Save"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.toolbar),
                        2
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        actionMenuItemView5.perform(click())

        val recyclerView4 = onView(
            allOf(
                withId(R.id.noteBoard),
                childAtPosition(
                    withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                    1
                )
            )
        )
        recyclerView4.perform(actionOnItemAtPosition<ViewHolder>(2, click()))

        val textView10 = onView(
            allOf(
                withId(R.id.noteDisplay), withText("not fngfkjherhe is note"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()
            )
        )
        textView10.check(matches(withText("not fngfkjherhe is note")))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
