package fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.zenoyuki.flavorhythm.gaslogger.R;

import java.util.List;

import adapter.LogAdapter;
import model.FuelLog;

/**
 * Created by zyuki on 4/25/2016.
 */
public class FragmentList extends Fragment  {
    private LogAdapter logAdapter;
    private List<FuelLog> logList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Callback callback = (Callback)context;
        logList = callback.getLogList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int fragmentLayout = R.layout.fragment_list;
        View customView = inflater.inflate(fragmentLayout, container, false);

        int rowLayout = R.layout.row_item_list;
        logAdapter = new LogAdapter(getActivity(), rowLayout, logList);

        ListView list = (ListView)customView.findViewById(R.id.listFrag_listView);
        list.setAdapter(logAdapter);

        list.setEmptyView(customView.findViewById(R.id.listFrag_text_emptyList));

        return customView;
    }

    public void notifyAdapter() {logAdapter.notifyDataSetChanged();}

    public interface Callback {
        List<FuelLog> getLogList();
    }
}
