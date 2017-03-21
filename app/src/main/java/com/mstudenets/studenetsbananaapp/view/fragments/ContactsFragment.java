package com.mstudenets.studenetsbananaapp.view.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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
import com.mstudenets.studenetsbananaapp.controller.MyContactsAdapter;
import com.mstudenets.studenetsbananaapp.controller.PhoneCaller;


public class ContactsFragment extends Fragment implements PhoneCaller
{
    protected static final int PERMISSION_REQUEST_READ_CONTACTS = 100;
    protected static final int PERMISSION_REQUEST_CALL_PHONE = 101;

    private MyContactsAdapter adapter;
    private View view;
    private ViewPager viewPager;

    private boolean hasCallPermission = true;
    private String phoneNumber;

    public ContactsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contacts, container, false);
        view = root;

        initializeTabs();
        //checkContactsPermission();

        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void callPhone(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        checkCallPhonePermission();
        try {
            if (hasCallPermission) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                getActivity().startActivity(callIntent);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Permission was not granted",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", getActivity().getPackageName(), null));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getActivity().startActivity(intent);
        }
    }

    public void checkCallPhonePermission() {
        if (!hasCallPermission) {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.CALL_PHONE)) {
                    Toast.makeText(getContext(), "We need this permissions to make calls from the app",
                            Toast.LENGTH_LONG).show();
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CALL_PHONE},
                            PERMISSION_REQUEST_CALL_PHONE);
                }
            } else {
                hasCallPermission = true;
                //callPhone(phoneNumber);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CALL_PHONE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasCallPermission = true;
                    callPhone(phoneNumber);
                } else {
                    hasCallPermission = false;
                    Toast.makeText(getContext(), "Permission was denied",
                            Toast.LENGTH_SHORT).show();
                    adapter.setCallButtonEnabled(hasCallPermission);
                }
                break;
            case PERMISSION_REQUEST_READ_CONTACTS:
                break;
            default:
                break;
        }
        /*
        if (requestCode == PERMISSION_REQUEST_CALL_PHONE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hasCallPermission = true;
                callPhone(phoneNumber);
            } else {
                hasCallPermission = false;
                Toast.makeText(getContext(), "Permission was denied",
                        Toast.LENGTH_SHORT).show();
                adapter.setCallButtonEnabled(hasCallPermission);
            }
        }
        */
    }

    private void checkContactsPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(getContext(),
                        "We need this permission to display your contacts list",
                        Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_CONTACTS},
                        PERMISSION_REQUEST_READ_CONTACTS);
            }
        }
    }

    private void initializeTabs() {
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
}
