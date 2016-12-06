package com.mokdoryeong.team7.mokdoryeong;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FragmentGraph extends Fragment {

    GraphFlipper frag_graph_flipper;
    ExpandableListAdapter frag_graph_list_adapter;
    ExpandableListView frag_graph_list;

    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    ArrayList<CervicalData> dataArr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_graph, container, false);
        frag_graph_list = (ExpandableListView) rootView.findViewById(R.id.frag_graph_list);
        prepareListData();

        frag_graph_list_adapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
        frag_graph_list.setAdapter(frag_graph_list_adapter);
        frag_graph_flipper = (GraphFlipper)rootView.findViewById(R.id.frag_graph_flipper);

        if(dataArr != null)
            frag_graph_flipper.update(getContext(), dataArr);

        return rootView;
    }

    private void prepareListData() {

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        int count = 0;
        ArrayList<CervicalData> dataArr = ((MainActivity)getActivity()).dataArr;

        if(dataArr != null){
            float averageAngle = 0.0f;
            float averageLevel = 0.0f;

            int previousHour = dataArr.get(0).getStartTime().getHourOfDay();
            CervicalData timeSum = new CervicalData();

            for(CervicalData c : dataArr){
                if (c.getFinishTime().isBefore(DateTime.now().minusHours(6))) {
                    listDataHeader.add(timeSum.getHourTimeString());
                    List<String> specific_str = new ArrayList<String>();
                    specific_str.add(timeSum.getSpecificString());
                    listDataChild.put(listDataHeader.get(count), specific_str);
                    break;
                }
                if(previousHour != c.getStartTime().getHourOfDay()){
                    listDataHeader.add(timeSum.getHourTimeString());
                    List<String> specific_str = new ArrayList<String>();
                    specific_str.add(timeSum.getSpecificString());
                    listDataChild.put(listDataHeader.get(count), specific_str);
                    timeSum = c;
                    ++count;
                }else {
                    timeSum.add(c);
                }
                previousHour = c.getStartTime().getHourOfDay();

            }
        }

    }

    public void update() {}

    public void setData(ArrayList<CervicalData> dataArr){
        if(dataArr != null)
            this.dataArr = dataArr;
    }

}
