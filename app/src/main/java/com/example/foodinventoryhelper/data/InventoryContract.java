package com.example.foodinventoryhelper.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class InventoryContract {
    private InventoryContract() {}

    //URI for ContentProvider
    public static final String CONTENT_AUTHORITY = "com.example.foodinventoryhelper";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_INVENTORY = "inventory";


    public static class Inventory_Table implements BaseColumns {

        //Uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_INVENTORY).build();
        public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/"+CONTENT_AUTHORITY+"/"+PATH_INVENTORY;
        public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/"+CONTENT_AUTHORITY+"/"+PATH_INVENTORY;

        //Table and Column Names
        public static final String TABLE_NAME = "INVENTORY_TABLE";
        public static final String COLUMN_INGREDIENT_NAME = "INGREDIENT_NAME";
        public static final String COLUMN_QUANTITY = "QUANTITY";
        public static final String COLUMN_UNIT = "UNIT";

        public static Uri buildInventoryUriWithID(long ID){
            return ContentUris.withAppendedId(CONTENT_URI,ID);
        }
    }
}
