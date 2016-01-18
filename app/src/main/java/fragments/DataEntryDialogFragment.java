package fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.zenoyuki.flavorhythm.gaslogger.ApplicationDatabase;
import com.zenoyuki.flavorhythm.gaslogger.R;

import util.Constants;
import data.DataAccessObject;
import model.FuelLog;

/**
 * Created by zyuki on 12/23/2015.
 * Reason why you cannot do the following:
 * public class DataEntryDialogFragment extends DialogFragment implements View.OnClickListener {
 *     private static final String MIN_MILEAGE_KEY = "min_mileage";
 *     private String myVar;
 * public static DataEntryDialogFragment newInstance(int minMileage, String myVar) {
 *     this.myVar = myVar;
 * This is because the instantiation (I believe it's an instance to save memory, instead of creating an object via a constructor)
 * needs to be declared static (can be declared just public. Maybe this would create a crash? must test) and static methods cannot pull from
 * "this."
 *
 * Example:
 * public static DataEntryDialogFragment newInstance() {
 *       DataEntryDialogFragment fragment = new DataEntryDialogFragment();
 *        Bundle args = new Bundle();
 *
 *        args.putInt(MIN_MILEAGE_KEY, minMileage);
 *        fragment.setArguments(args);
 *        return new DataEntryDialogFragment();
 * }
 */
public class DataEntryDialogFragment extends DialogFragment implements View.OnClickListener {
	private View customLayout;
	private EditText odomVal, gasVal;
	private CheckBox partialFillCheck;
	private Button submit, dismiss;
    private TextInputLayout odomWrapper, gasWrapper;

    public static DataEntryDialogFragment newInstance() {
        return new DataEntryDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.v("LifeCycle", "onCreateView Callback S");

        customLayout = inflater.inflate(R.layout.new_entry_dialog, container, false);
        customLayout.requestFocus();

		findViewsByID();

        odomWrapper.setHint(getResources().getString(R.string.odom_hint));
        gasWrapper.setHint(getResources().getString(R.string.gas_hint));

        submit.setOnClickListener(this);
        dismiss.setOnClickListener(this);

        odomVal.addTextChangedListener(new CustomTextWatcher(odomVal.getId()));
        gasVal.addTextChangedListener(new CustomTextWatcher(gasVal.getId()));

        //Removes title space from dialog
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

		//Creates a dialog with the minimum width of the screen (minus padding)
		int width = getResources().getDisplayMetrics().widthPixels - 350;
		customLayout.setMinimumWidth(width);

        Log.v("LifeCycle", "onCreateView Callback e");
        return customLayout;
    }

    @Override
    public void onPause() {
        submit.setOnClickListener(null);
        dismiss.setOnClickListener(null);

        odomVal.addTextChangedListener(null);
        gasVal.addTextChangedListener(null);

        getActivity().getSupportFragmentManager().popBackStack();

        super.onPause();
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
                    if(gasEmpty) {errorDisplay(gasWrapper, getResources().getString(R.string.blank_error));}
                    if(odomEmpty) {errorDisplay(odomWrapper, getResources().getString(R.string.blank_error));}
                    break;
                }

                int odomNewVal = Integer.parseInt(odomVal.getText().toString());

                SharedPreferences preferences = getContext().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
                int minOdom = preferences.getInt(Constants.MIN_MILEAGE_KEY, 0);

                if(minOdom >= odomNewVal) {
                    errorDisplay(odomWrapper, getResources().getString(R.string.odom_low_value_error) + " " + String.valueOf(minOdom));
                    break;
                } else {
                    FuelLog fuelLog = new FuelLog();

                    fuelLog.setCurrentOdomVal(Integer.parseInt(odomVal.getText().toString()));
                    fuelLog.setFuelTopupAmount(Float.parseFloat(gasVal.getText().toString()));
					fuelLog.setPartialFill(partialFillCheck.isChecked());

                    DataAccessObject dataAO = ((ApplicationDatabase)getActivity().getApplication()).mDataAO;
                    long entryID = dataAO.addEntry(fuelLog);

                    showAddSnackbar(entryID);
                }
            case R.id.alrt_btn_dismiss:
                getDialog().dismiss();
				break;
		}
    }

    private void showAddSnackbar(long entryID) {
        final long ERROR = -1l;
        View root = getActivity().findViewById(R.id.main_root);

        if(entryID != ERROR) {
            Snackbar.make(root, getResources().getString(R.string.save_snack_success), Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(root, getResources().getString(R.string.save_snack_error), Snackbar.LENGTH_SHORT).show();
        }
    }

    private void errorDisplay(TextInputLayout wrapper, String errorString) {
        wrapper.requestFocus();
        wrapper.setErrorEnabled(true);
        wrapper.setError(errorString);
    }

    private void findViewsByID() {
		odomVal = (EditText)customLayout.findViewById(R.id.alrt_edt_odom);
		gasVal = (EditText)customLayout.findViewById(R.id.alrt_edt_gas);

		partialFillCheck = (CheckBox)customLayout.findViewById(R.id.alrt_chk_partialFill);

		odomWrapper = (TextInputLayout)customLayout.findViewById(R.id.alrt_TIL_odomWrapper);
		gasWrapper = (TextInputLayout)customLayout.findViewById(R.id.alrt_TIL_gasWrapper);

		submit = (Button)customLayout.findViewById(R.id.alrt_btn_submit);
		dismiss = (Button)customLayout.findViewById(R.id.alrt_btn_dismiss);
	}

    private class CustomTextWatcher implements TextWatcher {
        private int viewID;

        private CustomTextWatcher(int viewID) {
            this.viewID = viewID;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            switch(viewID) {
                case R.id.alrt_edt_odom:
                    odomWrapper.setErrorEnabled(false);
                    break;
                case R.id.alrt_edt_gas:
                    gasWrapper.setErrorEnabled(false);
                    break;
            }
        }
    }
}
