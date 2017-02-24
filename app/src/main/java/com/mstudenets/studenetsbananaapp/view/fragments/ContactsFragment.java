package com.mstudenets.studenetsbananaapp.view.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mstudenets.studenetsbananaapp.R;


public class ContactsFragment extends Fragment
{
    //private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 101;

    private ViewPager viewPager;
    private View view;
    private String phoneNumber;

    public ContactsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contacts, container, false);
        view = root;
        initializeContactsList();

        return root;
    }

    public void callPhone(String number) {
        this.phoneNumber = number;
        checkCallPhonePermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhone(phoneNumber);
            }
        } else {
            Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_LONG).show();
        }
    }

    private void checkCallPhonePermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CALL_PHONE)) {
                Toast.makeText(getContext(), "We need this permissions to make calls from the app",
                        Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[] { Manifest.permission.CALL_PHONE },
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
            }
        } else {
            performCall();
        }
    }

    private void performCall() {
    }

    private void initializeContactsList() {
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.fragment_contacts_tablayout);

        viewPager = (ViewPager) view.findViewById(R.id.fragment_contacts_viewpager);
        final PagerAdapter pagerAdapter = new PagerAdapter(getFragmentManager(),
                tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }


    /*
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 101;
    private PermissionsCheckable permissionsCheckable;

    private ViewPager viewPager;
    private View view;

    public ContactsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contacts, container, false);
        view = root;

        checkContactsPermission();
        //initializeContactsList();

        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initializeContactsList();
                } else {
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case MY_PERMISSIONS_REQUEST_CALL_PHONE:
                ImageButton callButton = (ImageButton)
                        view.findViewById(R.id.contact_item_call_button);
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (callButton != null)
                        callButton.setEnabled(true);
                    Toast.makeText(getContext(), "Permission was granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                    if (callButton != null)
                        callButton.setEnabled(false);
                }
        }
    }

    @Override
    public void checkCallPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CALL_PHONE)) {
                ViewPager viewPager = (ViewPager) getView()
                        .findViewById(R.id.fragment_contacts_viewpager);
                Snackbar.make(viewPager,
                        "We need a permission to call your contacts from the app",
                        Snackbar.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[] { Manifest.permission.CALL_PHONE },
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
            }
        }
    }

    private void checkContactsPermission() throws NullPointerException {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CONTACTS)) {
                ViewPager viewPager = (ViewPager)
                        view.findViewById(R.id.fragment_contacts_viewpager);

                Snackbar.make(viewPager,
                        "The app needs a permission to read your phone's contacts",
                        Snackbar.LENGTH_LONG).show();

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[] {Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
    }
    private void initializeContactsList() {
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.fragment_contacts_tablayout);

        viewPager = (ViewPager) view.findViewById(R.id.fragment_contacts_viewpager);
        final PagerAdapter pagerAdapter = new PagerAdapter(getFragmentManager(),
                tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
    */
}
