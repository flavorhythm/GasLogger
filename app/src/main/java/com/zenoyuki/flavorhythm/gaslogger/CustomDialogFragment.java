package com.zenoyuki.flavorhythm.gaslogger;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

    public static CustomDialogFragment newInstance(int minMileage, String myVar) {
        CustomDialogFragment fragment = new CustomDialogFragment();
        Bundle args = new Bundle();

        args.putInt(MIN_MILEAGE_KEY, minMileage);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View customLayout = inflater.inflate(R.layout.new_entry_dialog, container, false);

        getDialog().setTitle(R.string.alrt_title);

        EditText odomVal, gasVal;
        Button submit,dismiss;

        odomVal = (EditText)customLayout.findViewById(R.id.alrt_edt_odom);
        gasVal = (EditText)customLayout.findViewById(R.id.alrt_edt_gas);

        submit = (Button)customLayout.findViewById(R.id.alrt_btn_submit);
        dismiss = (Button)customLayout.findViewById(R.id.alrt_btn_dismiss);

        submit.setOnClickListener(this);
        dismiss.setOnClickListener(this);

        return customLayout;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
			case R.id.alrt_btn_submit:
				alertSubmitBtnClick();
				break;
			case R.id.alrt_btn_dismiss:
				alertDismissBtnClick();
				break;
			default:
				Toast.makeText(getContext(), "Something went wrong with the buttons", Toast.LENGTH_SHORT).show();
				break;
		}
    }

    public void alertSubmitBtnClick() {
        int minMileage = getArguments().getInt(MIN_MILEAGE_KEY);
    }

    public void alertDismissBtnClick() {
        getDialog().dismiss();
    }
}
