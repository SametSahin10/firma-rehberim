package net.dijitalbeyin.firma_rehberim;

import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

import net.dijitalbeyin.firma_rehberim.adapters.RadioAdapter;


public class RadiosActivity extends FragmentActivity implements RadioAdapter.OnAddToFavouriteListener {
    private static final int NUM_PAGES = 2;

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        RadiosFragment radiosFragment = new RadiosFragment();
        fragmentTransaction.replace(R.id.fragment_container, radiosFragment).commit();

        viewPager = findViewById(R.id.view_pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/righteous_regular.ttf");
        Log.d("TAG46", "onCreate: ");
        int numOfTabs = tabLayout.getTabCount();
        for (int i = 0; i < numOfTabs; i++) {
            TextView tv_tab_title = (TextView) LayoutInflater.from(this)
                                    .inflate(R.layout.custom_textview_for_tab_titles, null);
            tv_tab_title.setTypeface(typeface);
            tabLayout.getTabAt(i).setCustomView(tv_tab_title);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new RadiosFragment();
                case 1:
                    return new FavouriteRadiosFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.all_the_radios_tab_title);
                case 1:
                    return getString(R.string.favourite_radios_tab_title);
                default:
                    return "Unknown tab title";
            }
        }
    }

    @Override
    public void onAddToFavouriteClick() {
        String tag = "android:switcher:" + R.id.view_pager + ":" + 1;
        FavouriteRadiosFragment favouriteRadiosFragment =
                (FavouriteRadiosFragment) getSupportFragmentManager().findFragmentByTag(tag);
        favouriteRadiosFragment.updateFavouriteRadiosList();
    }
}
