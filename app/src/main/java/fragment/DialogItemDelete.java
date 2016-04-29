package fragment;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.TextView;

import com.zenoyuki.flavorhythm.gaslogger.ApplicationDatabase;
import com.zenoyuki.flavorhythm.gaslogger.R;

import java.util.Timer;
import java.util.TimerTask;

import data.DataAccessObject;
import data.FillupTable;
import util.Constant;

/**
 * Created by zyuki on 1/12/2016.
 */
public class DialogItemDelete extends DialogFragment {
    private View customLayout;
    private TextView counter;
    private Button delete;

    DataAccessObject dataAccess;

    private Callback callback;

    private boolean timeReached = false;

    public static DialogItemDelete newInstance() {return new DialogItemDelete();}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        callback = (Callback)activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        customLayout = inflater.inflate(R.layout.dialog_delete_item, container, false);

        counter = (TextView)customLayout.findViewById(R.id.del_txt_counter);

        delete = (Button)customLayout.findViewById(R.id.del_btn_delete);

        Button dismiss = (Button)customLayout.findViewById(R.id.del_btn_dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        dataAccess = ((ApplicationDatabase)getActivity().getApplication()).dataAccess;

        //Removes title space from dialog
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Creates a dialog with the minimum width of the screen (minus padding)
        int width = getResources().getDisplayMetrics().widthPixels - 350;
        customLayout.setMinimumWidth(width);

        return delAllDialog();
    }

    private View delAllDialog() {
        if(callback.listSize() == Constant.EMPTY_INT) {
            delete.setEnabled(false);
            delete.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color));
            delete.setAlpha(0.6f);
        } else {
            delete.setEnabled(true);
            delete.setTextColor(ContextCompat.getColor(getContext(), R.color.deleteTextBtn));
            delete.setAlpha(1.0f);

            delete.setOnTouchListener(new AsyncListener());
        }
        //TODO: animate the "Delete" button like the normal onClick button (for consistency)
        delete.setText(getResources().getString(R.string.del_btn_all));

        return customLayout;
    }

    private void showDeleteSnackbar(int delCount) {
        View root = getActivity().findViewById(R.id.main_root);

        if(delCount != Constant.EMPTY_INT) {
            String success = getResources().getString(R.string.del_snack_success_1_2) + " (" + delCount;
            success += (delCount == 1) ?
                    " " + getResources().getString(R.string.del_snack_entry_2_2) + ")" :
                    " " + getResources().getString(R.string.del_snack_entries_2_2) + ")";

            Snackbar.make(root, success, Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(root, getResources().getString(R.string.del_snack_error), Snackbar.LENGTH_SHORT).show();
        }
    }

    /***********************************************************************************************
     * INNER CLASSES & INTERFACES
     **********************************************************************************************/
    /****/
    public interface Callback {
        int listSize();
        int clearList();
    }

    /****/
    private class AsyncListener implements View.OnTouchListener {
        private AsyncPressTimer listener;
        private CountDownTimer countDown;

        private long pressLen = 3000L;
        private long interval = 1000L;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    listener = new AsyncPressTimer();
                    listener.execute(pressLen);

                    counter.setVisibility(View.VISIBLE);
                    countDown = setupTimer(pressLen, interval).start();

                    return true;
                case MotionEvent.ACTION_UP:
                    if(timeReached) {
                        int delCount = callback.clearList();

                        showDeleteSnackbar(delCount);
                        getDialog().dismiss();

                        timerCancel();
                    } else {timerCancel();}

                    return true;
            }
            return false;
        }

        private void timerCancel() {
            if(listener != null) {
                counter.setVisibility(View.INVISIBLE);
                counter.setText(Constant.TIMER_MAX);
                countDown.cancel();

                listener.pressTimer.cancel();
                listener.pressTimer.purge();
            }
        }

        private CountDownTimer setupTimer(long millisInFuture, long countDownInterval) {
            final long buffer = 50L;

            return new CountDownTimer(millisInFuture + buffer, countDownInterval) {
                private int ticker = Long.valueOf(pressLen / 1000).intValue();

                @Override
                public void onTick(long millisUntilFinished) {
                    counter.setText(String.valueOf(ticker));
                    ticker--;
                }

                @Override
                public void onFinish() {counter.setText(getResources().getString(R.string.ok_text));}
            };
        }
    }

    /****/
    private class AsyncPressTimer extends AsyncTask<Long, Void, Boolean> {
        private Timer pressTimer = new Timer();

        private AsyncPressTimer() {}

        @Override
        protected Boolean doInBackground(Long... params) {
            long pressLen = params[0];

            pressTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    timeReached = true;
                }
            }, pressLen);

            return timeReached;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {super.onPostExecute(aBoolean);}
    }
}
