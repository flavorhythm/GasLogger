package fragments;

import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.zenoyuki.flavorhythm.gaslogger.ApplicationDatabase;
import com.zenoyuki.flavorhythm.gaslogger.HistoryActivity;
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
    private static final String RESET_COUNTER = "3";

    private View customLayout;
    private TextView alertTitle, counter;
    private Button delete;

    DataAccessObject dataAO;

    private boolean timeReached = false;

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
        counter = (TextView)customLayout.findViewById(R.id.del_txt_counter);

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
                int count = dataAO.deleteEntry(getArguments().getInt(ENTRY_ID_KEY));
                showDeleteSnackbar(count);
            case R.id.del_btn_dismiss:
                getDialog().dismiss();
                break;
        }
    }

    private View delAllDialog() {
        if(getArguments().getInt(ENTRY_ID_KEY) == Constants.NO_ENTRIES) {
            delete.setEnabled(false);
            delete.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color));
            delete.setAlpha(0.6f);
        } else {
            delete.setEnabled(true);
            delete.setTextColor(ContextCompat.getColor(getContext(), R.color.deleteTextBtn));
            delete.setAlpha(1.0f);
        }

        delete.setOnTouchListener(new AsyncListener());
        //TODO animate the "Delete" button like the normal onClick button (for consistency)
        delete.setText(getResources().getString(R.string.del_btn_all));

        return customLayout;
    }

    private View delOneDialog() {
        int id = getArguments().getInt(ENTRY_ID_KEY);
        int odomVal = dataAO.getEntry(FillupTable.KEY_ID + " = " + id, null).getCurrentOdomVal();
        String title = getResources().getString(R.string.del_one_title_1_2) +
                " " + odomVal + " " +
                getResources().getString(R.string.del_one_title_2_2);

        alertTitle.setText(title);
        delete.setOnClickListener(this);

        return customLayout;
    }

    private void showDeleteSnackbar(int delCount) {
        final long ERROR = 0l;
        View root = getActivity().findViewById(R.id.history_root);

        if(delCount != ERROR) {
            String success = getResources().getString(R.string.del_snack_success_1_2) + " (" + delCount;
            success += (delCount == 1) ?
                    " " + getResources().getString(R.string.del_snack_entry_2_2) + ")" :
                    " " + getResources().getString(R.string.del_snack_entries_2_2) + ")";

            Snackbar.make(root, success, Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(root, getResources().getString(R.string.del_snack_error), Snackbar.LENGTH_SHORT).show();
        }
    }

    private class AsyncListener implements View.OnTouchListener {
        private AsyncPressTimer listener;
        private CountDownTimer countDown;

        private long pressLen = 3000l;
        private long interval = 1000l;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d("event", "pressedDown & " + String.valueOf(timeReached));
                    listener = new AsyncPressTimer();
                    listener.execute(pressLen);

                    counter.setVisibility(View.VISIBLE);
                    countDown = setupTimer(pressLen, interval).start();

                    return true;
                case MotionEvent.ACTION_UP:
                    if(timeReached) {
                        int count = dataAO.deleteAllEntries();
                        showDeleteSnackbar(count);
                        getDialog().dismiss();
                        Log.d("event", "pressedUp & " + String.valueOf(timeReached));

                        timerCancel();
                    } else {
                        Log.d("event", "pressedUp & " + String.valueOf(timeReached));

                        timerCancel();
                    }
                    return true;
            }
            return false;
        }

        private void timerCancel() {
            if(listener != null) {
                counter.setVisibility(View.INVISIBLE);
                counter.setText(RESET_COUNTER);
                countDown.cancel();

                listener.pressTimer.cancel();
                listener.pressTimer.purge();
            }
        }

        private CountDownTimer setupTimer(long millisInFuture, long countDownInterval) {
            final long buffer = 50l;

            return new CountDownTimer(millisInFuture + buffer, countDownInterval) {
                private int ticker = (int)(long)pressLen / 1000;

                @Override
                public void onTick(long millisUntilFinished) {
                    counter.setText(String.valueOf(ticker));
                    ticker--;
                }

                @Override
                public void onFinish() {
                    counter.setText("OK");
                }
            };
        }
    }

    private class AsyncPressTimer extends AsyncTask<Long, Void, Boolean> {
        private Timer pressTimer = new Timer();

        private AsyncPressTimer() {
            Log.d("event", "instantiated");
        }

        @Override
        protected Boolean doInBackground(Long... params) {
            long pressLen = params[0];

            pressTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    timeReached = true;
                    Log.d("event", "reached " + String.valueOf(timeReached));
                }
            }, pressLen);

            return timeReached;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }
}
