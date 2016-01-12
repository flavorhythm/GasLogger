package fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;

import com.zenoyuki.flavorhythm.gaslogger.ApplicationDatabase;

import data.DataAccessObject;
import data.FillupTable;
import model.FuelLog;

/**
 * Created by zyuki on 1/12/2016.
 */
public class DeleteItemsDialogFragment extends DialogFragment {
    private static final String ENTRY_ID_KEY = "item_id_key";
    DataAccessObject dataAO;
    AlertDialog.Builder builder;

    public static DeleteItemsDialogFragment newInstance(int entryID) {
        DeleteItemsDialogFragment dialogFragment = new DeleteItemsDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ENTRY_ID_KEY, entryID);

        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dataAO = ((ApplicationDatabase)getActivity().getApplication()).mDataAO;
        int id = getArguments().getInt(ENTRY_ID_KEY);

        if(id == DialogFragmentRouter.ALL_ID) {
            return delAllDialog().create();
        } else {
            return delOneDialog(getArguments().getInt(ENTRY_ID_KEY)).create();
        }
    }

    private AlertDialog.Builder delAllDialog() {
        builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("testingDelAll");

        return builder;
    }

    private AlertDialog.Builder delOneDialog(final int entryID) {
        builder = new AlertDialog.Builder(getActivity());

        String query = FillupTable.KEY_ID + " = " + entryID;
        FuelLog entry = dataAO.getEntry(query, null);

        builder .setTitle("Topup at: " + entry.getCurrentOdomVal() + " miles")
                .setMessage("Delete?")
                .setPositiveButton("Destroy!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataAO.deleteEntry(entryID);
                    }
                })
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return builder;
    }
}
