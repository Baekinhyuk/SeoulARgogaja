package cau.seoulargogaja.data;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class HospitalDAO  {
    SQLiteDatabase database;
    String databaseName;
    String tableName;

    public HospitalDAO(Activity activity) {
        databaseName = "seoul";
        tableName = "hospital";

        try {
            database = activity.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null);
            println("데이터베이스를 열었습니다. : " + databaseName);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("hospital", "[dao db] : 데이터베이스가 안열림 ", e);
        }
    }

    public void createTable() {      //테이블만들기
        try {
            if (database != null) {
                database.execSQL("CREATE TABLE if not exists " + tableName + "("    //if not exists 은 이미 있으면 만들지 않는다.
                        + "ID integer PRIMARY KEY autoincrement,"
                        + "name text, " // 이름
                        + "latitude real, "  // 위도
                        + "longitude real, " // 경도
                        + "address text, " // 주소
                        + "phone text" // 전화번호
                        + ")");
                println("테이블을 만들었습니다. : " + tableName);
            } else {
                println("데이터베이스를 먼저 열어야 합니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("hospital", "[dao db] : 테이블생성이 안됨 ", e);
        }
    }

    public void setData(ArrayList<HospitalDTO> list) {  //파싱한거 최초1회 실행
        try {
            if (database != null) {

                for (int i = 0; i < list.size(); i++) {

                    HospitalDTO dto = list.get(i);

                    database.execSQL("INSERT INTO " + tableName + "(ID,name,latitude,longitude,address,phone) VALUES "
                            + "("
                            +  Integer.parseInt(dto.getId()) + ","
                            + "'" + dto.getName() + "',"
                            + "'" + dto.getLatitude() + "',"
                            + "'" + dto.getLongitude() + "',"
                            + "'" + dto.getAddress() + "',"
                            + "'" + dto.getPhone() + "'"
                            + ")");
                }
                println("데이터를 추가했습니다.");
            } else {
                println("데이터베이스를 먼저 열어야 합니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("hospital", "[dao db] : 초기값이 안들어가짐 ", e);
        }
    }



    public ArrayList<HospitalDTO> selectAll() {  //전부조회하기
        ArrayList<HospitalDTO> list = new ArrayList<HospitalDTO>();
        try {

            if (database != null) {
                Cursor cursor = database.rawQuery("SELECT * FROM " + tableName, null);

                int count = cursor.getCount();
                println("결과 레코드의 갯수 : " + count);

                /*startManagingCursor(cursor);
                String[] columns = new String[] {"_id", "age", "mobile"};
                int[] to = new int[] {R.id.editText3, R.id.editText4, R.id.editText5};
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.customer_item, cursor, columns, to);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged(); //리스트뷰가 업데이트 되는거.*/


                for (int i = 0; i < count; i++) {
                    cursor.moveToNext();
                    int id = cursor.getInt(0);
                    String name = cursor.getString(1);
                    String latitude = cursor.getString(2);
                    String longitude = cursor.getString(3);
                    String address = cursor.getString(4);
                    String phone = cursor.getString(5);

                    println("레코드 #" + i + " : " + id + ", " + name);

                    HospitalDTO dto = new HospitalDTO(id+"",name,latitude,longitude,address,phone);
                    list.add(dto);
                }

                cursor.close();  //커서어댑터를 사용해서 리스트뷰에 보여질려면 클로즈를 닫아주어야함.

                println("데이터를 조회했습니다.");
            } else {
                println("데이터베이스를 먼저 열어야 합니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Hospital","[HospitalDAO] ",e);
        }


        return list;
    }

    public void delete() {

        try {
            if (database != null) {
                database.execSQL("delete from " + tableName);

                println("데이터를 모두삭제 했습니다.");
            } else {
                println("데이터베이스를 먼저 열어야 합니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("hospital", "[dao db] : 삭제 안됨. ", e);
        }
    }



    private void println(String data) {
        Log.d("hospital", "[dao db] " + data);
    }


}
