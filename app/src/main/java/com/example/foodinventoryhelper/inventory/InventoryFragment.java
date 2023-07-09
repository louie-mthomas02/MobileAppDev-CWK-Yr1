package com.example.foodinventoryhelper.inventory;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.foodinventoryhelper.R;
import com.example.foodinventoryhelper.data.InventoryContentProvider;
import com.example.foodinventoryhelper.data.InventoryContract;
import com.example.foodinventoryhelper.data.InventoryDBHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class InventoryFragment extends Fragment {



    InventoryContentProvider inventoryContentProvider = new InventoryContentProvider();
    private InventoryDBHelper DBHelper;
    private SQLiteDatabase db;

    ListView inventoryLV;

    FloatingActionButton addItemBtn;

    ArrayList<InventoryItemModel> inventoryItemModels;
    InventoryLVAdapter adapter;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEditor;

    private boolean gotImgs;

    public InventoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Initialise the DB helper to interact with
        DBHelper = new InventoryDBHelper(getContext(),InventoryDBHelper.DB_NAME,null,InventoryDBHelper.DB_VERSION);
        db = DBHelper.getWritableDatabase();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inventory, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        inventoryLV = getView().findViewById(R.id.idLVinventory);
        inventoryItemModels = grabInventory();

        if (inventoryItemModels.isEmpty()) {
            createTestInventory();
            inventoryItemModels = grabInventory();
        }

        //Locate the AddItem Button and store in a variable
        addItemBtn = (FloatingActionButton) getView().findViewById(R.id.add_item_btn);

        //Add an onClick action to the button that directs the user to the AddItem Activity
        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), AddItemActivity.class);
                startActivity(i);
            }
        });
    }

    public ArrayList<InventoryItemModel> grabInventory() {
        ArrayList<InventoryItemModel> inventoryList = new ArrayList<InventoryItemModel>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Cursor cursor = getContext().getContentResolver().query(InventoryContract.Inventory_Table.CONTENT_URI, new String[] {"_ID, INGREDIENT_NAME", "QUANTITY", "UNIT"},null, null);


            if (cursor.moveToFirst()) {

                do {
                    int invIndex = cursor.getColumnIndex(InventoryContract.Inventory_Table._ID);
                    int id = cursor.getInt(invIndex);

                    invIndex = cursor.getColumnIndex(InventoryContract.Inventory_Table.COLUMN_INGREDIENT_NAME);
                    String name = cursor.getString(invIndex);

                    invIndex = cursor.getColumnIndex(InventoryContract.Inventory_Table.COLUMN_QUANTITY);
                    float quantity = cursor.getFloat(invIndex);

                    invIndex = cursor.getColumnIndex(InventoryContract.Inventory_Table.COLUMN_UNIT);
                    String unit = cursor.getString(invIndex);

                    inventoryList.add(new InventoryItemModel(id, name, quantity, unit));
                }while(cursor.moveToNext());
            } else {
                System.out.println("No values returned :(");
            }
        } else {
            System.out.println("Invalid Software");
        }

        return inventoryList;
    }

    public void createTestInventory() {
        ContentValues values = new ContentValues();
        values.put("INGREDIENT_NAME", "Milk");
        values.put("QUANTITY", 250);
        values.put("UNIT", "ml");
        getContext().getContentResolver().insert(InventoryContract.Inventory_Table.CONTENT_URI, values);

        values.put("INGREDIENT_NAME", "Butter");
        values.put("QUANTITY", 200);
        values.put("UNIT", "g");
        getContext().getContentResolver().insert(InventoryContract.Inventory_Table.CONTENT_URI, values);

        values.put("INGREDIENT_NAME", "Pork");
        values.put("QUANTITY", 1000);
        values.put("UNIT", "g");
        getContext().getContentResolver().insert(InventoryContract.Inventory_Table.CONTENT_URI, values);

        values.put("INGREDIENT_NAME", "Cheese");
        values.put("QUANTITY", 100);
        values.put("UNIT", "g");
        getContext().getContentResolver().insert(InventoryContract.Inventory_Table.CONTENT_URI, values);

        values.put("INGREDIENT_NAME", "Broccoli");
        values.put("QUANTITY", 150);
        values.put("UNIT", "g");
        getContext().getContentResolver().insert(InventoryContract.Inventory_Table.CONTENT_URI, values);
    }

    public void emptyInventory() {
        DBHelper.clearTable();
    }

    @Override
    public void onResume(){
        super.onResume();

        PhotoGetter photoGetter = new PhotoGetter();
        gotImgs = false;

        inventoryItemModels = grabInventory();
        adapter = new InventoryLVAdapter(getActivity(), inventoryItemModels);
        inventoryLV.setAdapter(adapter);

        sharedPreferences = getActivity().getSharedPreferences("invImages", Context.MODE_PRIVATE);
        myEditor = sharedPreferences.edit();

        for (InventoryItemModel temp: inventoryItemModels) {
            temp.setImg(sharedPreferences.getString(temp.getName(), null));

            if (temp.getImg() == null) {
                temp.setNeedToGetImg(true);
            }
        }

        photoGetter.execute(inventoryItemModels);
    }

    class PhotoGetter extends AsyncTask<ArrayList<InventoryItemModel>, Void, ArrayList<InventoryItemModel>> {
        String json = "";
        private final ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Grabbing Inventory");
            this.dialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<InventoryItemModel> inventoryItemModels) {
            super.onPostExecute(inventoryItemModels);
            this.dialog.dismiss();
            myEditor.commit();
            adapter = new InventoryLVAdapter(getActivity(), inventoryItemModels);
            inventoryLV.setAdapter(adapter);
        }

        @Override
        protected ArrayList<InventoryItemModel> doInBackground(ArrayList<InventoryItemModel>... models) {

            ArrayList<InventoryItemModel> inventoryItemModels1 = models[0];
            System.out.println(inventoryItemModels1.size());

            ConnectivityManager connMgr = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            for (InventoryItemModel temp: inventoryItemModels1) {
                if (temp.isNeedToGetImg()) {
                    if (networkInfo != null && networkInfo.isConnected()){
                        String url = "https://api.spoonacular.com/food/ingredients/search?apiKey=f9279c5d21914592978fdd7fcf4de364&number=1&query=" + temp.getName().toLowerCase();
                        json = GET(url);
                        System.out.println("API CALL!");
                    }
                    else {
                        System.out.println("No Internet Connection!");
                    }

                    try {
                        JSONObject jsonObj = new JSONObject(json).getJSONArray("results").getJSONObject(0);
                        temp.setImg(jsonObj.getString("image"));
                        myEditor.putString(temp.getName(), temp.getImg());
                    } catch (JSONException e) {
                        temp.setImg("kale.jpg");
                    }
                } else {
                    continue;
                }
            }
            gotImgs=true;
            return inventoryItemModels1;
        }

        public String GET(String url) {
            InputStream inputStream = null;
            String result = "";
            URL request = null;

            try {
                request = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            HttpsURLConnection conn = null;

            try {
                conn = (HttpsURLConnection) request.openConnection();
                conn.connect();
                inputStream = conn.getInputStream();
                if (inputStream != null) {
                    result = convertInputStreamToString(inputStream);
                } else {
                    result = "Didn't Work!";
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            return result;
        }

        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            Scanner s = new Scanner(inputStream).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";
            inputStream.close();
            return result;
        }
    }

}