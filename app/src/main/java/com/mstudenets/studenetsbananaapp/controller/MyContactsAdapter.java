package com.mstudenets.studenetsbananaapp.controller;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mstudenets.studenetsbananaapp.R;
import com.mstudenets.studenetsbananaapp.controller.database.DatabaseOperationManager;
import com.mstudenets.studenetsbananaapp.model.Contact;
import com.mstudenets.studenetsbananaapp.view.fragments.ContactBookFragment;
import com.mstudenets.studenetsbananaapp.view.fragments.ContactsFragment;
import com.mstudenets.studenetsbananaapp.view.fragments.MyContactsFragment;

import java.util.ArrayList;

public class MyContactsAdapter extends
        RecyclerView.Adapter<MyContactsAdapter.MyContactsViewHolder>
        implements Filterable
{
    private static final int MY_PERMISSION_REQUEST_CALL_PHONE = 110;

    private AlertDialog.Builder editDialog;
    private ArrayList<Contact> myContacts;
    private ArrayList<Contact> filteredList;
    private ContactsFragment parentFragment;
    private Context context;
    private EditText nameEdit, phoneEdit;
    private MyContactsViewHolder myViewHolder;
    private View dialogView;

    private int editPosition;
    private boolean isEditable;

    public MyContactsAdapter(ArrayList<Contact> myContacts, Context context,
                             ContactBookFragment fragment, boolean isEditable) {
        this.myContacts = myContacts;
        this.filteredList = myContacts;
        this.context = context;
        this.parentFragment = fragment;
        this.isEditable = isEditable;

        initializeEditDialog();
    }

    public MyContactsAdapter(ArrayList<Contact> myContacts, Context context,
                             MyContactsFragment fragment, boolean isEditable) {
        this.myContacts = myContacts;
        this.filteredList = myContacts;
        this.context = context;
        this.parentFragment = fragment;
        this.isEditable = isEditable;

        initializeEditDialog();
    }

    @Override
    public MyContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.contact_item, parent, false);
        myViewHolder = new MyContactsViewHolder(itemView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyContactsViewHolder viewHolder, int position) {
        final Contact contact = myContacts.get(position);
        final MyContactsViewHolder holder = viewHolder;
        viewHolder.nameTextView.setText(contact.getName());
        viewHolder.numberTextView.setText(contact.getPhoneNumber());
        viewHolder.callButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                callNumber(contact.getPhoneNumber());
            }
        });

        if (isEditable) {

            viewHolder.contactItem.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v) {
                    editPosition = holder.getAdapterPosition();
                    updateContact(editPosition);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return myContacts.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charSequence = constraint.toString();
                if (charSequence.isEmpty()) {
                    filteredList = myContacts;
                } else {
                    ArrayList<Contact> mFilteredList = new ArrayList<>();
                    for (Contact contact : myContacts) {
                        if (contact.getName().toLowerCase().contains(charSequence) ||
                                contact.getPhoneNumber().toLowerCase().contains(charSequence)) {
                            mFilteredList.add(contact);
                        }
                    }
                    filteredList = mFilteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //filteredList = (ArrayList<Contact>) results.values;
                myContacts = (ArrayList<Contact>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void setCallButtonEnabled(boolean enabled) {
        myViewHolder.callButtonEnabled(enabled);
    }

    public void addItem(Contact contact) {
        myContacts.add(contact);
        notifyItemInserted(myContacts.size());
    }

    public void removeItem(int index) {
        myContacts.remove(index);
        notifyItemRemoved(index);
        notifyItemRangeChanged(0, myContacts.size());
    }

    private void updateContact(int editPosition) {
        removeView();
        editDialog.setTitle(R.string.dialog_edit_title);
        editDialog.show();
        nameEdit.setText(myContacts.get(editPosition).getName());
        phoneEdit.setText(myContacts.get(editPosition).getPhoneNumber());
    }

    private void initializeEditDialog() {
        editDialog = new AlertDialog.Builder(context);
        Activity activity = (Activity) context;
        dialogView = activity.getLayoutInflater().inflate(R.layout.main_contact_dialog, null);
        editDialog.setView(dialogView);

        nameEdit = (EditText)
                dialogView.findViewById(R.id.dialog_contacts_edit_name);
        phoneEdit = (EditText)
                dialogView.findViewById(R.id.dialog_contacts_edit_phone);

        editDialog.setPositiveButton(R.string.dialog_positive_button,
                new DialogInterface.OnClickListener()
                {
                    DatabaseOperationManager operationManager = new DatabaseOperationManager();

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int updateId = myContacts.get(editPosition).getId();

                        String name = nameEdit.getText().toString();
                        String phone = phoneEdit.getText().toString();
                        Contact contact = new Contact(updateId, name, phone);
                        boolean isSuccessful = operationManager.updateContact(contact);
                        if (isSuccessful) {
                            myContacts.set(editPosition, contact);
                            notifyDataSetChanged();
                            dialog.dismiss();
                            nameEdit.setText("");
                            phoneEdit.setText("");
                        } else {
                            dialog.dismiss();
                            nameEdit.setText("");
                            phoneEdit.setText("");
                            Toast.makeText(context, "Error updating DB", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        editDialog.setNegativeButton(R.string.dialog_negative_button,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        nameEdit.setText("");
                        phoneEdit.setText("");
                    }
                });
    }

    private void removeView() {
        if (dialogView.getParent() != null)
            ((ViewGroup) dialogView.getParent()).removeView(dialogView);
    }

    private void callNumber(String phoneNumber) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                context.startActivity(callIntent);
            } else {
                parentFragment.callPhone(phoneNumber);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            Toast.makeText(context, "Error performing call", Toast.LENGTH_SHORT).show();
        }
    }

    class MyContactsViewHolder extends RecyclerView.ViewHolder
    {
        final ConstraintLayout contactItem;
        final TextView nameTextView;
        final TextView numberTextView;
        final ImageButton callButton;

        MyContactsViewHolder(final View view) {
            super(view);
            contactItem = (ConstraintLayout) view.findViewById(R.id.contact_item);
            nameTextView = (TextView) view.findViewById(R.id.contact_item_name);
            numberTextView = (TextView) view.findViewById(R.id.contact_item_phone);
            callButton = (ImageButton) view.findViewById(R.id.contact_item_call_button);
        }

        void callButtonEnabled(boolean enabled) {
            callButton.setEnabled(enabled);
        }

        /*
        public void addItem(Contact contact) {
            myContacts.add(contact);
            notifyItemInserted(myContacts.size());
        }

        public void removeItem(int index) {
            myContacts.remove(index);
            notifyItemRemoved(index);
            notifyItemRangeChanged(0, myContacts.size());
        }
        */
    }

    /*
    private class ContactComparator implements Comparator<Contact>
    {

        @Override
        public int compare(Contact o1, Contact o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }
    */
}