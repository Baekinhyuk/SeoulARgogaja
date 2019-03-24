package cau.seoulargogaja;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.design.widget.FloatingActionButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import java.util.Arrays;
import java.util.List;

public class PlanFragment extends Fragment{

    //TEST
    List<TravelLocation> list = Arrays.asList(
            new TravelLocation("여행지1", "여행지정보1"),
            new TravelLocation("여행지2", "여행지정보2"),
            new TravelLocation("여행지3", "여행지정보3"),
            new TravelLocation("여행지5", "여행지정보5"),
            new TravelLocation("여행지6", "여행지정보5"),
            new TravelLocation("여행지7", "여행지정보5"),
            new TravelLocation("여행지8", "여행지정보5"),
            new TravelLocation("여행지9", "여행지정보5"),
            new TravelLocation("여행지0", "여행지정보5"),
            new TravelLocation("여행지10", "여행지정보5"),
            new TravelLocation("여행지11", "여행지정보5"),
            new TravelLocation("여행지12", "여행지정보5"),
            new TravelLocation("여행지13", "여행지정보5"),
            new TravelLocation("여행지14", "여행지정보5"),
            new TravelLocation("여행지15", "여행지정보5"),
            new TravelLocation("여행지4", "여행지정보4")
    );



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_plan, container, false);


        final CustomListView listView = (CustomListView)rootView.findViewById(R.id.listView1);
        PlanAdapter adapter = new PlanAdapter(getActivity(), list, new PlanAdapter.Listener() {
            @Override
            public void onGrab(int position, RelativeLayout row) {
                listView.onGrab(position, row);
            }
        });

        listView.setAdapter(adapter);
        listView.setListener(new CustomListView.Listener() {
            @Override
            public void swapElements(int indexOne, int indexTwo) {
                TravelLocation temp = list.get(indexOne);
                list.set(indexOne, list.get(indexTwo));
                list.set(indexTwo, temp);
            }
        });


        FloatingActionButton fab = (FloatingActionButton)rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        return rootView;
    }


}
