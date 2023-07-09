package com.example.foodinventoryhelper.browseRecipes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foodinventoryhelper.R;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

public class RecipeGVAdapter extends ArrayAdapter<RecipeModel> {

    public RecipeGVAdapter(Context ctx, ArrayList<RecipeModel> recipeModelArrayList) {
        super(ctx, 0, recipeModelArrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;

        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.card_item, parent, false);
        }

        RecipeModel recipeModel = getItem(position);

        listItemView.setId(recipeModel.getRecipeID());
        TextView recipeTV = listItemView.findViewById(R.id.cardTxt);
        ImageView recipeIV = listItemView.findViewById(R.id.cardImg);

        recipeTV.setText(recipeModel.getRecipeName());
        Picasso.with(getContext()).load(recipeModel.getImgid()).into(recipeIV);
        return listItemView;
    }

}
