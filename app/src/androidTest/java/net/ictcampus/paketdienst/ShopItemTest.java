package net.ictcampus.paketdienst;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ShopItemTest {

    private SharedPreferences.Editor editor;
    private Intent intent;
    SharedPreferences inventoryFile;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class, true,false);


    @Before
    public void setUp(){
        //Sets up SharedPreferences
        Context context = getInstrumentation().getTargetContext();
        inventoryFile = context.getSharedPreferences("inventory", Context.MODE_PRIVATE);
        editor = inventoryFile.edit();
    }

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION");

    @Test
    public void mainActivityTest4() throws InterruptedException {

        //Gets Tokens and increases balance by 4000
        int previousTokens;
        previousTokens = inventoryFile.getInt("TOKENS", 0);
        editor.putInt("TOKENS", 4000);
        editor.apply();

        //Starts Activity
        mActivityTestRule.launchActivity(new Intent());

        //Clicks on menu button
        ViewInteraction imageButton = onView(
                allOf(withId(R.id.imageButton), withContentDescription(R.string.app_name),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        imageButton.perform(click());

        //Clicks on Shop
        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.shop), withText(R.string.menuShop),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        Thread.sleep(2000);
        appCompatTextView.perform(click());

        //Checks value of button2
        onView(withId(R.id.button2)).check(matches(withText("125")));

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.button2), withText("125"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                12),
                        isDisplayed()));
        Thread.sleep(2000);

        //Click button2
        appCompatButton.perform(click());
        Thread.sleep(2000);

        //Checks, if timer is running and not clickable
        onView(withId(R.id.button2)).check(matches(not(withText("125"))));
        onView(withId(R.id.button2)).check(matches(not(isClickable())));

        //Sets tokens value back
        editor.putInt("TOKENS", previousTokens);
        editor.apply();
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
