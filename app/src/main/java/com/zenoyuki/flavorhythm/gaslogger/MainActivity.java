package com.zenoyuki.flavorhythm.gaslogger;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.QuickRule;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.DecimalMin;
import com.mobsandgeeks.saripaar.annotation.Min;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import data.DatabaseHandler;
import model.FuelLog;

public class MainActivity extends AppCompatActivity {

    private TextView mpgText;

    private EditText odomInput, gasInput;

    private AlertDialog alertDialog;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mpgText = (TextView)findViewById(R.id.TV_display_mpg_value);
        mpgText.setText(updateAverage());

        FloatingActionButton addBtn = (FloatingActionButton)findViewById(R.id.FAB_add);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder();
            }
        });
    }

    private String updateAverage() {
        db = new DatabaseHandler(getApplicationContext());
        db.getReadableDatabase();
        ArrayList<FuelLog> fuelLogArrayList = db.getAllEntries();

        if(fuelLogArrayList.size() > 1) {
            int mileage = fuelLogArrayList.get(0).getCurrentOdomVal() - fuelLogArrayList.get(fuelLogArrayList.size() - 1).getCurrentOdomVal();

            float gasUse = 0;
            for(FuelLog fuelLog : fuelLogArrayList) {
                gasUse += fuelLog.getFuelTopupAmount();
            }

            float mpg = mileage / gasUse;
            DecimalFormat df = new DecimalFormat("###.0");

            db.close();
            return df.format(mpg);
        } else {
            return "0";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.MI_history:
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void dialogBuilder() {
        LayoutInflater layoutInflater = getLayoutInflater();
        View customLayout = layoutInflater.inflate(R.layout.new_entry_dialog, null);

        odomInput = (EditText) customLayout.findViewById(R.id.ET_dialog_odom);
        gasInput = (EditText) customLayout.findViewById(R.id.ET_dialog_gas);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(customLayout);
        builder.setTitle("New Entry");

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FuelLog fuelLog = new FuelLog();

                int odomVal = Integer.parseInt(odomInput.getText().toString());
                float gasVal = Float.parseFloat(gasInput.getText().toString());

                fuelLog.setCurrentOdomVal(odomVal);
                fuelLog.setFuelTopupAmount(gasVal);

                DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                db.addEntry(fuelLog);
                db.close();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog = builder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        mpgText.setText(updateAverage());
    }

    //
//    @Override
//    public void onValidationSucceeded() {
//        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
//        odomInput.setError(null);
//        gasInput.setError(null);
//        Log.d("validation", "success");
//    }
//
//    @Override
//    public void onValidationFailed(List<ValidationError> errors) {
//        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
//
//        for(ValidationError error : errors) {
//            switch(error.getView().getId()) {
//                case R.id.ET_dialog_odom:
//                    odomInput.setError("Whole numbers only");
//                    break;
//                case R.id.ET_dialog_gas:
//                    gasInput.setError("Whole/Decimal numbers only");
//                    break;
//                default: break;
//            }
//        }
//    }
//
//    @Override
//    public void onFocusChange(View v, boolean hasFocus) {
//        if(hasFocus) {
//            ((EditText)v).addTextChangedListener(new TextWatcher() {
//                @Override
//                public void afterTextChanged(Editable s) {
//                    validator.put(odomInput, minOdomValRule);
//                    validator.validate();
//                }
//
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {}
//            });
//        } else {
//            validator.validate();
//        }
//    }

    //    private void findOdomMinInput() {
//        minOdomValRule = new QuickRule<EditText>(2) {
//            @Override
//            public boolean isValid(EditText editText) {
//                switch(editText.getId()) {
//                    case R.id.ET_dialog_odom:
//                        db = new DatabaseHandler(getApplicationContext());
//                        ArrayList<FuelLog> fuelLogArrayList = db.getAllEntries();
//                        db.close();
//                        try {
//                            FuelLog fuelLog = fuelLogArrayList.get(fuelLogArrayList.size() - 1);
//                            return Integer.parseInt(editText.getText().toString()) >= fuelLog.getCurrentOdomVal();
//                        } catch(NumberFormatException|NullPointerException e) {
//                            odomInput.setError("Cannot be empty");
//                        }
//                    default:
//                        return false;
//                }
//            }
//
//            @Override
//            public String getMessage(Context context) {
//                return "Needs to be greater than previous value";
//            }
//        };
//
//        validator.put(odomInput, minOdomValRule);
//    }
}