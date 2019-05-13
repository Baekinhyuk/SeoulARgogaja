package cau.seoulargogaja.data;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class PlanListDAO {

    SQLiteDatabase database;
    String databaseName;
    String tableName;
    int mainId = 1;

    public PlanListDAO(Activity activity) {
        databaseName = "seoul";
        tableName = "planlist";

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
                        + "ID integer PRIMARY KEY autoincrement,"
                        + "name text, " // 이름
                        + "startdate text, "// 시작날짜
                        + "enddate text, " // 구역
                        + "budget integer, "  // 예산
                        + "code text " // 코드

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

    public ArrayList<PlanListDTO> selectAll() {  //전부조회하기
        ArrayList<PlanListDTO> list = new ArrayList<PlanListDTO>();
        try {

            if (database != null) {
                Cursor cursor = database.rawQuery("SELECT * FROM " + tableName, null);

                int count = cursor.getCount();
                println("결과 레코드의 갯수 : " + count);

                for (int i = 0; i < count; i++) {
                    cursor.moveToNext();
                    int id = cursor.getInt(0);
                    String name = cursor.getString(1);
                    String startdate = cursor.getString(2);
                    String enddate = cursor.getString(3);
                    int budget = cursor.getInt(4);
                    String code = cursor.getString(5);

                    println("레코드 #" + i + " : " + id + ", " + name);

                    PlanListDTO dto = new PlanListDTO(id,name,startdate,enddate,budget,code);
                    list.add(dto);
                }

                cursor.close();  //커서어댑터를 사용해서 리스트뷰에 보여질려면 클로즈를 닫아주어야함.

                println("데이터를 조회했습니다.");
            } else {
                println("데이터베이스를 먼저 열어야 합니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Spot","[PlanListDAO] ",e);
        }


        return list;
    }

    // 삭제하기전에 selectAll().size() == 1 이면 삭제 못하도록!!
    public void delete(int id) { // id 입력 받아 삭제

        try {
            if (database != null) {

                database.execSQL("delete from " + tableName + " WHERE ID = " + id);

                println(id+"번을 삭제했습니다.");
            } else {
                println("데이터베이스를 먼저 열어야 합니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PlanList", "[dao db] : 삭제 안됨. ", e);
        }
    }

    public void insert(PlanListDTO dto) {
        try {
            if (database != null) {

                database.execSQL("INSERT INTO " + tableName + "(name,startdate,enddate,budget,code) VALUES "
                        + "("
                        + "'" + dto.getName() + "',"
                        + "'" + dto.getStartDate() + "',"
                        + "'" + dto.getEndDate() + "',"
                        + "'" + dto.getBudget() + "',"
                        + "'" + dto.getCode() + "'"
                        + ")");

                println("데이터를 추가했습니다.");
            } else {
                println("데이터베이스를 먼저 열어야 합니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("tour", "[dao db] : 초기값이 안들어가짐 ", e);
        }
    }


    public void update(PlanListDTO dto) {

        try {


            database.execSQL("UPDATE " + tableName + " SET name = \'" + dto.getName() +"\'"
                    + ", startdate =\'" + dto.getStartDate() +"\'"
                    + ", enddate = \'" + dto.getEndDate() +"\'"
                    + ", budget = " + dto.getBudget()
                    + ", code = " + dto.getCode()
                    + " WHERE ID = " + dto.getId());


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("planlist", "[dao db] : planlist update 일어나지 않음", e);
        }

    }

    public void update_name(PlanListDTO dto) {
        try {
            database.execSQL("UPDATE " + tableName + " SET name = \'" + dto.getName() + "\' WHERE ID = " + dto.getId());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("planlist", "[dao db] : planlist update 일어나지 않음", e);
        }
    }
    public void update_budget(PlanListDTO dto) {
        try {
            database.execSQL("UPDATE " + tableName + " SET budget = " + dto.getBudget() + " WHERE ID = " + dto.getId());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("planlist", "[dao db] : planlist update 일어나지 않음", e);
        }
    }

    public PlanListDTO select_one(int id) {  //전부조회하기
        PlanListDTO dto = new PlanListDTO();
        try {
            if (database != null) {
                Cursor cursor = database.rawQuery("SELECT * FROM " + tableName + " WHERE ID = "+ id, null);
                int count = cursor.getCount();

                for (int i = 0; i < count; i++) {
                    cursor.moveToNext();
                    int mainid = cursor.getInt(0);
                    String name = cursor.getString(1);
                    String startdate = cursor.getString(2);
                    String enddate = cursor.getString(3);
                    int budget = cursor.getInt(4);
                    String code = cursor.getString(5);
                    dto.setId(mainid);
                    dto.setName(name);
                    dto.setStartDate(startdate);
                    dto.setEnddate(enddate);
                    dto.setBudget(budget);
                    dto.setCode(code);
                }

                cursor.close();  //커서어댑터를 사용해서 리스트뷰에 보여질려면 클로즈를 닫아주어야함.

                println("데이터를 조회했습니다.");
            } else {
                println("데이터베이스를 먼저 열어야 합니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Spot","[PlanListDAO] ",e);
        }


        return dto;
    }

    public int last_id() {  //전부조회하기
        int mainid = 1;
        try {
            if (database != null) {
                Cursor cursor = database.rawQuery("SELECT * FROM " + tableName + " ORDER BY id DESC", null);
                cursor.moveToNext();
                mainid = cursor.getInt(0);
                cursor.close();  //커서어댑터를 사용해서 리스트뷰에 보여질려면 클로즈를 닫아주어야함.
                println("데이터를 조회했습니다.");
            } else {
                println("데이터베이스를 먼저 열어야 합니다.");
            }

        } catch (Exception e) {
            Log.e("Spot","[PlanListDAO] ",e);
        }
        return mainid;
    }


    public int getMainID() {return mainId;}
    public void setMainID(int id){
        mainId = id;
    }


        private void println (String data){
            Log.d("tour", "[dao db] " + data);
        }



}





