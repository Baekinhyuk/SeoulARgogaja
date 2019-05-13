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
            println("데이터베이스를 열었습니다. 테이블 이름 : " + tableName);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("tour", "[dao db] : 데이터베이스가 안열림 ", e);
        }
    }

    public void createTable() {      //테이블만들기
        println("PlanDAO 만들기.");
        try {
            if (database != null) {
                database.execSQL("CREATE TABLE if not exists " + tableName + "("    //if not exists 은 이미 있으면 만들지 않는다.
                        + "ID integer PRIMARY KEY autoincrement," //일정ID
                        + "content text, " // 내용
                        + "date text, " // 날짜
                        + "spotID integer, "  // spotID
                        + "stamp integer, "  // stamp
                        + "latitude real, "  // 위도
                        + "longitude real, " // 경도
                        + "memo text, " // 메모
                        + "order_ integer,"  // 순서
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

                    database.execSQL("INSERT INTO " + tableName + "(ID,content,date,spotID,stamp,latitude,longitude,memo,order_,datatype,planlistid) VALUES "
                            + "("
                            + "'" + dto.getId() + "',"
                            + "'" + dto.getContent() + "',"
                            + "'" + dto.getdate() + "',"
                            + "'" + dto.getspotID() + "',"
                            + "'" + dto.getStamp() + "',"
                            + "'" + dto.getLatitude() + "',"
                            + "'" + dto.getLongitude() + "',"
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
                    int stamp = cursor.getInt(4);
                    String latitude = cursor.getString(5);
                    String longitude = cursor.getString(6);
                    String memo = cursor.getString(7);
                    int order = cursor.getInt(8);
                    int datatype = cursor.getInt(9);
                    int planlistid = cursor.getInt(10);

                    PlanDTO dto = new PlanDTO(id,content,date,spotID,stamp,latitude,longitude,memo,order,planlistid);
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

    public void insert_plan(PlanDTO dto) {  //데이터 삽입하기
        Log.d("InSertPlan : ","지금시작합니다");
        try {
            int order = 0;
            int order2 = 0;
            try {
                Cursor cursor = database.rawQuery("SELECT * FROM " + tableName + " WHERE planlistid = "+dto.getplanlistid()+" ORDER BY "+tableName+".order_ DESC", null);

                int count = cursor.getCount();
                println("결과 레코드의 갯수 : " + count);
                Log.d("InSertPlan : 처음 dto Date",dto.getdate());

                for (int i = 0; i < count; i++) {
                    cursor.moveToNext();
                    String date = cursor.getString(2);
                    if(date.equals(dto.getdate())){
                        order2 = cursor.getInt(8);
                        break;
                    }
                }
                database.execSQL("UPDATE " + tableName + " SET order_ = order_ +1 WHERE order_ >"+order2 +" AND planlistid = "+dto.getplanlistid());
                order = order2+1;

            }catch (Exception e) {
                e.printStackTrace();
                Log.e("plan", "[dao db] : 값이 안들어가짐 ", e);
            }
            Log.d("Insert_plan", "Insert_plan3 : "+ Integer.toString(order));
            database.execSQL("INSERT INTO " + tableName + "(content,date,spotID,stamp,latitude,longitude,memo,order_,datatype,planlistid) VALUES "
                    + "("
                    + "'" + dto.getContent() + "',"
                    + "'" + dto.getdate() + "',"
                    + "'" + dto.getspotID() + "',"
                    + "'" + dto.getStamp() + "',"
                    + "'" + dto.getLatitude() + "',"
                    + "'" + dto.getLongitude() + "',"
                    + "'" + dto.getmemo() + "',"
                    + "'" + order + "',"
                    + "'" + dto.getdatatype() + "',"
                    + "'" + dto.getplanlistid() + "'"
                    + ")");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("plan", "[dao db] : 값이 안들어가짐 ", e);
        }
    }

    public void insert_date(ArrayList<String> dates,int planlistid) {  //날짜 데이터 삽입하기(변경시)
        try {
            int date_order = 0;
            for(String date : dates){
                PlanDTO dto = new PlanDTO(date,planlistid);
                Log.d("all_date 읽어온 날짜: ",date);
                Log.d("새로만든 dto의 planlistid : ",Integer.toString(dto.getplanlistid()));
                try {

                    Cursor cursor = database.rawQuery("SELECT * FROM " + tableName + " WHERE planlistid = "+dto.getplanlistid() +" ORDER BY "+tableName+".order_ ASC", null);

                    int count = cursor.getCount();
                    Log.d("결과 레코드의 갯수 : ",Integer.toString(count));

                    if(count == 0){
                        database.execSQL("INSERT INTO " + tableName + "(content,date,spotID,stamp,latitude,longitude,memo,order_,datatype,planlistid) VALUES "
                                + "("
                                + "'" + dto.getContent() + "',"
                                + "'" + dto.getdate() + "',"
                                + "'" + dto.getspotID() + "',"
                                + "'" + dto.getStamp() + "',"
                                + "'" + dto.getLatitude() + "',"
                                + "'" + dto.getLongitude() + "',"
                                + "'" + dto.getmemo() + "',"
                                + "'" + date_order + "',"
                                + "'" + dto.getdatatype() + "',"
                                + "'" + dto.getplanlistid() + "'"
                                + ")");
                    }

                    for (int i = 0; i < count; i++) {
                        Log.d("all_date : ",Integer.toString(i));
                        cursor.moveToNext();
                        String c_date = cursor.getString(2);
                        date_order = cursor.getInt(8);

                        if(dto.getdate().compareTo(c_date)==0){
                            break;
                        }
                        else if(dto.getdate().compareTo(c_date) < 0){
                            database.execSQL("UPDATE " + tableName + " SET order_ = order_ +1 WHERE order_ >="+date_order+" AND planlistid = "+dto.getplanlistid());
                            database.execSQL("INSERT INTO " + tableName + "(content,date,spotID,stamp,latitude,longitude,memo,order_,datatype,planlistid) VALUES "
                                    + "("
                                    + "'" + dto.getContent() + "',"
                                    + "'" + dto.getdate() + "',"
                                    + "'" + dto.getspotID() + "',"
                                    + "'" + dto.getStamp() + "',"
                                    + "'" + dto.getLatitude() + "',"
                                    + "'" + dto.getLongitude() + "',"
                                    + "'" + dto.getmemo() + "',"
                                    + "'" + date_order + "',"
                                    + "'" + dto.getdatatype() + "',"
                                    + "'" + dto.getplanlistid() + "'"
                                    + ")");
                            break;
                        }
                        else{
                            if(i == count-1) {
                                Cursor cursor2 = database.rawQuery("SELECT * FROM " + tableName + " WHERE planlistid = "+dto.getplanlistid()+" ORDER BY "+tableName+".order_ DESC", null);
                                cursor2.moveToFirst();
                                date_order= cursor2.getInt(6);
                                date_order +=1;
                                database.execSQL("INSERT INTO " + tableName + "(content,date,spotID,stamp,latitude,longitude,memo,order_,datatype,planlistid) VALUES "
                                        + "("
                                        + "'" + dto.getContent() + "',"
                                        + "'" + dto.getdate() + "',"
                                        + "'" + dto.getspotID() + "',"
                                        + "'" + dto.getStamp() + "',"
                                        + "'" + dto.getLatitude() + "',"
                                        + "'" + dto.getLongitude() + "',"
                                        + "'" + dto.getmemo() + "',"
                                        + "'" + date_order + "',"
                                        + "'" + dto.getdatatype() + "',"
                                        + "'" + dto.getplanlistid() + "'"
                                        + ")");
                                cursor2.close();
                            }
                        }

                    }


                }catch (Exception e) {
                    e.printStackTrace();
                    Log.e("plan", "[dao db] : 값이 안들어가짐 ", e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("plan", "[dao db] : 값이 안들어가짐 ", e);
        }
    }

    public ArrayList<PlanDTO> select_planlistid(int planlist_id,ArrayList<String> dates) {  //planlistid에 해당하는 내용만 조회하기
        ArrayList<PlanDTO> list = new ArrayList<PlanDTO>();
        try {

            if (database != null) {
                Cursor cursor = database.rawQuery("SELECT * FROM " + tableName + " WHERE planlistid = "+ planlist_id +" ORDER BY "+tableName+".order_ ASC", null);

                int count = cursor.getCount();

                for (int i = 0; i < count; i++) {
                    cursor.moveToNext();
                    int id = cursor.getInt(0);
                    String content = cursor.getString(1);
                    String date = cursor.getString(2);
                    int spotID = cursor.getInt(3);
                    int stamp = cursor.getInt(4);
                    String latitude = cursor.getString(5);
                    String longitude = cursor.getString(6);
                    String memo = cursor.getString(7);
                    int order = cursor.getInt(8);
                    int datatype = cursor.getInt(9);
                    int planlistid = cursor.getInt(10);

                    //날짜변경할수도 있으니 해당하는 날짜에 대한 정보만 보여주기 위해 date가 dates변경된 것을 넘겨주는것안에있을시만 작동
                    for(String date_ : dates) {
                        if(date_.equals(date)) {
                            if (datatype == 1) {
                                PlanDTO dto = new PlanDTO(id, content, date, spotID, stamp,latitude,longitude, memo, order, planlistid);
                                list.add(dto);
                                break;
                            } else if (datatype == 0) {
                                PlanDTO dto = new PlanDTO(id, date, order, planlistid);
                                list.add(dto);
                                break;
                            }
                        }
                    }
                }

                cursor.close();  //커서어댑터를 사용해서 리스트뷰에 보여질려면 클로즈를 닫아주어야함.

                println("데이터를 조회했습니다.");
            } else {
                println("데이터베이스를 먼저 열어야 합니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Plan","[PlanDAO] ",e);
        }


        return list;
    }

    public void Change_two_order(PlanDTO dto1,PlanDTO dto2) {  //데이터 두개 변경하기

        try {
            int temp_id1 = dto1.getId();
            int temp_id2 = dto2.getId();
            int temp_order1 = dto1.getOrder();
            int temp_order2 = dto2.getOrder();
            int temp_datatype1 = dto1.getdatatype();
            int temp_datatype2 = dto2.getdatatype();
            String temp_date1 = dto1.getdate();
            String temp_date2 = dto2.getdate();

            System.out.println("데이터 변경 dto1값 날짜 :"+temp_date1+" ID: "+Integer.toString(temp_id1)+" order: "+Integer.toString(temp_order1)+" datetype: "+Integer.toString(temp_datatype1));
            System.out.println("데이터 변경 dto2값 날짜 :"+temp_date2+" ID: "+Integer.toString(temp_id2)+" order: "+Integer.toString(temp_order2)+" datetype: "+Integer.toString(temp_datatype2));

            if(temp_datatype1 == temp_datatype2) {
                database.execSQL("UPDATE " + tableName + " SET order_ =" + temp_order2 + " WHERE ID=" + temp_id1);
                database.execSQL("UPDATE " + tableName + " SET order_ =" + temp_order1 + " WHERE ID=" + temp_id2);
            }
            else{
                if(temp_order1 < temp_order2) {
                    //아래로가는상황
                    database.execSQL("UPDATE " + tableName + " SET order_ =" + temp_order2 + " WHERE ID=" + temp_id1);
                    database.execSQL("UPDATE " + tableName + " SET date =\'" + temp_date2 + "\' WHERE ID=" + temp_id1);
                    database.execSQL("UPDATE " + tableName + " SET order_ =" + temp_order1 + " WHERE ID=" + temp_id2);
                }
                else{
                    //위로가는상황
                    Cursor cursor_change = database.rawQuery("SELECT * FROM " + tableName + " WHERE planlistid = "+dto1.getplanlistid()+" ORDER BY "+tableName+".order_ DESC", null);

                    int count = cursor_change.getCount();
                    System.out.println("데이터 변경 ---------------"+Integer.toString(count));
                    String temp_date3 = temp_date2;
                    for (int i = 0; i < count; i++) {
                        cursor_change.moveToNext();
                        String temp_date = cursor_change.getString(2);
                        int temp_order = cursor_change.getInt(8);
                        if(temp_order2 > temp_order){
                            temp_date3 = temp_date;
                            break;
                        }
                    }
                    database.execSQL("UPDATE " + tableName + " SET order_ =" + temp_order2 + " WHERE ID=" + temp_id1);
                    database.execSQL("UPDATE " + tableName + " SET date =\'" + temp_date3 + "\' WHERE ID=" + temp_id1);
                    database.execSQL("UPDATE " + tableName + " SET order_ =" + temp_order1 + " WHERE ID=" + temp_id2);
                    cursor_change.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("plan", "[dao db] : 값이 안 변경됨 ㅡㅡ ", e);
        }
    }

    public void test_sql_order(int planlist_id) {  //planlistid에 해당하는 내용 test용
        ArrayList<PlanDTO> list = new ArrayList<PlanDTO>();
        try {
            if (database != null) {
                Cursor cursor = database.rawQuery("SELECT * FROM " + tableName + " WHERE planlistid = "+ planlist_id +" ORDER BY "+tableName+".order_ ASC", null);

                int count = cursor.getCount();
                println("결과 레코드의 갯수 : " + count);

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
                    Log.d("TEST_SQL_RESULT","CONTENT = "+content+" DATE = "+date+" Order = "+order+" Planlistid"+planlistid);
                }

                cursor.close();  //커서어댑터를 사용해서 리스트뷰에 보여질려면 클로즈를 닫아주어야함.

                println("데이터를 조회했습니다.");
            } else {
                println("데이터베이스를 먼저 열어야 합니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Plan","[PlanDAO] ",e);
        }
    }

    public void delete_plan(PlanDTO dto) {

        try {
            if (database != null) {
                database.execSQL("delete from " + tableName + " where ID=" + dto.getId());

                println("데이터를 모두삭제 했습니다.");
            } else {
                println("데이터베이스를 먼저 열어야 합니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Wallet", "[dao db] : 삭제 안됨. ", e);
        }

    }

    public void update_plan(PlanDTO dto) {

        try {
            database.execSQL("UPDATE " + tableName + " SET content = \'" + dto.getContent() +"\'"
                    + ", date = \'" + dto.getdate() +"\'"
                    + ", spotID = " + dto.getspotID()
                    + ", stamp = " + dto.getStamp()
                    + ", latitude = \'" + dto.getLatitude() +"\'"
                    + ", longitude  = \'" + dto.getLongitude() +"\'"
                    + ", memo = \'" + dto.getmemo() +"\'"
                    + ", order_ = " + dto.getOrder()
                    + ", datatype = " + dto.getdatatype()
                    + ", planlistid = " + dto.getplanlistid()
                    + " WHERE ID = " + dto.getId());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("planlist", "[dao db] : planlist update 일어나지 않음", e);
        }

    }
}
