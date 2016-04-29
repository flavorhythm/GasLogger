package fragment;

import android.content.Context;
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

import java.util.List;

/**
 * Created by zyuki on 4/25/2016.
 */
public class FragmentChart extends Fragment {
    private List<Entry> entryList;
    private List<String> labelList;

    private TextView mpgAvg;
    private LineChart chart;

    private Callback callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        callback = (Callback)context;

        entryList = callback.getEntryList();
        labelList = callback.getLabelList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutRes = R.layout.fragment_chart;
        View customView = inflater.inflate(layoutRes, container, false);

        mpgAvg = (TextView)customView.findViewById(R.id.chartFrag_text_mpgAvg);
        chart = (LineChart)customView.findViewById(R.id.chartFrag_chart);

        updateAvg(callback.formattedAvg());

        LineDataSet dataset = new LineDataSet(entryList, "test");
        LineData data = new LineData(labelList, dataset);

        chart.setData(data);

        chart.getLegend().setEnabled(false);

        return customView;
    }

    public void updateAvg(String formattedAvg) {mpgAvg.setText(formattedAvg);}

    public void notifyChart() {
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    public interface Callback {
        List<Entry> getEntryList();
        List<String> getLabelList();
        String formattedAvg();
    }
}
