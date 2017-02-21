package com.mstudenets.studenetsbananaapp.controller.contacts;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mstudenets.studenetsbananaapp.R;
import com.mstudenets.studenetsbananaapp.model.Contact;
import com.mstudenets.studenetsbananaapp.view.activities.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MyContactsAdapter extends RecyclerView.Adapter<MyContactsAdapter.MyContactsViewHolder>
{
    /*
    private List<Contact> contacts;
    private ArrayList<Contact> mContactsArray;
    private Context context;

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
*/

    private static final int MY_PERMISSION_REQUEST_CALL_PHONE = 110;

    private ArrayList<Contact> myContacts;
    //private final DatabaseOperationManager operationManager;
    private AlertDialog.Builder alertDialog;
    private Context context;

    class MyContactsViewHolder extends RecyclerView.ViewHolder
    {
        final TextView nameTextView;
        final TextView numberTextView;
        final ImageButton callButton;

        MyContactsViewHolder(View view) {
            super(view);
            nameTextView = (TextView) view.findViewById(R.id.contact_item_name);
            numberTextView = (TextView) view.findViewById(R.id.contact_item_phone);
            callButton = (ImageButton) view.findViewById(R.id.contact_item_call_button);
        }
    }

    public MyContactsAdapter(ArrayList<Contact> myContacts, Context context) {
        this.myContacts = myContacts;
        Collections.sort(myContacts, new ContactComparator());
        this.context = context;
    }

    @Override
    public MyContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.contact_item, parent, false);
        return new MyContactsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyContactsViewHolder viewHolder, int position) {
        final Contact contact = myContacts.get(position);
        viewHolder.nameTextView.setText(contact.getName());
        viewHolder.numberTextView.setText(contact.getPhoneNumber());
        viewHolder.callButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                callNumber(contact.getPhoneNumber());
                checkCallPhonePermission();
            }
        });
    }

    @Override
    public int getItemCount() {
        return myContacts.size();
    }

    class ContactComparator implements Comparator<Contact>
    {

        @Override
        public int compare(Contact o1, Contact o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }

    private boolean checkCallPhonePermission() {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    (MainActivity) context, Manifest.permission.CALL_PHONE)) {
                ConstraintLayout root = (ConstraintLayout)
                        ((MainActivity) context).findViewById(R.id.contact_book_layout);
                Snackbar.make(root, "This app needs the permission to make calls",
                        Snackbar.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions((MainActivity) context,
                        new String[] {Manifest.permission.CALL_PHONE},
                        MY_PERMISSION_REQUEST_CALL_PHONE);
            }
        }

        return true;
    }

    private void callNumber(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale((MainActivity) context,
                    Manifest.permission.CALL_PHONE)) {
                return;
            } else {
                ActivityCompat.requestPermissions((MainActivity) context,
                        new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSION_REQUEST_CALL_PHONE);
            }
            context.startActivity(callIntent);
        } else {
            Toast.makeText(context, "Call permission was not granted",
                    Toast.LENGTH_SHORT).show();
        }
    }
}