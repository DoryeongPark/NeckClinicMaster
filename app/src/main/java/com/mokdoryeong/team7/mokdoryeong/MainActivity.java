package com.mokdoryeong.team7.mokdoryeong;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //Data from database
    public ArrayList<CervicalData> dataArr;
    private int averageAngle;
    TextView main_top_text;

    ArrayList<Drawable> icons = new ArrayList<Drawable>();
    ArrayList<Drawable> iconsHighlighted = new ArrayList<Drawable>();
    String[] topString = {"홈", "설정", "진단"};

    Drawable main_home_none;
    Drawable main_home_select;
    Drawable main_option_none;
    Drawable main_option_select;
    Drawable main_graph_none;
    Drawable main_graph_select;

    FragmentHome fragmentHome;
    FragmentOption fragmentOption;
    FragmentGraph fragmentGraph;

    SectionsPagerAdapter pagerAdapter;
    ViewPager main_pager;
    TabLayout main_tabs;

    public static Typeface doh_type;
    public static Typeface old_L_type;
    public static Typeface jua_type;

    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        public int getCount() {return 3;}
        public Fragment getItem(int paramInt)
        {
            if (paramInt == 0)
                return MainActivity.this.fragmentHome;
            else if (paramInt == 1)
                return MainActivity.this.fragmentOption;
            else if (paramInt == 2)
                return MainActivity.this.fragmentGraph;
            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }

    private BroadcastReceiver dataResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String strReceived = intent.getStringExtra("DataResponse");
            ArrayList<CervicalData> dataArr = (ArrayList<CervicalData>)intent.getSerializableExtra("Data");
            if(strReceived != null && dataArr != null) {
                MainActivity.this.dataArr = dataArr;
                if(dataArr != null) {
                    Log.d("Database", "Data transferred");
                    int count = 0;
                    averageAngle = 0;
                    for (CervicalData c : dataArr) {
                        averageAngle += c.getAverageAngle();
                        ++count;
                        if (c.getFinishTime().isBefore(DateTime.now().minusHours(6)))
                            break;
                    }
                    averageAngle /= count;
                    fragmentGraph.setData(dataArr);
                    fragmentHome.angle = averageAngle;
                    fragmentHome.update();
                }
                Log.d("Database", strReceived);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        doh_type = Typeface.createFromAsset(getAssets(), "fonts/doh.ttf");
        old_L_type = Typeface.createFromAsset(getAssets(), "fonts/a_old_L.ttf");
        jua_type = Typeface.createFromAsset(getAssets(), "fonts/jua.ttf");

        main_top_text = (TextView) findViewById(R.id.main_top_text);
        main_top_text.setTypeface(doh_type);
        main_top_text.setText(topString[0]);

        main_home_none = ContextCompat.getDrawable(this, R.drawable.main_home_none);
        main_home_select =  ContextCompat.getDrawable(this,R.drawable.main_home_selec);
        main_option_none =  ContextCompat.getDrawable(this,R.drawable.main_option_none);
        main_option_select =  ContextCompat.getDrawable(this,R.drawable.main_option_selec);
        main_graph_none =  ContextCompat.getDrawable(this,R.drawable.main_graph_none);
        main_graph_select =  ContextCompat.getDrawable(this,R.drawable.main_graph_selec);
        icons.add(main_home_none);
        icons.add(main_option_none);
        icons.add(main_graph_none);
        iconsHighlighted.add(main_home_select);
        iconsHighlighted.add(main_option_select);
        iconsHighlighted.add(main_graph_select);

        main_tabs = (TabLayout) findViewById(R.id.main_tabs);

        fragmentHome = new FragmentHome();
        fragmentOption = new FragmentOption();
        fragmentGraph = new FragmentGraph();


        pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        main_pager = (ViewPager) findViewById(R.id.main_pager);
        main_pager.setAdapter(pagerAdapter);

        main_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageSelected(int position) {
                main_tabs.getTabAt(position).setIcon(iconsHighlighted.get(position));
                main_tabs.getTabAt((3+position-1)%3).setIcon(icons.get((3+position-1)%3));
                main_tabs.getTabAt((3+position+1)%3).setIcon(icons.get((3+position+1)%3));
                main_top_text.setText(topString[position]);

                if(position == 0)
                    fragmentHome.update();
                else if (position == 1)
                    fragmentOption.update();
                else if (position == 2)
                    fragmentGraph.update();

            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        main_tabs.setupWithViewPager(main_pager);
        main_tabs.getTabAt(0).setIcon(iconsHighlighted.get(0));
        main_tabs.getTabAt(1).setIcon(icons.get(1));
        main_tabs.getTabAt(2).setIcon(icons.get(2));
    }

    private void loadCervicalDataFromService(){
        registerReceiver(dataResponseReceiver,
                new IntentFilter("com.mokdoryeong.team7.SEND_GRAPH_DATA_RESPONSE"));
        Intent intent = new Intent("com.mokdoryeong.team7.SEND_GRAPH_DATA_REQUEST");
        intent.putExtra("DataRequest", "Data request is successfully sent");
        sendBroadcast(intent);
    }

    public ArrayList<CervicalData> getData(){
        return dataArr;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(BackgroundService.isAlive){
            loadCervicalDataFromService();
            stopService(new Intent(MainActivity.this, BackgroundService.class));
        }else {
            startService(new Intent(MainActivity.this, BackgroundService.class));
            loadCervicalDataFromService();
            stopService(new Intent(MainActivity.this, BackgroundService.class));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(BackgroundService.isChecked) {
            if(fragmentHome.isOpeningActivity()) {
                return;
            }
            startService(new Intent(MainActivity.this, BackgroundService.class));
            loadCervicalDataFromService();

        }

        unregisterReceiver(dataResponseReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
