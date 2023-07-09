package com.example.foodinventoryhelper.inventory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.foodinventoryhelper.R;
import com.example.foodinventoryhelper.data.InventoryContract;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddItemActivity extends AppCompatActivity {

    TextInputLayout ingredientNameInputField;
    TextInputLayout quantityInputField;
    Spinner unitSpinner;
    Button confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_item);


        //Locate the input fields and store into variables to reference
        ingredientNameInputField = (TextInputLayout) findViewById(R.id.ingredientNameInput);
        quantityInputField = (TextInputLayout) findViewById(R.id.quantityInput);
        unitSpinner = (Spinner) findViewById(R.id.unitSpinner);


        //Locate the confirm button and store in a variable
        confirmBtn = (Button) findViewById(R.id.confirm_add_item);

        //Give the confirm button an onClick action
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //Grab the inputs from the form, and cast into respective data types
                    String name = String.valueOf(ingredientNameInputField.getEditText().getText()).trim();
                    Float quantity = Float.valueOf(String.valueOf(quantityInputField.getEditText().getText()));
                    String unit = unitSpinner.getSelectedItem().toString();

                    //If inserting into the DB returned true, exit the AddItem Activity
                    if (insertItemIntoDB(name, quantity, unit)) {
                        System.out.println("It Works!");
                        finish();
                    } else { //If for whatever reason that inserting into the DB did not return True, alert through console.
                        System.out.println("Didn't go into the DB!");
                    }

                } catch (Exception e) { //If invalid inputs, deny from entering into the DB
                    System.out.println("Something went wrong!");
                }
            }
        });


        //Create the ArrayAdapter for the Spinner with the dropdown selections
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.units,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(adapter);

    }

    public boolean insertItemIntoDB(String name, Float quantity, String unit){
        try {
            //Putting the parameters into a ContentValues object
            ContentValues values = new ContentValues();
            values.put("INGREDIENT_NAME", name);
            values.put("QUANTITY", quantity);
            values.put("UNIT", unit);

            //Inserting said parameters into the database
            getContentResolver().insert(InventoryContract.Inventory_Table.CONTENT_URI, values);

            //If all is good, return true
            return true;
        } catch (Exception e) {

            //If there is a problem, return false
            System.out.println(e);
            return false;
        }
    }
}