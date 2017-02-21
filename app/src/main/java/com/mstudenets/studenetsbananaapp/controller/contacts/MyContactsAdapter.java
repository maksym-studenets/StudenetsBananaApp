package com.mstudenets.studenetsbananaapp.controller.contacts;


import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mstudenets.studenetsbananaapp.R;
import com.mstudenets.studenetsbananaapp.model.Contact;

import java.util.ArrayList;
import java.util.List;

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

    private ArrayList<Contact> myContacts;
    //private final DatabaseOperationManager operationManager;
    private AlertDialog.Builder alertDialog;

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
        //this.operationManager = new DatabaseOperationManager(context);
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
        /*
        viewHolder.callButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //callNumber(contact.getPhoneNumber());
            }
        });
*/
    }

    @Override
    public int getItemCount() {
        return myContacts.size();
    }
}

    /*
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
                        new String[] {
                        Manifest.permission.CALL_PHONE});
            }
            context.startActivity(callIntent);
        }
    }
    */

    /*
        @Override
    public int getItemCount() {
        return myContacts.size();
    }
    */
//}
