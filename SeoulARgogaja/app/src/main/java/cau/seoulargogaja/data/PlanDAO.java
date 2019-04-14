package cau.seoulargogaja.data;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class PlanDAO {
    SQLiteDatabase database;
    String databaseName;
    String tableName;

    public PlanDAO(Activity activity) {
        databaseName = "seoul";
        tableName = "plan";

        try {
            database = activity.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null);
            println("데이터베이스를 열었습니다. : " + databaseName);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("tour", "[dao db] : 데이터베이스가 안열림 ", e);
        }
    }

    public void createTable() {      //테이블만들기
        try {
            if (database != null) {
                database.execSQL("CREATE TABLE if not exists " + tableName + "("    //if not exists 은 이미 있으면 만들지 않는다.
                        + "ID integer PRIMARY KEY autoincrement," //일정ID
                        + "content text, " // 내용
                        + "date text, " // 날짜
                        + "spotID integer, "  // spotID
                        + "customID integer, "  // customID
                        + "memo text, " // 메모
                        + "order integer, "  // 순서
                        + "datatype integer, " //데이터타입구별
                        + "planlistid integer "// 리스트ID
                        + ")");
                println("테이블을 만들었습니다. : " + tableName);
            } else {
                println("데이터베이스를 먼저 열어야 합니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("tour", "[dao db] : 테이블생성이 안됨 ", e);
        }
    }

    public void setData(ArrayList<PlanDTO> list) {  //파싱한거 최초1회 실행
        try {
            if (database != null) {

                for (int i = 0; i < list.size(); i++) {

                    PlanDTO dto = list.get(i);

                    database.execSQL("INSERT INTO " + tableName + "(ID,content,date,spotID,customID,memo,order,datatype,planlistid) VALUES "
                            + "("
                            + "'" + dto.getId() + "',"
                            + "'" + dto.getContent() + "',"
                            + "'" + dto.getdate() + "',"
                            + "'" + dto.getspotID() + "',"
                            + "'" + dto.getcustomID() + "',"
                            + "'" + dto.getmemo() + "',"
                            + "'" + dto.getOrder() + "',"
                            + "'" + dto.getdatatype() + "',"
                            + "'" + dto.getplanlistid() + "'"
                            + ")");
                }
                println("데이터를 추가했습니다.");
            } else {
                println("데이터베이스를 먼저 열어야 합니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("tour", "[dao db] : 초기값이 안들어가짐 ", e);
        }
    }



    public ArrayList<PlanDTO> selectAll() {  //전부조회하기
        ArrayList<PlanDTO> list = new ArrayList<PlanDTO>();
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
                    String content = cursor.getString(1);
                    String date = cursor.getString(2);
                    int spotID = cursor.getInt(3);
                    int customID = cursor.getInt(4);
                    String memo = cursor.getString(5);
                    int order = cursor.getInt(6);
                    int datatype = cursor.getInt(7);
                    int planlistid = cursor.getInt(8);

                    PlanDTO dto = new PlanDTO(id,content,date,spotID,customID,memo,order,planlistid);
                    list.add(dto);
                }

                cursor.close();  //커서어댑터를 사용해서 리스트뷰에 보여질려면 클로즈를 닫아주어야함.

                println("데이터를 조회했습니다.");
            } else {
                println("데이터베이스를 먼저 열어야 합니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Wallet","[WalletDAO] ",e);
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
            Log.e("Wallet", "[dao db] : 삭제 안됨. ", e);
        }
    }

    private void println(String data) {
        Log.d("tour", "[dao db] " + data);
    }

}
