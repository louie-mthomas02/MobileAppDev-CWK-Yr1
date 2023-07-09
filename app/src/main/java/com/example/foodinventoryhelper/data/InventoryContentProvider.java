package com.example.foodinventoryhelper.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.FrameLayout;

public class InventoryContentProvider extends ContentProvider {

    public static final int INVENTORY = 1;
    public static final int INVENTORY_WITH_ID = 2;

    private static final UriMatcher myUriMatcher = buildUriMatcher();
    public static InventoryDBHelper myDBHelper;


    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, INVENTORY);
        matcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY+"/#", INVENTORY_WITH_ID);

        return matcher;
    }

    public InventoryContentProvider() {}

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs){
        int match_code = myUriMatcher.match(uri);

        switch(match_code){
            case INVENTORY:{
                myDBHelper.deleteItemFromTable(Integer.parseInt(selectionArgs[0]));
                break;
            }
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        return 0;
    }

    @Override
    public String getType(Uri uri){
        int match_code = myUriMatcher.match(uri);

        switch(match_code){
            case INVENTORY:
                return InventoryContract.Inventory_Table.CONTENT_TYPE_DIR;
            case INVENTORY_WITH_ID:
                return InventoryContract.Inventory_Table.CONTENT_TYPE_ITEM;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values){
        int match_code = myUriMatcher.match(uri);
        Uri retUri = null;

        switch(match_code){
            case INVENTORY:{
                SQLiteDatabase db = myDBHelper.getWritableDatabase();
                long _id = db.insert(InventoryContract.Inventory_Table.TABLE_NAME,null,values);
                if (_id > 0) {
                    retUri = InventoryContract.Inventory_Table.buildInventoryUriWithID(_id);
                }
                else
                    throw new SQLException("failed to insert");
                break;
            }
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        return retUri;
    }

    public boolean onCreate() {
        myDBHelper = new InventoryDBHelper(getContext(), InventoryDBHelper.DB_NAME, null, InventoryDBHelper.DB_VERSION);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match_code = myUriMatcher.match(uri);
        Cursor myCursor;

        switch(match_code) {
            case INVENTORY:
                SQLiteDatabase db = myDBHelper.getWritableDatabase();
                myCursor = db.query(
                        InventoryContract.Inventory_Table.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        null
                );
                break;
            case INVENTORY_WITH_ID:
                myCursor = myDBHelper.getReadableDatabase().query(
                        InventoryContract.Inventory_Table.TABLE_NAME,
                        projection,
                        InventoryContract.Inventory_Table._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Not Yet Implemented");
        }

        return myCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs){

        int match_code = myUriMatcher.match(uri);

        switch(match_code){
            case INVENTORY:{
                myDBHelper.updateItemQuantity(Integer.parseInt(selectionArgs[0]), Float.valueOf(selectionArgs[1]));
                break;
            }
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        return 0;
    }
}
