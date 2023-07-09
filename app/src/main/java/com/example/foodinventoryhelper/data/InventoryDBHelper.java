package com.example.foodinventoryhelper.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InventoryDBHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;

    public static final String DB_NAME = "inventory.db";

    public SQLiteDatabase myDB;

    public InventoryDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        myDB = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        myDB = db;
        String query = "CREATE TABLE IF NOT EXISTS " +
                InventoryContract.Inventory_Table.TABLE_NAME +
                " (" +
                InventoryContract.Inventory_Table._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                InventoryContract.Inventory_Table.COLUMN_INGREDIENT_NAME + " TEXT NOT NULL," +
                InventoryContract.Inventory_Table.COLUMN_QUANTITY + " REAL NOT NULL," +
                InventoryContract.Inventory_Table.COLUMN_UNIT + " TEXT NOT NULL );";

        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS "+InventoryContract.Inventory_Table.TABLE_NAME);
        onCreate(db);
    }

    public void clearTable(){
        myDB.execSQL("DELETE FROM "+InventoryContract.Inventory_Table.TABLE_NAME);
    }

    public void deleteItemFromTable(int id){
        myDB.execSQL("DELETE FROM "+InventoryContract.Inventory_Table.TABLE_NAME+" WHERE "+InventoryContract.Inventory_Table._ID + " = '"+String.valueOf(id)+"'");
    }

    public void updateItemQuantity(int id, float quantity) {
        myDB.execSQL("UPDATE "+InventoryContract.Inventory_Table.TABLE_NAME+" SET "+InventoryContract.Inventory_Table.COLUMN_QUANTITY+"='"+String.valueOf(quantity)+"' WHERE "+InventoryContract.Inventory_Table._ID + " = '"+String.valueOf(id)+"'");
    }
}
