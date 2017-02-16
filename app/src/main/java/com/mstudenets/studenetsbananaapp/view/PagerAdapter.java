package com.mstudenets.studenetsbananaapp.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mstudenets.studenetsbananaapp.view.fragments.ContactBookFragment;
import com.mstudenets.studenetsbananaapp.view.fragments.MyContactsFragment;


public class PagerAdapter extends FragmentStatePagerAdapter
{
    private int tabCount;

    public PagerAdapter(FragmentManager fragmentManager, int tabCount) {
        super(fragmentManager);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ContactBookFragment();
            case 1:
                return new MyContactsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
