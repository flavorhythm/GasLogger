package com.zenoyuki.flavorhythm.gaslogger;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import data.DatabaseHandler;
import model.FuelLog;

/**
 * Created by zyuki on 12/23/2015.
 * Reason why you cannot do the following:
 * public class CustomDialogFragment extends DialogFragment implements View.OnClickListener {
 *     private static final String MIN_MILEAGE_KEY = "min_mileage";
 *     private String myVar;
 * public static CustomDialogFragment newInstance(int minMileage, String myVar) {
 *     this.myVar = myVar;
 * This is because the instantiation (I believe it's an instance to save memory, instead of creating an object via a constructor)
 * needs to be declared static (can be declared just public. Maybe this would create a crash? must test) and static methods cannot pull from
 * "this."
 */
public class CustomDialogFragment extends DialogFragment implements View.OnClickListener {
    private static final String MIN_MILEAGE_KEY = "min_mileage";

    private EditText odomVal, gasVal;

    public static CustomDialogFragment newInstance(int minMileage) {
        CustomDialogFragment fragment = new CustomDialogFragment();
        Bundle args = new Bundle();

        args.putInt(MIN_MILEAGE_KEY, minMileage);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View customLayout = inflater.inflate(R.layout.new_entry_dialog, container, false);

        //Creates a dialog with the minimum width of the screen (minus padding)
        int width = getResources().getDisplayMetrics().widthPixels - 350;
        customLayout.setMinimumWidth(width);

        odomVal = (EditText)customLayout.findViewById(R.id.alrt_edt_odom);
        gasVal = (EditText)customLayout.findViewById(R.id.alrt_edt_gas);

        Button submit = (Button)customLayout.findViewById(R.id.alrt_btn_submit);
        Button dismiss = (Button)customLayout.findViewById(R.id.alrt_btn_dismiss);

        submit.setOnClickListener(this);
        dismiss.setOnClickListener(this);

        //Removes title space from dialog
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return customLayout;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
			case R.id.alrt_btn_submit:
                boolean odomEmpty = TextUtils.isEmpty(odomVal.getText());
                boolean gasEmpty = TextUtils.isEmpty(gasVal.getText());

                //Tests for empty fields. If either fields are empty, further validation goes on here in these nested IF blocks
                if(odomEmpty || gasEmpty) {
                    //Tests for empty gas usage field first
                    if(gasEmpty) {
                        gasVal.requestFocus();
                        gasVal.setError(getResources().getString(R.string.gas_blank_error));
                    }
                    if(odomEmpty) {
                        odomVal.requestFocus();
                        odomVal.setError(getResources().getString(R.string.odom_blank_error));
                    }

                    break;
                }

                int odomNewVal = Integer.parseInt(odomVal.getText().toString());
                int minOdom = getArguments().getInt(MIN_MILEAGE_KEY);

                if(minOdom >= odomNewVal) {
                    odomVal.requestFocus();

                    String minOdomError = getResources().getString(R.string.odom_low_value_error) + " " + String.valueOf(minOdom);
                    odomVal.setError(minOdomError);

                    break;
                } else {
                    DatabaseHandler db = new DatabaseHandler(getContext());
                    FuelLog fuelLog = new FuelLog();
                    db.getWritableDatabase();

                    fuelLog.setCurrentOdomVal(Integer.parseInt(odomVal.getText().toString()));
                    fuelLog.setFuelTopupAmount(Float.parseFloat(gasVal.getText().toString()));

                    db.addEntry(fuelLog);
                    db.close();
                }
			case R.id.alrt_btn_dismiss:
                getDialog().dismiss();

                getActivity().getSupportFragmentManager().popBackStack();
				break;
			default:
				Toast.makeText(getContext(), "Something went wrong with the buttons", Toast.LENGTH_SHORT).show();
				break;
		}
    }
}
