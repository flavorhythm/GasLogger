package data;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.zenoyuki.flavorhythm.gaslogger.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import model.FuelLog;
import fragments.DialogFragmentRouter;

/**
 * Created by ZYuki on 1/4/2016.
 */
public class DataAdapter extends ArrayAdapter<FuelLog> {
    private Activity activity;
    private int layoutResource;
    private ArrayList<FuelLog> fuelLogArrayList;

    public DataAdapter(Activity activity, int layoutResource, ArrayList<FuelLog> fuelLogArrayList) {
        super(activity, layoutResource, fuelLogArrayList);

        this.activity = activity;
        this.layoutResource = layoutResource;
        this.fuelLogArrayList = fuelLogArrayList;

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return fuelLogArrayList.size();
    }

    @Override
    public FuelLog getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(FuelLog item) {
        return super.getPosition(item);
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        ViewHolder viewHolder;

        if(row == null || row.getTag() == null) {
            row = LayoutInflater.from(activity).inflate(layoutResource, null);
            viewHolder = new ViewHolder();

//            viewHolder.idHolder = (TextView)row.findViewById(R.id.TV_row_timestamp);
            viewHolder.odomHolder = (TextView)row.findViewById(R.id.TV_row_odom);
            viewHolder.gasHolder = (TextView)row.findViewById(R.id.TV_row_gas);
            viewHolder.timestampHolder = (TextView)row.findViewById(R.id.TV_row_timestamp);
            viewHolder.partialIcon = (ImageView)row.findViewById(R.id.IV_partial_fill_icon);

//            viewHolder.editBtn = (ImageButton)row.findViewById(R.id.IB_row_edit);
            viewHolder.delBtn = (ImageButton)row.findViewById(R.id.IB_row_delete);

            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)row.getTag();
        }

        FuelLog entry = getItem(position);

        viewHolder.odomHolder.setText(String.valueOf(entry.getCurrentOdomVal()));
//		viewHolder.gasHolder.setText(String.valueOf(entry.getPartialFill()));
        viewHolder.gasHolder.setText(String.valueOf(entry.getFuelTopupAmount()));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        Date date = new Date(entry.getRecordDate());
        viewHolder.timestampHolder.setText(simpleDateFormat.format(date));

        if(entry.getPartialFill()) {viewHolder.partialIcon.setVisibility(View.VISIBLE);}
        else {viewHolder.partialIcon.setVisibility(View.INVISIBLE);}

        final int finalEntryID = entry.getItemID();
        viewHolder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragmentRouter.instantiateDeleteItemsDF(activity, finalEntryID);
            }
        });
//        viewHolder.editBtn.setOnClickListener(new View.OnClickListener() {});

        return row;
    }

    private class ViewHolder {
//        TextView idHolder;
        TextView odomHolder;
        TextView gasHolder;
        TextView timestampHolder;
//        ImageButton editBtn;
        ImageButton delBtn;

        ImageView partialIcon;
    }
}
