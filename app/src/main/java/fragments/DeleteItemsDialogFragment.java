package fragments;

import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
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
import util.Constants;

/**
 * Created by zyuki on 1/12/2016.
 */
public class DeleteItemsDialogFragment extends DialogFragment implements View.OnClickListener {
    private static final String ENTRY_ID_KEY = "item_id_key";

    private View customLayout;
    private TextView alertTitle;
    private Button delete;

    private static Long pressDownEventTime = 0l;
    private static Long releaseEventTime = 0l;

    DataAccessObject dataAO;

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

        final Timer timer = new Timer();
        delete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                dataAO.deleteAllEntries();
                            }
                        }, 3000l);
                        return true;
                    case MotionEvent.ACTION_UP:
                        timer.cancel();
                        timer.purge();

                        return true;
                }
                return false;
            }
        });

        return customLayout;
    }

    private View delOneDialog() {
        String title = "Fuel log at: " + getArguments().getInt(ENTRY_ID_KEY) + " miles";
        alertTitle.setText(title);
        delete.setOnClickListener(this);

        return customLayout;
    }
}
