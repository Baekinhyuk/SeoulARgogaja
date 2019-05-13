package cau.seoulargogaja;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cau.seoulargogaja.adapter.SelectListAdapter;
import cau.seoulargogaja.data.IdDAO;
import cau.seoulargogaja.data.MainState;
import cau.seoulargogaja.data.PlanListDAO;
import cau.seoulargogaja.data.PlanListDTO;

public class SelectPlanlist extends AppCompatActivity {

    private ListView listview;
    ArrayList<PlanListDTO> list = new ArrayList<>();
    SelectListAdapter selectListAdapter;
    MainState mainState;
    IdDAO iddao;
    PlanListDAO listdao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_list);

        listdao = new PlanListDAO(this);
        iddao = new IdDAO(this);
        list = getPlanList();

        listview = (ListView)findViewById(R.id.select_list);

        selectListAdapter = new SelectListAdapter(getApplicationContext(),list);
        listview.setAdapter(selectListAdapter);
        listview.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        PlanListDTO temp1 = list.get(position);
                        iddao.update(temp1.getId());
                        PlanListDTO mainPlan = listdao.select_one(iddao.select());
                        mainState = new MainState(mainPlan);
                        finish();
                    }
                });

    }

    public ArrayList<PlanListDTO> getPlanList() {
        PlanListDAO dao = new PlanListDAO(this);
        return dao.selectAll();
    }
}
