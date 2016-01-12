package fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by zyuki on 1/12/2016.
 */
public final class DialogFragmentRouter {
    public static final int ALL_ID = -1;

    private DialogFragmentRouter() {}

    public static void instantiateDataEntryDF(Activity activity) {
        FragmentTransaction fTransaction = clearFragments(activity);

        DataEntryDialogFragment dataEntryDF = DataEntryDialogFragment.newInstance();
        dataEntryDF.show(fTransaction, "dialog");
    }

    public static void instantiateDeleteItemsDF(Activity activity, int entryID) {
        FragmentTransaction fTransaction = clearFragments(activity);

        DeleteItemsDialogFragment deleteItemsDF = DeleteItemsDialogFragment.newInstance(entryID);
        deleteItemsDF.show(fTransaction, "dialog");
    }

    private static FragmentTransaction clearFragments(Activity activity) {
        FragmentManager fManager = ((FragmentActivity)activity).getSupportFragmentManager();
        FragmentTransaction fTransaction = fManager.beginTransaction();

        Fragment previousFragment = fManager.findFragmentByTag("dialog");
        if(previousFragment != null) {fTransaction.remove(previousFragment);}

        fTransaction.addToBackStack(null);

        return fTransaction;
    }
}
