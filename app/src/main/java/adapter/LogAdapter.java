package adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.zenoyuki.flavorhythm.gaslogger.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import fragment.DialogRouter;
import model.FuelLog;
import util.Constant;

/**
 * Created by zyuki on 4/27/2016.
 */
public class LogAdapter extends ArrayAdapter<FuelLog> {
    private int layoutRes;
    private Activity activity;
    private List<FuelLog> logList;

    public LogAdapter(Activity activity, int layoutRes, List<FuelLog> logList) {
        super(activity, layoutRes, logList);

        this.activity = activity;
        this.layoutRes = layoutRes;
        this.logList = logList;
    }

    @Override
    public int getCount() {return logList.size();}

    @Override
    public FuelLog getItem(int position) {return super.getItem(position);}

    @Override
    public int getPosition(FuelLog item) {return super.getPosition(item);}

    @Override
    public long getItemId(int position) {return logList.get(position).getItemID();}

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        ViewHolder viewHolder;

        if(row == null || row.getTag() == null) {
            row = LayoutInflater.from(activity).inflate(layoutRes, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.odomHolder = (TextView)row.findViewById(R.id.row_text_odometer);
            viewHolder.gasHolder = (TextView)row.findViewById(R.id.row_text_gas);
            viewHolder.timestampHolder = (TextView)row.findViewById(R.id.row_text_timestamp);
            viewHolder.partialIcon = (ImageView)row.findViewById(R.id.row_image_partialFill);

            viewHolder.editBtn = (ImageButton)row.findViewById(R.id.row_button_edit);

            row.setTag(viewHolder);
        } else {viewHolder = (ViewHolder)row.getTag();}

        FuelLog entry = getItem(position);

        viewHolder.odomHolder.setText(String.valueOf(entry.getCurrentOdomVal()));
        viewHolder.gasHolder.setText(doubleFormatter(entry.getFuelTopupAmount()));
        viewHolder.timestampHolder.setText(dateFormatter(entry.getRecordDate()));

        if(entry.getPartialFill()) {viewHolder.partialIcon.setVisibility(View.VISIBLE);}
        else {viewHolder.partialIcon.setVisibility(View.INVISIBLE);}

        final int entryId = entry.getItemID();
        viewHolder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogRouter.showEntryDialog(activity, entryId);
            }
        });

        return row;
    }

    private String doubleFormatter(double topupAmount) {
        DecimalFormat df = new DecimalFormat(Constant.FLOAT_FORMAT); //Formats MPG value

        return df.format(topupAmount);
    }

    private String dateFormatter(long recordDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constant.DATE_FORMAT, Locale.US);

        return simpleDateFormat.format(recordDate);
    }

    private class ViewHolder {
        TextView odomHolder;
        TextView gasHolder;
        TextView timestampHolder;
        ImageButton editBtn;
        ImageView partialIcon;
    }
}
