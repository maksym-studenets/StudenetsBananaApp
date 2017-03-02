package com.mstudenets.studenetsbananaapp.controller.contacts;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mstudenets.studenetsbananaapp.R;
import com.mstudenets.studenetsbananaapp.model.Contact;
import com.mstudenets.studenetsbananaapp.view.activities.MainActivity;
import com.mstudenets.studenetsbananaapp.view.fragments.ContactsFragment;

import java.util.ArrayList;
import java.util.Comparator;

public class MyContactsAdapter extends RecyclerView.Adapter<MyContactsAdapter.MyContactsViewHolder>
{
    private static final int MY_PERMISSION_REQUEST_CALL_PHONE = 110;
    private PermissionsCheckable permissionsCheckable;

    private ArrayList<Contact> myContacts;
    private AlertDialog.Builder alertDialog;
    private Context context;
    private ContactsFragment fragment;

    class MyContactsViewHolder extends RecyclerView.ViewHolder implements Filterable
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

        private void disableCallButton() {
            callButton.setEnabled(false);
        }

        private void enableCallButton() {
            callButton.setEnabled(true);
        }

        @Override
        public Filter getFilter() {
            return null;
        }
    }

    public MyContactsAdapter(ArrayList<Contact> myContacts, Context context,
                             Fragment fragment) {
        this.myContacts = myContacts;
        //Collections.sort(myContacts, new ContactComparator());
        this.context = context;
        this.fragment = (ContactsFragment) fragment;
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
            }
        });
    }

    @Override
    public int getItemCount() {
        return myContacts.size();
    }

    public ItemTouchHelper.Callback createHelperCallback() {
        return new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            }
        };
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

    }
    */

    public void addItem(Contact contact) {
        myContacts.add(contact);
        notifyItemInserted(myContacts.size());
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
        //(MainActivity) context.chec
        /*
        Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            context.startActivity(callIntent);
            */
    }

    private class ContactComparator implements Comparator<Contact>
    {

        @Override
        public int compare(Contact o1, Contact o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }

    private class ContactFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

        }
    }
}