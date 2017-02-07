package com.mstudenets.studenetsbananaapp.controller.database;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mstudenets.studenetsbananaapp.R;
import com.mstudenets.studenetsbananaapp.model.Fruit;

import java.util.ArrayList;

public class DatabaseAdapter extends RecyclerView.Adapter<DatabaseAdapter.FruitViewHolder>
{
    private ArrayList<Fruit> fruits;
    private Context context;
    private DatabaseOperationManager operationManager = new DatabaseOperationManager(context);
    private AlertDialog.Builder dialog;

    public class FruitViewHolder extends RecyclerView.ViewHolder
    {
        TextView idTextView, nameTextView, countryTextView, priceTextView;

        public FruitViewHolder(View view) {
            super(view);
            idTextView = (TextView) view.findViewById(R.id.database_row_text_id);
            nameTextView = (TextView) view.findViewById(R.id.database_row_text_name);
            countryTextView = (TextView) view.findViewById(R.id.database_row_text_country);
            priceTextView = (TextView) view.findViewById(R.id.database_row_text_price);
        }
    }

    public DatabaseAdapter(ArrayList<Fruit> fruits, Context context) {
        this.fruits = fruits;
        this.context = context;
    }

    @Override
    public FruitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.database_row, parent, false);
        return new FruitViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FruitViewHolder viewHolder, int position) {
        Fruit fruit = fruits.get(position);
        viewHolder.idTextView.setText(String.valueOf(fruit.getId()));
        viewHolder.nameTextView.setText(fruit.getName());
        viewHolder.countryTextView.setText(fruit.getCountry());
        viewHolder.priceTextView.setText(String.valueOf(fruit.getPrice()));
    }

    @Override
    public int getItemCount() {
        return fruits.size();
    }

    public ItemTouchHelper.Callback createHelperCallback() {
        return new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)
        {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                removeRow(viewHolder.getAdapterPosition());
            }
        };
    }

    public void addItem(Fruit fruit) {
        fruits.add(fruit);
        notifyItemInserted(fruits.size());
    }

    public void removeItem(int position) {
        fruits.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, fruits.size());
    }

    private void removeRow(int position) {
        fruits.remove(position);
        this.notifyItemRemoved(position);
        operationManager.deleteRow(position);
        this.notifyItemRangeChanged(0, fruits.size());
    }
}
