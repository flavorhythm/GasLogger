package adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.zenoyuki.flavorhythm.gaslogger.ApplicationDatabase;
import com.zenoyuki.flavorhythm.gaslogger.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import data.DataAccessObject;
import fragment.DialogRouter;
import model.FuelLog;
import model.MilesPerGal;
import util.MpgCalculator;

/**
 * Created by ZYuki on 1/4/2016.
 */
public class ListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<FuelLog> logList;

    private Callback callback;

    public ListAdapter(Activity activity) {
        this.activity = activity;
        inflater = LayoutInflater.from(activity);
        logList = new ArrayList<>();

        callback = (Callback)activity;
    }

    @Override
    public int getCount() {
        return logList.size();
    }

    @Override
    public FuelLog getItem(int position) {
        return logList.get(position);
    }

    @Override
    public long getItemId(int position) {return position;}

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        int layoutRes = R.layout.row_item_list;
        ViewHolder viewHolder;

        if(row == null || row.getTag() == null) {
            row = inflater.inflate(layoutRes, parent, false);
            viewHolder = new ViewHolder();

//            viewHolder.idHolder = (TextView)row.findViewById(R.id.TV_row_timestamp);
            viewHolder.odomHolder = (TextView)row.findViewById(R.id.row_text_odometer);
            viewHolder.gasHolder = (TextView)row.findViewById(R.id.row_text_gas);
            viewHolder.timestampHolder = (TextView)row.findViewById(R.id.row_text_timestamp);
            viewHolder.partialIcon = (ImageView)row.findViewById(R.id.row_image_partialFill);

            viewHolder.editBtn = (ImageButton)row.findViewById(R.id.row_button_edit);
//            viewHolder.delBtn = (ImageButton)row.findViewById(R.id.IB_row_delete);

            row.setTag(viewHolder);
        } else {viewHolder = (ViewHolder)row.getTag();}

        FuelLog entry = getItem(position);

        viewHolder.odomHolder.setText(String.valueOf(entry.getCurrentOdomVal()));
        viewHolder.gasHolder.setText(doubleFormatter(entry.getFuelTopupAmount()));
        viewHolder.timestampHolder.setText(dateFormatter(entry.getRecordDate()));

        if(entry.getPartialFill()) {viewHolder.partialIcon.setVisibility(View.VISIBLE);}
        else {viewHolder.partialIcon.setVisibility(View.INVISIBLE);}

//        final int listPos = position;
        final int entryId = entry.getItemID();
        viewHolder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DialogRouter.showDeleteDialog(activity, listPos, entryId);
                DialogRouter.showEntryDialog(activity, entryId);
            }
        });

        return row;
    }

    public void addLog(FuelLog currentLog) {
        final int first = 0;
        final int second = 1;
        logList.add(first, currentLog);

        if(logList.size() != 1) {
            callback.addMpg(MpgCalculator.calculateMpg(currentLog, logList.get(second)));
        }
    }

//    public void remove(int listPos) {
//        int listSize = logList.size();
//        logList.remove(listPos);
//
//        callback.removeMpg(listPos, listSize);
//    }

    public void clear() {logList.clear();}

    /**Updates the items displayed on the entryList**/
    public int refreshData() {
        // Pulls all entries from the DB into an ArrayList
        // Param1: selection. Null for all entries
        // Param2: selection arguments. Null for all entries
        DataAccessObject dataAccess = ((ApplicationDatabase)activity.getApplication()).dataAccess;
        List<FuelLog> logsFromDB = dataAccess.getAllEntries(null, null);

        // For ever entry in the DB...
        for (int i = 0; i < logsFromDB.size(); i++) {

            // Instantiates a new temp FuelLog object to pass data from the DB arraylist to the listAdapter arraylist
            FuelLog entry = new FuelLog();

            // Copies odom value from DB to the list
            entry.setCurrentOdomVal(logsFromDB.get(i).getCurrentOdomVal());
            // Copies fillup amount from DB to the list
            entry.setFuelTopupAmount(logsFromDB.get(i).getFuelTopupAmount());
            // Copies record date from DB to the list
            entry.setRecordDate(logsFromDB.get(i).getRecordDate());
            // Copies partial fill data from DB to the list
            entry.setPartialFill(logsFromDB.get(i).getPartialFill());
            // Copies id from DB to the list
            entry.setItemID(logsFromDB.get(i).getItemID());

            // Adds the temp FuelLog to the adapter-attached arraylist
            logList.add(entry);
        }

        // Returns the number of entries within the DB
        return logsFromDB.size();
    }

    private String doubleFormatter(double topupAmount) {
        DecimalFormat df = new DecimalFormat("##.00"); //Formats MPG value

        return df.format(topupAmount);
    }

    private String dateFormatter(long recordDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        return simpleDateFormat.format(recordDate);
    }

    public interface Callback {
        void addMpg(MilesPerGal mpg);
//        void removeMpg(int listPos, int listSize);
    }

    private class ViewHolder {
//        TextView idHolder;
        TextView odomHolder;
        TextView gasHolder;
        TextView timestampHolder;
        ImageButton editBtn;
//        ImageButton delBtn;

        ImageView partialIcon;
    }
}
