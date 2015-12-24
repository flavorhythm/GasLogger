package com.zenoyuki.flavorhythm.gaslogger;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zyuki on 12/23/2015.
 */
public class CustomDialogFragment extends DialogFragment {
    public static CustomDialogFragment newInstance() {
        CustomDialogFragment fragment = new CustomDialogFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.new_entry_dialog, container, false);
    }
}
