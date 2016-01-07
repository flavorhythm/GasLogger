package data;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zenoyuki.flavorhythm.gaslogger.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import model.FuelLog;

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

//            viewHolder.editBtn = (ImageButton)row.findViewById(R.id.IB_row_edit);
            viewHolder.delBtn = (ImageButton)row.findViewById(R.id.IB_row_delete);

            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)row.getTag();
        }

        FuelLog entry = getItem(position);

        viewHolder.odomHolder.setText(String.valueOf(entry.getCurrentOdomVal()));
        viewHolder.gasHolder.setText(String.valueOf(entry.getFuelTopupAmount()));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        Date date = new Date(entry.getRecordDate());
        viewHolder.timestampHolder.setText(simpleDateFormat.format(date));

        final int finalEntryID = entry.getItemID();
        final int finalOdomVal = entry.getCurrentOdomVal();
        viewHolder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAlert(String.valueOf(finalOdomVal), finalEntryID);
            }
        });
//        viewHolder.editBtn.setOnClickListener(new View.OnClickListener() {});

        return row;
    }

    private void deleteAlert(String odomVal, final int itemID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Topup at: " + odomVal + " miles");
        builder.setMessage("Delete?");

        builder.setPositiveButton("Destroy!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseHandler db = new DatabaseHandler(getContext());
                db.getWritableDatabase();

                db.deleteEntry(itemID);
                notifyDataSetChanged();
                db.close();
            }
        });
        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private class ViewHolder {
//        TextView idHolder;
        TextView odomHolder;
        TextView gasHolder;
        TextView timestampHolder;
//        ImageButton editBtn;
        ImageButton delBtn;
    }
}
