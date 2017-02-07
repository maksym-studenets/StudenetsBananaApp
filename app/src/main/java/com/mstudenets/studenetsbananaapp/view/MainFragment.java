package com.mstudenets.studenetsbananaapp.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.j256.ormlite.dao.Dao;
import com.mstudenets.studenetsbananaapp.R;
import com.mstudenets.studenetsbananaapp.controller.database.DatabaseAdapter;
import com.mstudenets.studenetsbananaapp.controller.database.DatabaseOperationManager;
import com.mstudenets.studenetsbananaapp.model.Fruit;

import java.util.ArrayList;


public class MainFragment extends Fragment
{
    private Dao<Fruit, Integer> fruitDao;
    private ArrayList<Fruit> fruits;
    private DatabaseOperationManager operationManager;
    private DatabaseAdapter adapter;
    private RecyclerView fruitView;
    private AlertDialog.Builder alertDialog;
    private View view;
    private EditText nameEdit, countryEdit, priceEdit;
    private boolean add = false;
    private int editPosition;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fragment_main_fab);
        fruitView = (RecyclerView) view.findViewById(R.id.fragment_main_recyclerview);
        //final DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        operationManager = new DatabaseOperationManager(getContext());
        fruitDao = operationManager.getFruitDao();
        fruits = operationManager.selectFromDatabase();

        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                add = true;
                removeView();
                alertDialog.setTitle("Add fruit");
                alertDialog.show();
            }
        });

        initializeRecyclerView();
        initializeDialog();

        fruits = operationManager.selectFromDatabase();
        initializeRecyclerView();

        return view;
    }

    private void initializeRecyclerView() {
        adapter = new DatabaseAdapter(fruits, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        fruitView.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(),
                layoutManager.getOrientation());
        fruitView.addItemDecoration(itemDecoration);
        adapter.createHelperCallback();
        fruitView.setAdapter(adapter);
        initializeSwipeAction();
    }

    private void initializeSwipeAction() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)
        {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    int id = fruits.get(position).getId();
                    boolean isSuccessful = operationManager.deleteRowOptimized(id);
                    if (isSuccessful)
                        adapter.removeItem(position);
                } else if (direction == ItemTouchHelper.RIGHT) {
                    removeView();
                    editPosition = position;
                    alertDialog.setTitle("Edit fruit");
                    nameEdit.setText(fruits.get(position).getName());
                    countryEdit.setText(fruits.get(position).getCountry());
                    priceEdit.setText(String.valueOf(fruits.get(position).getPrice()));
                    alertDialog.show();
                }
            }
        };
        ItemTouchHelper touchHelper = new ItemTouchHelper(simpleCallback);
        touchHelper.attachToRecyclerView(fruitView);
    }

    private void initializeDialog() {
        alertDialog = new AlertDialog.Builder(getContext());
        view = getActivity().getLayoutInflater().inflate(R.layout.main_dialog, null);
        alertDialog.setView(view);

        nameEdit = (EditText) view.findViewById(R.id.main_dialog_edit_name);
        countryEdit = (EditText) view.findViewById(R.id.main_dialog_edit_country);
        priceEdit = (EditText) view.findViewById(R.id.main_dialog_edit_price);

        alertDialog.setPositiveButton(R.string.dialog_positive_button,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameEdit.getText().toString();
                        String country = countryEdit.getText().toString();
                        double price = Double.parseDouble(priceEdit.getText().toString());

                        if (add) {
                            add = false;
                            Fruit fruit = new Fruit(name, country, price);
                            boolean isSuccessful = operationManager.addRowOptimized(fruit);
                            if (isSuccessful) {
                                adapter.addItem(fruit);
                                dialog.dismiss();
                            }
                            nameEdit.setText("");
                            countryEdit.setText("");
                            priceEdit.setText("");
                        } else {
                            int id = fruits.get(editPosition).getId();
                            Fruit fruit = new Fruit(id, name, country, price);
                            boolean isSuccessful = operationManager.updateRowOptimized(fruit);
                            if (isSuccessful) {
                                fruits.set(editPosition, fruit);
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }

                            nameEdit.setText("");
                            countryEdit.setText("");
                            priceEdit.setText("");
                        }
                    }
                });
        alertDialog.setNegativeButton(R.string.dialog_negative_button,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nameEdit.setText("");
                        countryEdit.setText("");
                        priceEdit.setText("");
                    }
                });
    }

    private void removeView() {
        if (view.getParent() != null)
            ((ViewGroup) view.getParent()).removeView(view);
    }
}
