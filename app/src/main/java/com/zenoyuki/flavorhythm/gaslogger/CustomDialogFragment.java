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

/**
 * Created by zyuki on 12/23/2015.
 */
public class CustomDialogFragment extends DialogFragment {
    public static CustomDialogFragment newInstance() {
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
        EditText odomVal, gasVal;
        Button submit,dismiss;

        odomVal = (EditText)customLayout.findViewById(R.id.alrt_edt_odom);
        gasVal = (EditText)customLayout.findViewById(R.id.alrt_edt_gas);

        submit = (Button)customLayout.findViewById(R.id.alrt_btn_submit);
        dismiss = (Button)customLayout.findViewById(R.id.alrt_btn_dismiss);

        return customLayout;
    }


}
