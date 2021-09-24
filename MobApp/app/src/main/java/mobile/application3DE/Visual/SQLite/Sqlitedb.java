package mobile.application3DE.Visual.SQLite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Sqlitedb {
    private static SQLiteDatabase database;
    private static Cursor cursor;
    private static boolean isBoolean;

    @SuppressLint("WrongConstant")
    public static SQLiteDatabase sqLiteDatabase(Context context) {
        try {
            database = context.openOrCreateDatabase("mydb.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
            database.setVersion(1);
            database.setLocale(Locale.getDefault());
            database.setLockingEnabled(true);
            return database;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return database;
    }

    public static void CreateTabels(Context context, String TabelName, boolean Drop, String ColumNames) {
        try {
            SQLiteDatabase database = Sqlitedb.sqLiteDatabase(context);
            if (Drop) {
                database.execSQL("DROP TABLE IF EXISTS " + TabelName);
                database.execSQL("CREATE TABLE IF NOT EXISTS " + TabelName + "(ID_No INTEGER PRIMARY KEY AUTOINCREMENT," + ColumNames + ")");
            } else {
                database.execSQL("CREATE TABLE IF NOT EXISTS " + TabelName + "(ID_No INTEGER PRIMARY KEY AUTOINCREMENT," + ColumNames + ")");
            }
            Toast.makeText(context, "Creating table " + TabelName + " successful", Toast.LENGTH_SHORT).show();
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean InsertData(Context context, String TabelName, String Colums, String Data) {

        try {
            SQLiteDatabase database = Sqlitedb.sqLiteDatabase(context);
            database.execSQL("INSERT INTO " + TabelName + "(" + Colums + ") VALUES (" + Data + ")");
            System.out.println("INSERT INTO " + TabelName + "(" + Colums + ") VALUES (" + Data + ")");
//            Toast.makeText(context, " Data saving successful", Toast.LENGTH_SHORT).show();
            database.close();
            isBoolean = true;
        } catch (Exception e) {
            isBoolean = false;
//            Toast.makeText(context, " Data saving unsuccessful", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return isBoolean;
    }

    public static ArrayList<Map<String, String>> Search(Context context, String Qury, int colum_count) {
        ArrayList<Map<String, String>> prolist2 = new ArrayList<>();

        try {
            SQLiteDatabase sqLiteDatabase = Sqlitedb.sqLiteDatabase(context);
            cursor = sqLiteDatabase.rawQuery(Qury, null);
            cursor.moveToFirst();

            if (cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    Map<String, String> map = new HashMap<>();
                    for (int j = 0; j < colum_count; j++) {
                        map.put("" + j, cursor.getString(j));
                    }
                    prolist2.add(map);
                    cursor.moveToNext();
                }
            }
            sqLiteDatabase.close();
            System.out.println(prolist2.toString());
            return prolist2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(prolist2.toString());
        return prolist2;
    }

    public static void DeleteData(Context context, String TabelDetail) {
        try {
            SQLiteDatabase database = Sqlitedb.sqLiteDatabase(context);
            database.execSQL("DELETE FROM " + TabelDetail);
            database.close();

            Toast.makeText(context, "Item delete successful", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void UpdateData(Context context, String Table_name, String ColumsANDValues, String qury) {
        try {

            SQLiteDatabase database = Sqlitedb.sqLiteDatabase(context);
            database.execSQL(" UPDATE " + Table_name + " SET " + ColumsANDValues + " WHERE " + qury);
            Toast.makeText(context, "Table " + Table_name + " Update successful", Toast.LENGTH_SHORT).show();

            database.close();
            System.out.println(" UPDATE " + Table_name + " SET " + ColumsANDValues + " WHERE " + qury);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
