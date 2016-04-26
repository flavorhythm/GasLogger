package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.zenoyuki.flavorhythm.gaslogger.R;

import java.util.List;

import adapter.ListAdapter;
import model.FuelLog;
import model.MilesPerGal;
import util.MpgCalculator;

/**
 * Created by zyuki on 4/25/2016.
 */
public class FragmentList extends Fragment  {
    private ListAdapter listAdapter;
    private List<MilesPerGal> mpgList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutRes = R.layout.fragment_list;
        View customView = inflater.inflate(layoutRes, container, false);
        listAdapter = new ListAdapter(getActivity());
        mpgList = MpgCalculator.findMpgList(getActivity().getApplication());

        ListView list = (ListView)customView.findViewById(R.id.listFrag_listView);
        list.setAdapter(listAdapter);
        list.setEmptyView(customView.findViewById(R.id.listFrag_text_emptyList));

        listAdapter.refreshData();
        listAdapter.notifyDataSetChanged();

        return customView;
    }

    public void addLog(FuelLog fuelLog) {
        listAdapter.addLog(fuelLog);
        listAdapter.notifyDataSetChanged();
    }

    public void addMpg(MilesPerGal mpg) {
        mpgList.add(mpg);
//        Log.v("test", mpgListPrint());
    }

//    public void removeLog(int listPos) {
//        listAdapter.remove(listPos);
//        listAdapter.notifyDataSetChanged();
//    }
//
//    public void removeMpg(int listPos) {
//        mpgList.remove(listPos);
//        Log.v("test", mpgListPrint());
//    }

    public void clear() {
        listAdapter.clear();
        mpgList.clear();
        listAdapter.notifyDataSetChanged();
    }

    public int getCount() {return listAdapter.getCount();}



    /****/
    /****/
    private String mpgListPrint() {
        String mpgString = "";
        for(MilesPerGal mpg : mpgList) {
            mpgString += mpg.getMpg() + " ";
        }

        return mpgString;
    }
    /****/
    /****/
}
