package com.benrcarvergmail.cvhsmobileapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.app.AlertDialog;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Object to represent the tabLayout. TabLayout is an XML layout used for creating tabbed
    // interfaces. It is best to use this methodology for our current target API and supported APIs
    private TabLayout mTabLayout;

    // Reference to the Relative Layout that displays the plus schedule
    private LinearLayout mPlusLayout;

    // GoogleAPI Client
    private GoogleApiClient mGoogleApiClient;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Always call the super onCreate method, passing savedInstanceState
        super.onCreate(savedInstanceState);
        // Set the content view (XML file to render what the user sees) to activity_main.xml
        setContentView(R.layout.activity_main);
        // Instantiated the toolbar object to the one defined in the XML
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        // Assign the support action bar (the toolbar) to the object we just instantiated
        setSupportActionBar(mToolbar);
        // Setting this to true makes it such that if selecting whatever we determine a "home" button to be
        // will make the UI go up ONE level as opposed to going all the way to the front page.
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Disables the label defined in AndroidManifest.xml from being displayed on the toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Instantiate the ViewPager
        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), MainActivity.this);
        mViewPager.setAdapter(adapter);
        setupViewPager(mViewPager);
        // Instantiate the TabLayout object
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        // Assign the ViewPager object to the TabLayout object so our tabs are able to be navigated
        // by swiping left and right (which is what we want)
        mTabLayout.setupWithViewPager(mViewPager);
        // Prevents the keyboard from opening right when the app opens
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Crisis Button
        ImageButton imageButtonCrisis = (ImageButton) findViewById(R.id.crisisButton);
        assert imageButtonCrisis != null;
        String tel = "";

        final CharSequence crisis[] = new CharSequence[] {
                "CrisisLink Regional Hotlink","Dominion Hospital Emergency Room","Inova Emergency Services",
                "Mobile Crisis Unit","National Suicide Prevention Hotline","Merrifield Center Emergency Services",
                "Fairfax County Police Department","Fairfax County Sheriff Department","Life Threating Emergencies (911)"
        };

        final String numbers[] = new String[] {
                "tel:7035274077","tel:7035362000","tel:7032897560","tel:18446274747","tel:1800273TALK","tel:7035735769","tel:7036912131",
                "tel:7033608404","tel:911"
        };

        imageButtonCrisis.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder  = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Crisis Numbers");
                /* setNegativeButton is an easter-egg message */
                builder.setNegativeButton(" ",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String easteregg[] = new String[]{
                                        "snafuPop","Jick","When the Meme is so Supreme, you let out a scream!"
                                        ,"He may be #Small, but his money is #BIG.","GRAND DAD??","muck","Ban Daniel Lies"};
                                double eggnum = Math.random() * easteregg.length;
                                Toast.makeText(MainActivity.this, easteregg[(int)eggnum], Toast.LENGTH_LONG).show();
                            }
                        });
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setItems(crisis, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialConfirm(numbers[which], crisis[which]);
                            }
                        });
                // After the dialog has been built, assign it to an AlertDialog object and show it
                final AlertDialog alertDialog = builder.show();


            }
        });

        Button mPlusButton = (Button) findViewById(R.id.button_plusSchedule);
        mPlusLayout = (LinearLayout) findViewById(R.id.relativelayout_plusSchedule);

        // The onClickListener for the plus button. When clicked, it causes the plus
        // schedule to fade in, overlaying upon whatever is visible.
        mPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlusLayout.setVisibility(View.VISIBLE);
                runFadeInAnimationOn(MainActivity.this, mPlusLayout);
            }
        });

        // When the user taps the plus layout (when its visible), it will fade back out.
        mPlusLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runFadeOutAnimationOn(MainActivity.this, mPlusLayout);
                mPlusLayout.setVisibility(View.GONE);
            }
        });

        // Assign the icons to the tabs
        setupTabIcons();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    /* Sends a confirmation message, then sends the user to their dialer with the number automatically inputted */
    private void dialConfirm(final String dial, CharSequence name){
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("Are you sure you want to call " + name + "? You will be taken to your phone's dialer.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse(dial));
                        startActivity(callIntent);
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    private static Animation runFadeOutAnimationOn(Activity ctx, View target) {
        Animation animation = AnimationUtils.loadAnimation(ctx,
                android.R.anim.fade_out);
        target.startAnimation(animation);
        return animation;
    }

    private static Animation runFadeInAnimationOn(Activity ctx, View target) {
        Animation animation = AnimationUtils.loadAnimation(ctx,
                android.R.anim.fade_in);
        target.startAnimation(animation);
        return animation;
    }

    // Hard-coded icons for the tabs
    private final int[] tabIcons = {
            R.drawable.tab1, // Newspaper. I am setting the drawable to an XML document that
            // determines what icon to display based on whether or not the tab is active or not.
            R.drawable.tab2, // Academics. I am setting the drawable to an XML document that
            // determines what icon to display based on whether or not the tab is active or not.
            R.drawable.tab3, // Box with cross. I am setting the drawable to an XML document that
            // determines what icon to display based on whether or not the tab is active or not.
            R.drawable.tab4, // Two silhouettes. I am setting the drawable to an XML document that
            // determines what icon to display based on whether or not the tab is active or not.
            R.drawable.tab5 // Gear. I am setting the drawable to an XML document that
            // determines what icon to display based on whether or not the tab is active or not.
    };

    // Assigns the tabs the correct icon from the tabIcons array
    private void setupTabIcons() {
        mTabLayout.getTabAt(0).setIcon(tabIcons[0]);
        mTabLayout.getTabAt(1).setIcon(tabIcons[1]);
        mTabLayout.getTabAt(2).setIcon(tabIcons[2]);
        mTabLayout.getTabAt(3).setIcon(tabIcons[3]);
        mTabLayout.getTabAt(4).setIcon(tabIcons[4]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    // This method creates Fragments for each tab we want.
    // We want five tabs, so we add five Fragments.
    // A Fragment is a piece of an application's user interface
    // or behavior that can be placed in an Activity
    private void setupViewPager(ViewPager viewPager) {
        // Does not pass any text to the addFrag method, so the tabs do not have any text titles
        ViewPagerAdapter vpa = (ViewPagerAdapter) viewPager.getAdapter();
        vpa.addFrag(new AnnouncementsFragment(), ""); // Announcements
        vpa.addFrag(new ArticlesVideosFragment(), ""); // Placeholder
        vpa.addFrag(new AcademicsFragment(), ""); // Placeholder
        vpa.addFrag(new ClubsFragment(), ""); // Clubs Fragment
        vpa.addFrag(new SettingsFragment(), ""); // Placeholder
    }

    /**
     * This is an override of the onKeyDown method of the Activity class.
     * I am overriding this to provide back button functionality such that the
     * user may use the back button to exit the plus schedule.
     * @param keyCode the button's key code
     * @param event the specific event the button triggered
     * @return boolean
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (android.os.Build.VERSION.SDK_INT > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * Here I am overriding the onBackPressed() method to allow the user
     * to use the back button to close the plus schedule.
     */
    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        if (mPlusLayout.getVisibility() == View.VISIBLE) {
            runFadeOutAnimationOn(this, mPlusLayout);
            mPlusLayout.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    // A FragmentPagerAdapter is an implementation of PagerAdapter that
    // represents each page as a Fragment that is persistently kept in
    // the fragment manager as long as the user can return to the page.
    class ViewPagerAdapter extends FragmentPagerAdapter {
        // List of all the Fragments pertaining to our tabs
        private final List<Fragment> mFragmentList = new ArrayList<>();

        // List of each title of each Fragment. The titles would be the String we pass in setupViewPager()
        private final List<String> mFragmentTitleList = new ArrayList<>();

        final Context context;

        // Constructor. Calls the super constructor passing our FragmentManager object
        public ViewPagerAdapter(FragmentManager manager, Context context) {
            super(manager);
            this.context = context;
        }

        // Returns the position of a given fragment in the fragmentList (would
        // effectively return which tab was currently the active tab)
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        // Returns the number of Fragments (equivalent to the number of tabs)
        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        // Adds a Fragment (effectively a tab). Takes in a Fragment and a title.
        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            notifyDataSetChanged();
            mFragmentTitleList.add(title);
        }

        // Returns the title of the current Fragment.
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }


    }


}















/* wew lad */