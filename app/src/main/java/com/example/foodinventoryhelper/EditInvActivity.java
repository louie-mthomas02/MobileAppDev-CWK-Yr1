package com.example.foodinventoryhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.foodinventoryhelper.data.InventoryContract;
import com.example.foodinventoryhelper.inventory.InventoryItemModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class EditInvActivity extends AppCompatActivity {

    int selectedItemID;
    Button deleteItemBtn;
    Button updateItemBtn;
    TextInputEditText quantity;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_inv);

        Intent i = getIntent();
        selectedItemID = i.getIntExtra("selectedItemID", -1);

        title = findViewById(R.id.edit_item_header);
        String[] itemDetails = grabItemDetails(selectedItemID);

        title.setText(itemDetails[0]+" - "+itemDetails[2]);

        quantity = findViewById(R.id.quantity);
        quantity.setText(itemDetails[1]);

        updateItemBtn = findViewById(R.id.save_changes);

        updateItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //If inserting into the DB returned true, exit the AddItem Activity
                if (updateItemDetails(selectedItemID, Float.parseFloat(quantity.getText().toString()))) {
                    System.out.println("It Works!");
                    finish();
                } else { //If for whatever reason that updating did not return True, alert through console.
                    System.out.println("Didn't go into the DB!");
                }
            }
        });

        deleteItemBtn = findViewById(R.id.delete_item_btn);

        deleteItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteItemFromDB(selectedItemID)) {
                    System.out.println("It Works!");
                    SharedPreferences.Editor myEditor = getSharedPreferences("invImages", Context.MODE_PRIVATE).edit();
                    myEditor.remove(itemDetails[0]);
                    myEditor.commit();
                    finish();
                } else { //If for whatever reason that deleting did not return True, alert through console.
                    System.out.println("Didn't go into the DB!");
                }
            }
        });
    }

    public boolean deleteItemFromDB(int id){
        try {
            String[] args = {String.valueOf(id)};

            //Inserting said parameters into the database
            getContentResolver().delete(InventoryContract.Inventory_Table.CONTENT_URI, "", args);

            //If all is good, return true
            return true;
        } catch (Exception e) {

            //If there is a problem, return false
            System.out.println(e);
            return false;
        }
    }

    public boolean updateItemDetails(int id, float quantity) {
        try {
            String[] args = {String.valueOf(id), String.valueOf(quantity)};
            //Inserting said parameters into the database
            getContentResolver().update(InventoryContract.Inventory_Table.CONTENT_URI, null,"", args);

            //If all is good, return true
            return true;
        } catch (Exception e) {

            //If there is a problem, return false
            System.out.println(e);
            return false;
        }
    }

    public String[] grabItemDetails(int id) {

        String[] temp = {};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Cursor cursor = getContentResolver().query(Uri.withAppendedPath(InventoryContract.Inventory_Table.CONTENT_URI, String.valueOf(id)), new String[] {"INGREDIENT_NAME", "QUANTITY", "UNIT"},null, null);


            if (cursor.moveToFirst()) {

                do {
                    int invIndex = cursor.getColumnIndex(InventoryContract.Inventory_Table.COLUMN_INGREDIENT_NAME);
                    String name = cursor.getString(invIndex);

                    invIndex = cursor.getColumnIndex(InventoryContract.Inventory_Table.COLUMN_QUANTITY);
                    float quantity = cursor.getFloat(invIndex);

                    invIndex = cursor.getColumnIndex(InventoryContract.Inventory_Table.COLUMN_UNIT);
                    String unit = cursor.getString(invIndex);

                    temp = new String[] {name, String.valueOf(quantity), unit};

                }while(cursor.moveToNext());
            } else {
                System.out.println("No values returned :(");
            }
        } else {
            System.out.println("Invalid Software");
        }

        return temp;
    }
}