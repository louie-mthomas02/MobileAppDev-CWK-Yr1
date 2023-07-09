package com.example.foodinventoryhelper.browseRecipes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.example.foodinventoryhelper.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class ViewRecipeActivity extends AppCompatActivity {

    int selectedRecipeID;
    boolean isUserVeg, userGlutenFree, userDairyFree;
    boolean recipeVeg, recipeGF, recipeDF;
    Button cookedBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);

        SharedPreferences sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        isUserVeg = sharedPreferences.getBoolean("isVegetarian", false);
        userGlutenFree = sharedPreferences.getBoolean("glutenFree", false);
        userDairyFree = sharedPreferences.getBoolean("lactoseIntolerant", false);

        Intent i = getIntent();
        selectedRecipeID = i.getIntExtra("selectedRecipeID", -1);

        GrabRecipeDetails temp = new GrabRecipeDetails();
        temp.execute();

        cookedBtn = findViewById(R.id.confirmCooking);
        cookedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    class GrabRecipeDetails extends AsyncTask<Void, Void, String> {
        String json = "";

        @Override
        protected String doInBackground(Void... strings) {

            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()){

                String url = "https://api.spoonacular.com/recipes/" +String.valueOf(selectedRecipeID)+ "/information?apiKey=f9279c5d21914592978fdd7fcf4de364";
                json = GET(url);
            }
            else {
                System.out.println("No Internet Connection!");
            }

            return json;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObj = new JSONObject(s);

                TextView recipeTitle = (TextView) findViewById(R.id.recipeTitleHeader);
                recipeTitle.setText(jsonObj.getString("title"));

                recipeVeg = jsonObj.getBoolean("vegetarian");
                recipeGF = jsonObj.getBoolean("glutenFree");
                recipeDF = jsonObj.getBoolean("dairyFree");

                CardView warningCard = (CardView) findViewById(R.id.warningCard);
                View warningSep = findViewById(R.id.warningSeparator);

                if ((isUserVeg && !recipeVeg) || (userGlutenFree && !recipeGF) || (userDairyFree && !recipeDF)){
                    warningSep.setVisibility(View.VISIBLE);
                    warningCard.setVisibility(View.VISIBLE);

                    TextView vegWarning = (TextView) findViewById(R.id.vegWarning);
                    TextView GFWarning = (TextView) findViewById(R.id.glutenWarning);
                    TextView DFWarning = (TextView) findViewById(R.id.dairyWarning);

                    if (isUserVeg && !recipeVeg) {
                        vegWarning.setVisibility(View.VISIBLE);
                    }
                    if (userGlutenFree && !recipeGF) {
                        GFWarning.setVisibility(View.VISIBLE);
                    }
                    if (userDairyFree && !recipeDF) {
                        DFWarning.setVisibility(View.VISIBLE);
                    }
                }

                ImageView recipeImg = (ImageView) findViewById(R.id.recipeImg);
                Picasso.with(getApplicationContext()).load(jsonObj.getString("image")).into(recipeImg);

                TextView recipeSummary = (TextView) findViewById(R.id.recipeSummary);
                recipeSummary.setText(Html.fromHtml(jsonObj.getString("summary")));

                TextView recipeIngredients = (TextView) findViewById(R.id.specifyIngredients);
                recipeIngredients.setText("");
                JSONArray ingredientRequirements = jsonObj.getJSONArray("extendedIngredients");

                for (int i = 0; i < ingredientRequirements.length(); i++) {
                    JSONObject ingredient = ingredientRequirements.getJSONObject(i);

                    recipeIngredients.append("- "+ingredient.getString("original") + "\n");
                }
                recipeIngredients.setText(recipeIngredients.getText().toString().trim());



                TextView recipeInstructions = (TextView) findViewById(R.id.specifyInstructions);
                recipeInstructions.setText("");

                JSONArray instructions = jsonObj.getJSONArray("analyzedInstructions");

                for (int i = 0; i < instructions.length(); i++) {

                    JSONArray instructionSet = instructions.getJSONObject(i).getJSONArray("steps");

                    for (int j = 0; j < instructionSet.length(); j++) {
                        String step = instructionSet.getJSONObject(j).getString("step");

                        recipeInstructions.append(String.valueOf(j+1) + ". " + step+"\n\n");
                    }

                    recipeInstructions.append("\n");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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