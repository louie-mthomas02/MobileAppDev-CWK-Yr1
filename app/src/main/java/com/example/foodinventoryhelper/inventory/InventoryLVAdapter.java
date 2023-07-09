package com.example.foodinventoryhelper.inventory;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foodinventoryhelper.R;
import com.example.foodinventoryhelper.inventory.InventoryItemModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class InventoryLVAdapter extends ArrayAdapter<InventoryItemModel> {


    public InventoryLVAdapter(Context ctx, ArrayList<InventoryItemModel> inventoryItemModels) {
        super(ctx, 0, inventoryItemModels);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;

        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.inventory_item, parent, false);
        }

        InventoryItemModel inventoryItemModel = getItem(position);

        listItemView.setId(inventoryItemModel.getId());

        TextView itemName = listItemView.findViewById(R.id.itemName);
        TextView itemQuantity = listItemView.findViewById(R.id.itemQuantity);
        TextView itemUnits = listItemView.findViewById(R.id.itemUnits);
        ImageView itemImage = listItemView.findViewById(R.id.imageView);

        itemName.setText(inventoryItemModel.getName());
        itemQuantity.setText(Float.toString(inventoryItemModel.getQuantity()));
        itemUnits.setText(inventoryItemModel.getUnits());

        Picasso.with(getContext()).load("https://spoonacular.com/cdn/ingredients_500x500/"+ inventoryItemModel.getImg()).into(itemImage);

        return listItemView;
    }


}
