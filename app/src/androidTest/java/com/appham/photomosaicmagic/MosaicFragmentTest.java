package com.appham.photomosaicmagic;

import android.content.Intent;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Some simple tests just to demonstrate that the app can be tested automatically
 *
 * @author Thomas Fuchs-Martin <t.fuchsmartin@gmail.com>
 */
@RunWith(AndroidJUnit4.class)
public class MosaicFragmentTest {

    @ClassRule
    public static ActivityTestRule<BaseActivity> mActivityClassRule = new ActivityTestRule<>(
            BaseActivity.class);

    @Rule
    public ActivityTestRule<BaseActivity> mActivityRule = new ActivityTestRule<>(
            BaseActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    /**
     * Test if load image button text is displayed with correct text
     */
    @Test
    public void testImageButtonText() {

        onView(withId(R.id.btnLoadImg))
                .check(matches(withText(R.string.load_image)));
    }

    /**
     * Test if click on image button opens chooser
     */
    @Test
    public void testImageButtonClick() {

        onView(withId(R.id.btnLoadImg))
                .perform(click());

        intended(hasAction(Intent.ACTION_CHOOSER));

    }
}
