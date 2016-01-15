package fragments;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.zenoyuki.flavorhythm.gaslogger.ApplicationDatabase;
import com.zenoyuki.flavorhythm.gaslogger.R;

import java.util.Timer;
import java.util.TimerTask;

import data.DataAccessObject;
import data.FillupTable;
import util.Constants;

/**
 * Created by zyuki on 1/12/2016.
 */
public class DeleteItemsDialogFragment extends DialogFragment implements View.OnClickListener {
    private static final String ENTRY_ID_KEY = "item_id_key";

    private View customLayout;
    private TextView alertTitle;
    private Button delete;

    DataAccessObject dataAO;

    private boolean timeReached;

    public static DeleteItemsDialogFragment newInstance(int entryID) {
        DeleteItemsDialogFragment dialogFragment = new DeleteItemsDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ENTRY_ID_KEY, entryID);

        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        customLayout = inflater.inflate(R.layout.delete_dialog, container, false);

        alertTitle = (TextView)customLayout.findViewById(R.id.del_txt_delTitle);
        delete = (Button)customLayout.findViewById(R.id.del_btn_delete);

        Button dismiss = (Button)customLayout.findViewById(R.id.del_btn_dismiss);
        dismiss.setOnClickListener(this);

        dataAO = ((ApplicationDatabase)getActivity().getApplication()).mDataAO;

        //Removes title space from dialog
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Creates a dialog with the minimum width of the screen (minus padding)
        int width = getResources().getDisplayMetrics().widthPixels - 350;
        customLayout.setMinimumWidth(width);

        if(getArguments().getInt(ENTRY_ID_KEY) <= Constants.ALL_ID) {
            return delAllDialog();
        } else {
            return delOneDialog();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.del_btn_delete:
                dataAO.deleteEntry(getArguments().getInt(ENTRY_ID_KEY));
                break;
            case R.id.del_btn_dismiss:
                getDialog().dismiss();
                break;
        }
    }

    private View delAllDialog() {
        if(getArguments().getInt(ENTRY_ID_KEY) == Constants.NO_ENTRIES) {
            delete.setEnabled(false);
            delete.setAlpha(0.6f);
        } else {
            delete.setEnabled(true);
            delete.setAlpha(1.0f);
        }

        delete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        PressTimer pressTimer = new PressTimer();
                        pressTimer.execute();
                        Log.d("event", "pressedDown & " + String.valueOf(timeReached));
                        return false;
                    case MotionEvent.ACTION_UP:
                        if(timeReached) {
                            dataAO.deleteAllEntries();
                            return true;
                        }
                        Log.d("event", "pressedUp & " + String.valueOf(timeReached));
                        return false;
                }
                return false;
            }
        });

        return customLayout;
    }

    private View delOneDialog() {
        int id = getArguments().getInt(ENTRY_ID_KEY);
        int odomVal = dataAO.getEntry(FillupTable.KEY_ID + " = " + id, null).getCurrentOdomVal();
        String title = "Fuel log at: " + odomVal + " miles";

        alertTitle.setText(title);
        delete.setOnClickListener(this);

        return customLayout;
    }

    private class PressTimer extends AsyncTask<Void, Void, Boolean> implements View.OnTouchListener {
        private Timer timer;
        private boolean timeReachedAsync;

        @Override
        protected Boolean doInBackground(Void... params) {
            long pressLen = 3000l;
            timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    timeReachedAsync = true;
                }
            }, pressLen);

            timer.cancel();
            timer.purge();
            return timeReachedAsync;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            timeReached = timeReachedAsync;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }
    }
}
