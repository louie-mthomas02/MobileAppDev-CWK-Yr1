package com.example.foodinventoryhelper;

import android.content.Context;
import android.database.Cursor;
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
import android.widget.GridView;

import com.example.foodinventoryhelper.browseRecipes.RecipeGVAdapter;
import com.example.foodinventoryhelper.browseRecipes.RecipeModel;
import com.example.foodinventoryhelper.data.InventoryContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class BrowseRecipesFragment extends Fragment {

    GridView recipesGV;
    ArrayList<RecipeModel> recipeModelArrayList = new ArrayList<RecipeModel>();
    RecipeGVAdapter adapter;


    public BrowseRecipesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_browse_recipes, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        recipesGV = getView().findViewById(R.id.idGVrecipes);

        adapter = new RecipeGVAdapter(getActivity(), recipeModelArrayList);

        if (recipeModelArrayList.isEmpty() || ((MainActivity)getActivity()).invChange){
            ((MainActivity)getActivity()).invChange = false;

            GrabRecipesTask temp = new GrabRecipesTask();
            temp.execute();
        } else {
            recipesGV.setAdapter(adapter);
        }

    }

    class GrabRecipesTask extends AsyncTask<Void, Void, String> {

        String json = "";

        @Override
        protected String doInBackground(Void... strings) {

            recipeModelArrayList.clear();

            ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()){
                String url = "https://api.spoonacular.com/recipes/findByIngredients?apiKey=f9279c5d21914592978fdd7fcf4de364&number=10&ranking=1&ignorePantry=true&ingredients=" + grabInventoryNames();
                json = GET(url);
                System.out.println("API CALL!");
            }
            else {
                System.out.println("No Internet Connection!");
            }

            return json;
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONArray jsonArr = new JSONArray(s);

                for (int i = 0; i < jsonArr.length(); i++)
                {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    System.out.println(jsonObj);

                    recipeModelArrayList.add(new RecipeModel(jsonObj.getString("title"), jsonObj.getString("image"), jsonObj.getInt("id")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            recipesGV.setAdapter(adapter);
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


    public String grabInventoryNames() {
        String inventoryList = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Cursor cursor = getContext().getContentResolver().query(InventoryContract.Inventory_Table.CONTENT_URI, new String[] {"INGREDIENT_NAME"},null, null);


            if (cursor.moveToFirst()) {

                int invIndex = cursor.getColumnIndex(InventoryContract.Inventory_Table.COLUMN_INGREDIENT_NAME);
                String name = cursor.getString(invIndex).replace(" ","-");

                inventoryList = inventoryList.concat(name);

                while(cursor.moveToNext()){
                    invIndex = cursor.getColumnIndex(InventoryContract.Inventory_Table.COLUMN_INGREDIENT_NAME);
                    name = cursor.getString(invIndex).replace(" ","-");

                    inventoryList = inventoryList.concat(","+name);
                }
            } else {
                System.out.println("No values returned :(");
            }
        }

        return inventoryList;
    }
}