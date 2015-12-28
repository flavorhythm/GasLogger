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
 */
public class CustomDialogFragment extends DialogFragment implements View.OnClickListener {
    public static CustomDialogFragment newInstance(int minMileage) {
        CustomDialogFragment fragment = new CustomDialogFragment();
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

    }

    public void alertDismissBtnClick() {

    }
}
