package com.example.foodinventoryhelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.view.MenuItem;

import com.example.foodinventoryhelper.browseRecipes.ViewRecipeActivity;
import com.example.foodinventoryhelper.inventory.InventoryFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.sql.Time;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener  {

    GridView recipesGV;
    BottomNavigationView bottomNavigationView;

    boolean invChange = true;

    int currentlySelected = R.id.browseRecipes;

    BrowseRecipesFragment browseRecipesFragment = new BrowseRecipesFragment();
    InventoryFragment inventoryFragment = new InventoryFragment();
    TimerFragment timerFragment = new TimerFragment();
    SettingsFragment settingsFragment = new SettingsFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Upon start-up, set screen to main activity
        setContentView(R.layout.activity_main);

        //Create a channel for notification from the timer to come through
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1", "timerNotification", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        //Find the bottom nav bar
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        //Set the navbar to be interactive with the main activity
        bottomNavigationView.setOnItemSelectedListener(this);

    }


    //Upon the onResume() call, set the item to be whatever has been currently selected by the user
    protected void onResume(){
        super.onResume();
        bottomNavigationView.setSelectedItemId(currentlySelected);
    }


    //When pausing the activity, save the fragment that the user is currently on
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("currentlySelected", currentlySelected);
    }

    //When resuming/restoring the activity, direct the activity back to the fragment that the user was on
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        currentlySelected = savedInstanceState.getInt("currentlySelected", R.id.browseRecipes);
        bottomNavigationView.setSelectedItemId(currentlySelected);
    }

    //Function for handling switching fragments in the main activity
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Grab the item tapped...
        currentlySelected = item.getItemId();
        switch (item.getItemId()) {
            //..if browse recipes, then switch to that fragment
            case R.id.browseRecipes:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, browseRecipesFragment).commit();
                return true;

            //..if inventory, then switch to that fragment
            case R.id.inventory:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, inventoryFragment).commit();
                return true;

            //..if timer, then switch to that fragment
            case R.id.timer:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, timerFragment).commit();
                return true;

            //..if settings, then switch to that fragment
            case R.id.settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, settingsFragment).commit();
                return true;
        }

        //if not a valid item, return false to not switch to anything and indicate error
        return false;
    }


    //Function to grab the recipe selected in the browse recipes fragment then bundle in an intent to pass to the 'ViewRecipeActivity'
    public void visitRecipe(View view) {
        int selectedRecipeID = view.getId();

        Intent i = new Intent(MainActivity.this, ViewRecipeActivity.class);
        i.putExtra("selectedRecipeID", selectedRecipeID);
        startActivity(i);
    }

    public void editInventory(View view) {
        int selectedItemID = view.getId();

        Intent i = new Intent(MainActivity.this, EditInvActivity.class);
        i.putExtra("selectedItemID", selectedItemID);
        startActivity(i);
    }
}