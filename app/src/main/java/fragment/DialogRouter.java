package fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by zyuki on 1/12/2016.
 */
public final class DialogRouter {
    private DialogRouter() {}

    public static void showEntryDialog(Activity activity, int entryId) {
        FragmentTransaction fTransaction = clearFragments(activity);

        DialogItemEntry entryDialog = DialogItemEntry.newInstance(entryId);
        entryDialog.show(fTransaction, "dialog");
    }

    public static void showDeleteDialog(Activity activity) {
        FragmentTransaction fTransaction = clearFragments(activity);

        DialogItemDelete deleteDialog = DialogItemDelete.newInstance();
        deleteDialog.show(fTransaction, "dialog");
    }

    private static FragmentTransaction clearFragments(Activity activity) {
        final FragmentManager fManager = ((FragmentActivity)activity).getSupportFragmentManager();

        FragmentTransaction fragTrans = fManager.beginTransaction();

        Fragment previousFragment = fManager.findFragmentByTag("dialog");
        if(previousFragment != null) {fragTrans.remove(previousFragment);}

        fragTrans.addToBackStack(null);

        return fragTrans;
    }
}
