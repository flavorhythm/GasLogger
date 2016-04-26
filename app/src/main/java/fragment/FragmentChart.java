package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.zenoyuki.flavorhythm.gaslogger.R;

import java.util.ArrayList;
import java.util.List;

import util.MpgCalculator;

/**
 * Created by zyuki on 4/25/2016.
 */
public class FragmentChart extends Fragment {
    private TextView mpgAvg;
    private LineChart chart;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutRes = R.layout.fragment_chart;
        View customView = inflater.inflate(layoutRes, container, false);

        mpgAvg = (TextView)customView.findViewById(R.id.chartFrag_text_mpgAvg);
        chart = (LineChart)customView.findViewById(R.id.chartFrag_chart);
        updateAverage();

        List<Entry> entryList = new ArrayList<>();
        List<String> xList = new ArrayList<>();
        xList.add("T1"); xList.add("T2"); xList.add("T3"); xList.add("T4");
        Entry entry1 = new Entry(1.0f, 0);
        Entry entry2 = new Entry(1.5f, 1);
        Entry entry3 = new Entry(2.0f, 2);
        Entry entry4 = new Entry(2.5f, 3);
        entryList.add(entry1); entryList.add(entry2); entryList.add(entry3); entryList.add(entry4);
        LineDataSet dataset = new LineDataSet(entryList, "test");
        LineData data = new LineData(xList, dataset);

        chart.setData(data);

        chart.getLegend().setEnabled(false);

        return customView;
    }

    public void updateAverage() {
        mpgAvg.setText(MpgCalculator.calculate(getActivity().getApplicationContext()));
    }
}
