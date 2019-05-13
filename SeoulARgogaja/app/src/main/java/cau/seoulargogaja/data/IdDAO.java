package cau.seoulargogaja.data;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class IdDAO {

    SQLiteDatabase database;
    String databaseName;
    String tableName;

    public IdDAO(Activity activity) {
        databaseName = "seoul";
        tableName = "main_id";

        try {
            database = activity.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("tour", "[dao db] : 데이터베이스가 안열림 ", e);
        }
    }

    public void createTable() {      //테이블만들기
        try {
            if (database != null) {
                database.execSQL("CREATE TABLE if not exists " + tableName + "("    //if not exists 은 이미 있으면 만들지 않는다.
                        + "ID integer PRIMARY KEY"
                        + ")");
                database.execSQL("INSERT INTO " + tableName + "(ID) VALUES "
                        + "(" + 1 + ")");
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("iddao", "[dao db] : 테이블생성이 안됨 ", e);
        }
    }

    public int select() {
        int id = 1;
        try {
            if (database != null) {
                Cursor cursor = database.rawQuery("SELECT * FROM " + tableName, null);
                int count = cursor.getCount();
                for (int i = 0; i < count; i++) {
                    cursor.moveToNext();
                    id = cursor.getInt(0);
                    MainState mainState = new MainState();
                    mainState.setplanlistId(id);
                }
                cursor.close();  //커서어댑터를 사용해서 리스트뷰에 보여질려면 클로즈를 닫아주어야함.
            } else {
                Log.d("iddao 실패", Integer.toString(id));
            }
        } catch (Exception e) {
            Log.d("iddao catch", Integer.toString(id));
        }
        return id;
    }

    public void update(int id) {
        try {
            database.execSQL("UPDATE " + tableName + " SET ID = " + id);
        } catch (Exception e) {
            Log.e("planlist", "[dao db] : planlist update 일어나지 않음", e);
        }
    }
}





