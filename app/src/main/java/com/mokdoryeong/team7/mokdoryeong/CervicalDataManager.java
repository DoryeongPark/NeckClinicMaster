package com.mokdoryeong.team7.mokdoryeong;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class CervicalDataManager {

    private DbOpenHelper dbOpenHelper;
    private Context context;

    private Deque<CervicalData> dataQueue;

    private BroadcastReceiver dataRequestReceiver;

    public CervicalDataManager(Context context){
        this.context = context;
        dbOpenHelper = new DbOpenHelper(context);
        dataQueue = new ArrayDeque<CervicalData>();
        loadData();

        dataRequestReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String strReceived = intent.getStringExtra("DataRequest");
                Log.d("Database", strReceived);
                sendDataSetForActivity();
            }
        };
        context.registerReceiver(dataRequestReceiver,
                new IntentFilter("com.mokdoryeong.team7.SEND_GRAPH_DATA_REQUEST"));
        //This is for initial data of graph view
        sendDataSetForActivity();
    }

    public void insert(CervicalData cd){
        dataQueue.addFirst(cd);
        insertToDatabase(cd);
        if(DateTime.now().minusMonths(1).isAfter(dataQueue.getLast().getStartTime().getMillis()))
            removeFromDatabase(dataQueue.pollLast());
    }

    private void loadData(){
        dbOpenHelper.open();
        Cursor c = dbOpenHelper.getAllColumns();
        while(c.moveToNext()){
            dataQueue.addFirst(new CervicalData(new DateTime(c.getLong(c.getColumnIndex(Databases.CreateDB.STARTTIME))),
                                             new DateTime(c.getLong(c.getColumnIndex(Databases.CreateDB.FINISHTIME))),
                                             c.getFloat(c.getColumnIndex(Databases.CreateDB.AVERAGEANGLE))));

        }

        dbOpenHelper.close();
        Log.d("Database", "Data is successfully loaded");
    }

    private void insertToDatabase(CervicalData cd){
        dbOpenHelper.open();
        boolean result = dbOpenHelper.insertRecord(String.valueOf(cd.getStartTime().getMillis()),
                                  String.valueOf(cd.getFinishTime().getMillis()),
                                  String.valueOf(cd.getAverageAngle()),
                                  String.valueOf(cd.getCervicalRiskIndex()));
        Log.d("Database", "Insert successfully - " + result);
        dbOpenHelper.close();
    }

    private void removeFromDatabase(CervicalData cd){
        dbOpenHelper.open();
        boolean result = dbOpenHelper.deleteRecord(String.valueOf(cd.getStartTime().getMillis()));
        Log.d("Database", "Delete successfully - " + result);
        dbOpenHelper.close();
    }

    private void sendDataSetForActivity(){
        Intent intent = new Intent("com.mokdoryeong.team7.SEND_GRAPH_DATA_RESPONSE");
        intent.putExtra("DataResponse","Data response is successfully sent");

        ArrayList<CervicalData> dataSet = new ArrayList<CervicalData>();
        for(CervicalData cd : dataQueue){
            dataSet.add(new CervicalData(new DateTime(cd.getStartTime().getMillis()),
                    new DateTime(cd.getFinishTime().getMillis()),
                    cd.getAverageAngle()));
        }
        intent.putExtra("Data", dataSet);
        context.sendBroadcast(intent);
    }

    @Override
    protected void finalize(){
        context.unregisterReceiver(dataRequestReceiver);
    }
}
