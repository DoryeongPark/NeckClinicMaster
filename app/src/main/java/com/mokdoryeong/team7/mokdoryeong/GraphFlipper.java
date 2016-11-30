package com.mokdoryeong.team7.mokdoryeong;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;


public class GraphFlipper  extends LinearLayout {

    public static int countIndexes = 3;
    LinearLayout buttonLayout;
    ImageView[] indexButtons;
    View[] views;
    ViewFlipper flipper;
    int currentIndex = 0;
    Button left_graph;
    Button right_graph;

    Context context;

    ArrayList<Drawable> graph;
    private ArrayList<CervicalData> dataArr;
    int color[];

    public GraphFlipper(Context context) {
        super(context);
        this.context = context;

    }

    public GraphFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

    }

    public void update(Context context, ArrayList<CervicalData> dataArr) {

        color = new int[3];
        color[0] = context.getResources().getColor(R.color.a_graph1);
        color[1] = context.getResources().getColor(R.color.a_graph2);
        color[2] = context.getResources().getColor(R.color.a_graph3);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.graph_flip, this, true);

        buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);
        flipper = (ViewFlipper) findViewById(R.id.flipper);

        graphInit(context);

        indexButtons = new ImageView[countIndexes];
        views = new TextView[countIndexes];

        LayoutParams params = new LayoutParams( 40, 40);
        params.leftMargin = 30;

        for(int i = 0; i < countIndexes; i++) {
            indexButtons[i] = new ImageView(context);

            if (i == currentIndex) {
                setBackgroundColor(color[i]);
                indexButtons[i].setImageResource(R.drawable.frag_graph_point_selec);
            } else {
                indexButtons[i].setImageResource(R.drawable.frag_graph_point_none);
            }
            indexButtons[i].setPadding(10, 10, 10, 10);
            buttonLayout.addView(indexButtons[i], params);

            if(dataArr != null) {
                GraphView curView = new GraphView(context);
                curView.setMode(i);
                curView.update(dataArr);
                flipper.addView(curView);
            }
        }

        left_graph = (Button) findViewById(R.id.leftGraph);
        right_graph = (Button) findViewById(R.id.rightGraph);
        left_graph.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                flipper.setInAnimation(AnimationUtils.loadAnimation(getContext(),
                        R.anim.wallpaper_open_enter));
                flipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(),
                        R.anim.wallpaper_open_exit));

                if (currentIndex > 0) {
                    flipper.showPrevious();
                    currentIndex--;
                    updateIndexes();
                }
            }
        });
        right_graph.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                flipper.setInAnimation(AnimationUtils.loadAnimation(getContext(),
                        R.anim.wallpaper_open_enter));
                flipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(),
                        R.anim.wallpaper_open_exit));

                if (currentIndex < (countIndexes-1)) {
                    flipper.showNext();
                    currentIndex++;
                    updateIndexes();
                }
            }
        });

    }

    private void updateIndexes() {
        for(int i = 0; i < countIndexes; i++) {
            if (i == currentIndex) {
                setBackgroundColor(color[i]);
                indexButtons[i].setImageResource(R.drawable.frag_graph_point_selec);
            } else {
                indexButtons[i].setImageResource(R.drawable.frag_graph_point_none);
            }
        }
    }

    private void graphInit(Context context)
    {
        graph = new ArrayList<Drawable>();
        Drawable daily_graph  = ContextCompat.getDrawable(context, R.drawable.frag_graph_day);
        Drawable week_graph  = ContextCompat.getDrawable(context, R.drawable.frag_graph_week);
        Drawable month_graph  = ContextCompat.getDrawable(context, R.drawable.frag_graph_month);
        graph.add(daily_graph);
        graph.add(week_graph);
        graph.add(month_graph);

    }
}
