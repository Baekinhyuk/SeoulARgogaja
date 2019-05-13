package cau.seoulargogaja.data;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class WalletDAO {
    SQLiteDatabase database;
    String databaseName;
    String tableName;

    public WalletDAO(Activity activity) {
        databaseName = "seoul";
        tableName = "wallet";

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
                        + "ID integer PRIMARY KEY autoincrement," //가계부ID
                        + "date text, " // 날짜
                        + "planlistid integer, "// 리스트ID
                        + "detail text, " // 내역
                        + "expend integer, "  // 금액
                        + "memo text, " // 메모
                        + "datatype integer, " //데이터타입구별
                        + "main_image integer, "  // 이미지 저장용 0 : 식사, 1 : 쇼핑, 2 : 교통, 3 : 관광, 4 : 숙박, 5: 기타
                        + "sub_image integer, "  // 0 : 카드, 1 : 현금
                        + "color_type integer, "  // color_type
                        + "order_ integer "  // order
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

    public void setData(ArrayList<WalletDTO> list) {  //파싱한거 최초1회 실행
        try {
            if (database != null) {

                for (int i = 0; i < list.size(); i++) {

                    WalletDTO dto = list.get(i);

                    database.execSQL("INSERT INTO " + tableName + "(ID,date,planlistid,detail,expend,memo,datatype,main_image,sub_image,color_type,order_) VALUES "
                            + "("
                            + "'" + dto.getId() + "',"
                            + "'" + dto.getdate() + "',"
                            + "'" + dto.getplanlistid() + "',"
                            + "'" + dto.getdetail() + "',"
                            + "'" + dto.getexpend() + "',"
                            + "'" + dto.getmemo() + "',"
                            + "'" + dto.getdatatype() + "',"
                            + "'" + dto.getmain_image() + "',"
                            + "'" + dto.getsub_image() + "',"
                            + "'" + dto.getcolor_type() + "',"
                            + "'" + dto.getOrder() + "'"
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

    public ArrayList<WalletDTO> selectAll() {  //전부조회하기
        ArrayList<WalletDTO> list = new ArrayList<WalletDTO>();
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
                    String date = cursor.getString(1);
                    int planlistid = cursor.getInt(2);
                    String detail = cursor.getString(3);
                    int expend = cursor.getInt(4);
                    String memo = cursor.getString(5);
                    int datatype = cursor.getInt(6);

                    println("레코드 #" + i + " : " + id + ", " + detail);

                    WalletDTO dto = new WalletDTO(id,date,planlistid,detail,expend,memo);
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

    public void insert_date(ArrayList<String> dates,int planlistid) {  //날짜 데이터 삽입하기(변경시)
        try {
            int date_order = 0;
            for(String date : dates){
                WalletDTO dto = new WalletDTO(date,planlistid);
                Log.d("Wallet 읽어온 날짜: ",date);
                Log.d("Wallet 새로만든 dto의 DataType : ",Integer.toString(dto.getdatatype()));
                try {

                    Cursor cursor = database.rawQuery("SELECT * FROM " + tableName + " WHERE planlistid = "+dto.getplanlistid() +" ORDER BY "+tableName+".order_ ASC", null);

                    int count = cursor.getCount();
                    Log.d(" Wallet 결과 레코드의 갯수  : ",Integer.toString(count));

                    if(count == 0){
                        database.execSQL("INSERT INTO " + tableName + "(date,planlistid,detail,expend,memo,datatype,main_image,sub_image,color_type,order_) VALUES "
                                + "("
                                + "'" + dto.getdate() + "',"
                                + "'" + dto.getplanlistid() + "',"
                                + "'" + dto.getdetail() + "',"
                                + "'" + dto.getexpend() + "',"
                                + "'" + dto.getmemo() + "',"
                                + "'" + dto.getdatatype() + "',"
                                + "'" + dto.getmain_image() + "',"
                                + "'" + dto.getsub_image() + "',"
                                + "'" + dto.getcolor_type() + "',"
                                + "'" + date_order + "'"
                                + ")");
                    }

                    for (int i = 0; i < count; i++) {
                        Log.d("Wallet all_date : ",Integer.toString(i));
                        cursor.moveToNext();
                        String c_date = cursor.getString(1);
                        date_order = cursor.getInt(10);

                        if(dto.getdate().compareTo(c_date)==0){
                            break;
                        }
                        else if(dto.getdate().compareTo(c_date) < 0){
                            database.execSQL("UPDATE " + tableName + " SET order_ = order_ +1 WHERE order_ >="+date_order);
                            database.execSQL("INSERT INTO " + tableName + "(date,planlistid,detail,expend,memo,datatype,main_image,sub_image,color_type,order_) VALUES "
                                    + "("
                                    + "'" + dto.getdate() + "',"
                                    + "'" + dto.getplanlistid() + "',"
                                    + "'" + dto.getdetail() + "',"
                                    + "'" + dto.getexpend() + "',"
                                    + "'" + dto.getmemo() + "',"
                                    + "'" + dto.getdatatype() + "',"
                                    + "'" + dto.getmain_image() + "',"
                                    + "'" + dto.getsub_image() + "',"
                                    + "'" + dto.getcolor_type() + "',"
                                    + "'" + date_order + "'"
                                    + ")");
                            break;
                        }
                        else{
                            if(i == count-1) {
                                Cursor cursor2 = database.rawQuery("SELECT * FROM " + tableName + " WHERE planlistid = "+dto.getplanlistid() +" ORDER BY "+tableName+".order_ DESC", null);
                                cursor2.moveToFirst();
                                date_order= cursor2.getInt(10);
                                date_order +=1;
                                database.execSQL("INSERT INTO " + tableName + "(date,planlistid,detail,expend,memo,datatype,main_image,sub_image,color_type,order_) VALUES "
                                        + "("
                                        + "'" + dto.getdate() + "',"
                                        + "'" + dto.getplanlistid() + "',"
                                        + "'" + dto.getdetail() + "',"
                                        + "'" + dto.getexpend() + "',"
                                        + "'" + dto.getmemo() + "',"
                                        + "'" + dto.getdatatype() + "',"
                                        + "'" + dto.getmain_image() + "',"
                                        + "'" + dto.getsub_image() + "',"
                                        + "'" + dto.getcolor_type() + "',"
                                        + "'" + date_order + "'"
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

    public ArrayList<WalletDTO> select_planlistid(int planlist_id,ArrayList<String> dates) {  //planlistid에 해당하는 내용만 조회하기
        ArrayList<WalletDTO> list = new ArrayList<WalletDTO>();
        try {

            if (database != null) {
                Cursor cursor = database.rawQuery("SELECT * FROM " + tableName + " WHERE planlistid = "+ planlist_id +" ORDER BY "+tableName+".order_ ASC", null);

                int count = cursor.getCount();
                println("결과 레코드의 갯수 : " + count);

                for (int i = 0; i < count; i++) {
                    cursor.moveToNext();
                    int id = cursor.getInt(0);
                    String date = cursor.getString(1);
                    int planlistid = cursor.getInt(2);
                    String detail = cursor.getString(3);
                    int expend = cursor.getInt(4);
                    String memo = cursor.getString(5);
                    int datatype = cursor.getInt(6);
                    int main_image = cursor.getInt(7);
                    int sub_image = cursor.getInt(8);
                    int color_type = cursor.getInt(9);
                    int order = cursor.getInt(10);

                    //날짜변경할수도 있으니 해당하는 날짜에 대한 정보만 보여주기 위해 date가 dates변경된 것을 넘겨주는것안에있을시만 작동
                    for(String date_ : dates) {
                        if(date_.equals(date)) {
                            if (datatype == 1) {
                                WalletDTO dto = new WalletDTO(id, date, planlistid, detail, expend, memo, main_image, sub_image, color_type, order);
                                list.add(dto);
                                break;
                            } else if (datatype == 0) {
                                WalletDTO dto = new WalletDTO(id, date, order, planlistid);
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

    public void insert_wallet_item(WalletDTO dto) {  //데이터 삽입하기
        Log.d("Wallet_Plan : ","지금시작합니다");
        try {
            int order = 0;
            int order2 = 0;
            try {
                Cursor cursor = database.rawQuery("SELECT * FROM " + tableName + " WHERE planlistid = "+dto.getplanlistid()+" ORDER BY "+tableName+".order_ DESC", null);

                int count = cursor.getCount();
                println("Wallet_Plan 결과 레코드의 갯수 : " + count);
                Log.d("Wallet_Plan : 처음 dto Date",dto.getdate());

                for (int i = 0; i < count; i++) {
                    cursor.moveToNext();
                    String date = cursor.getString(1);
                    Log.d("Wallet_Plan : 검색중 dto Date",date);
                    if(date.equals(dto.getdate())){
                        order2 = cursor.getInt(10);
                        Log.d("Wallet_Plan : 검색중 dto order2",Integer.toString(order2));

                        break;
                    }
                }
                database.execSQL("UPDATE " + tableName + " SET order_ = order_ +1 WHERE order_ >"+order2);
                order = order2+1;
            }catch (Exception e) {
                e.printStackTrace();
            }
            database.execSQL("INSERT INTO " + tableName + "(date,planlistid,detail,expend,memo,datatype,main_image,sub_image,color_type,order_) VALUES "
                    + "("
                    + "'" + dto.getdate() + "',"
                    + "'" + dto.getplanlistid() + "',"
                    + "'" + dto.getdetail() + "',"
                    + "'" + dto.getexpend() + "',"
                    + "'" + dto.getmemo() + "',"
                    + "'" + dto.getdatatype() + "',"
                    + "'" + dto.getmain_image() + "',"
                    + "'" + dto.getsub_image() + "',"
                    + "'" + dto.getcolor_type() + "',"
                    + "'" + order + "'"
                    + ")");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("plan", "[dao db] : 값이 안들어가짐 ", e);
        }
    }

    public void test_sql_order(int planlist_id) {  //planlistid에 해당하는 내용 test용
        ArrayList<WalletDTO> list = new ArrayList<WalletDTO>();
        try {
            if (database != null) {
                Cursor cursor = database.rawQuery("SELECT * FROM " + tableName + " WHERE planlistid = "+ planlist_id +" ORDER BY "+tableName+".order_ ASC", null);

                int count = cursor.getCount();
                println("결과 레코드의 갯수 : " + count);

                for (int i = 0; i < count; i++) {
                    cursor.moveToNext();
                    int id = cursor.getInt(0);
                    String date = cursor.getString(1);
                    int planlistid = cursor.getInt(2);
                    String detail = cursor.getString(3);
                    int expend = cursor.getInt(4);
                    String memo = cursor.getString(5);
                    int datatype = cursor.getInt(6);
                    int main_image = cursor.getInt(7);
                    int sub_image = cursor.getInt(8);
                    int color_type = cursor.getInt(9);
                    int order = cursor.getInt(10);
                    Log.d("Wallet_Plan TEST_SQL_RESULT : ","detail = "+detail+" DATE = "+date+" Order = "+order);
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

    public void Change_two_order(WalletDTO dto1,WalletDTO dto2) {  //데이터 두개 변경하기

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
                test_sql_order(dto1.getplanlistid());
            }
            /*날짜에따른 date변경다시해야함..... Date 적용 오류.... 다른경우 dto1이 data고 dto2가 날짜인경우만 존재*/
            else{
                if(temp_order1 < temp_order2) {
                    //아래로가는상황
                    database.execSQL("UPDATE " + tableName + " SET order_ =" + temp_order2 + " WHERE ID=" + temp_id1);
                    database.execSQL("UPDATE " + tableName + " SET date =\'" + temp_date2 + "\' WHERE ID=" + temp_id1);
                    database.execSQL("UPDATE " + tableName + " SET order_ =" + temp_order1 + " WHERE ID=" + temp_id2);
                    test_sql_order(dto1.getplanlistid());
                }
                else{
                    //위로가는상황
                    Cursor cursor_change = database.rawQuery("SELECT * FROM " + tableName + " WHERE planlistid = "+dto1.getplanlistid()+" ORDER BY "+tableName+".order_ DESC", null);

                    int count = cursor_change.getCount();
                    System.out.println("데이터 변경 ---------------"+Integer.toString(count));
                    String temp_date3 = temp_date2;
                    for (int i = 0; i < count; i++) {
                        cursor_change.moveToNext();
                        String temp_date = cursor_change.getString(1);
                        int temp_order = cursor_change.getInt(10);
                        if(temp_order2 > temp_order){
                            temp_date3 = temp_date;
                            break;
                        }
                    }
                    database.execSQL("UPDATE " + tableName + " SET order_ =" + temp_order2 + " WHERE ID=" + temp_id1);
                    database.execSQL("UPDATE " + tableName + " SET date =\'" + temp_date3 + "\' WHERE ID=" + temp_id1);
                    database.execSQL("UPDATE " + tableName + " SET order_ =" + temp_order1 + " WHERE ID=" + temp_id2);
                    test_sql_order(dto1.getplanlistid());
                    cursor_change.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("plan", "[dao db] : 값이 안 변경됨 ㅡㅡ ", e);
        }
    }

    public int sum_expend(int planlist_id, String day) {  //planlistid에 해당하는 내용 test용
        ArrayList<WalletDTO> list = new ArrayList<WalletDTO>();
        int sum = 0;
        println("sum_expend 날짜 : " + day);
        try {
            if (database != null) {
                Cursor cursor = database.rawQuery("SELECT expend FROM " + tableName + " WHERE planlistid = "+ planlist_id +" AND date = \'"+day+"\' ORDER BY "+tableName+".order_ ASC", null);

                int count = cursor.getCount();
                println("sum_expend 결과 레코드의 갯수 : " + count);

                for (int i = 0; i < count; i++) {
                    cursor.moveToNext();
                    int expend = cursor.getInt(0);
                    sum += expend;
                    Log.d("Wallet_Plan sum_expend : ","expend = "+expend+" SUM = "+sum);
                }

                cursor.close();  //커서어댑터를 사용해서 리스트뷰에 보여질려면 클로즈를 닫아주어야함.
                println("데이터를 조회했습니다.");
                return sum;
            } else {
                println("데이터베이스를 먼저 열어야 합니다.");
                return sum;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return sum;
        }
    }

    public int sum_expend_all(int planlist_id, ArrayList<String> dates) {  //planlistid에 해당하는 내용 test용
        ArrayList<WalletDTO> list = new ArrayList<WalletDTO>();
        int sum = 0;
        try {
            if (database != null) {
                Cursor cursor = database.rawQuery("SELECT * FROM " + tableName + " WHERE planlistid = "+ planlist_id +" ORDER BY "+tableName+".order_ ASC", null);

                int count = cursor.getCount();
                println("sum_expend_all 결과 레코드의 갯수 : " + count);

                for (int i = 0; i < count; i++) {
                    cursor.moveToNext();
                    String date = cursor.getString(1);
                    int expend = cursor.getInt(4);
                    println("sum_expend_all sum : " + sum);
                    println("sum_expend_all expend : " + expend);
                    for(String date_ : dates) {
                        if(date_.equals(date)) {
                            sum += expend;
                        }
                    }
                }
                println("sum_expend_all 결과 레코드의 갯수 : " + sum);
                cursor.close();  //커서어댑터를 사용해서 리스트뷰에 보여질려면 클로즈를 닫아주어야함.
                println("데이터를 조회했습니다.");
                return sum;
            } else {
                println("데이터베이스를 먼저 열어야 합니다.");
                return sum;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return sum;
        }
    }

    public void update_wallet(WalletDTO dto) {
        try {
            database.execSQL("UPDATE " + tableName + " SET date = \'" + dto.getdate() +"\'"
                    + ", planlistid = " + dto.getplanlistid()
                    + ", detail = \'" + dto.getdetail()+"\'"
                    + ", expend = " + dto.getexpend()
                    + ", memo = \'" + dto.getmemo() +"\'"
                    + ", datatype = " + dto.getdatatype()
                    + ", main_image = " + dto.getmain_image()
                    + ", sub_image = " + dto.getsub_image()
                    + ", color_type = " + dto.getcolor_type()
                    + ", order_ = " + dto.getOrder()
                    + " WHERE ID = " + dto.getId());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("planlist", "[dao db] : planlist update 일어나지 않음", e);
        }

    }

    public void delete_wallet(WalletDTO dto) {

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

    public int[] sum_expend_cm(int planlist_id, ArrayList<String> dates) {  //planlistid에 해당하는 내용 test용
        ArrayList<WalletDTO> list = new ArrayList<WalletDTO>();
        int[] sum = new int[2];
        int card = 0;
        int money = 0;
        try {
            if (database != null) {
                Cursor cursor = database.rawQuery("SELECT * FROM " + tableName + " WHERE planlistid = "+ planlist_id +" ORDER BY "+tableName+".order_ ASC", null);

                int count = cursor.getCount();
                println("sum_expend_all 결과 레코드의 갯수 : " + count);

                for (int i = 0; i < count; i++) {
                    cursor.moveToNext();
                    String date = cursor.getString(1);
                    int expend = cursor.getInt(4);
                    int sub_image = cursor.getInt(8);
                    for(String date_ : dates) {
                        if(date_.equals(date)) {
                            if(sub_image == 0){
                                card += expend;
                            }
                            else {
                                money += expend;
                            }
                        }
                    }
                }
                cursor.close();  //커서어댑터를 사용해서 리스트뷰에 보여질려면 클로즈를 닫아주어야함.
                println("데이터를 조회했습니다.");
                sum[0] = card;
                sum[1] = money;
                return sum;
            } else {
                println("데이터베이스를 먼저 열어야 합니다.");
                return sum;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return sum;
        }
    }
}
