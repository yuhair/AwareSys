package com.example.hai.awaresys.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.hai.awaresys.R;
import com.example.hai.awaresys.dao.EmotionDao;
import com.example.hai.awaresys.model.Emotion;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LineChartActivity extends Activity {

    public static final String TAG = "chart";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);

        LineChart lineChart = (LineChart) findViewById(R.id.chart);
        ArrayList<String> labels = new ArrayList<String>();
        ArrayList<Entry> frusEntries = new ArrayList<>();
        ArrayList<Entry> boreEntries = new ArrayList<>();


        EmotionDao emotionDao = new EmotionDao(getApplicationContext());
        ArrayList<Emotion> frus = emotionDao.getEmotions(getTime()[0], "Frustration");
        int size = frus.size();
        ArrayList<Emotion> bore = emotionDao.getEmotions(getTime()[0], "Boredom");
        for(int i=0;i<size; i++){
            Emotion f = frus.get(i);
            Emotion b = bore.get(i);
            labels.add(f.getTime());
            frusEntries.add(new Entry(Float.parseFloat(f.getValue().substring(0,6)), i));
            boreEntries.add(new Entry(Float.parseFloat(b.getValue().substring(0,6)), i));
        }

        LineDataSet frusDataset = new LineDataSet(frusEntries, "Score of Frustration");
        LineDataSet boreDataset = new LineDataSet(boreEntries, "Score of Boredom");

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(frusDataset);
        dataSets.add(boreDataset);

        LineData data = new LineData(labels, dataSets);

        frusDataset.setDrawCubic(true);
        frusDataset.setColor(ColorTemplate.VORDIPLOM_COLORS[1]);
        frusDataset.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[1]);
        frusDataset.setDrawFilled(true);

        boreDataset.setDrawCubic(true);
        boreDataset.setColor(ColorTemplate.VORDIPLOM_COLORS[3]);
        boreDataset.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[3]);
        boreDataset.setDrawFilled(true);

        lineChart.setData(data);
        lineChart.animateY(5000);

    }

    private String[] getTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateTime = df.format(new Date());
        String[] res = dateTime.split(" ");
        return res;
    }
}
