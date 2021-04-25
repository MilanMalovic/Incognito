
package mm.com.myinstantappdemo.wifi.lib;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.LocationManager;
import android.provider.Settings;
import mm.com.myinstantappdemo.R;

/**
 * AccessRequester is responsible for creating dialog for opening Location Settings, what allows
 * user to enable Location Services on the device
 */
@SuppressWarnings("PMD")
public class AccessRequester {

    private AccessRequester() {
    }

    /**
     * Checks if Location Services are turned on the device
     *
     * @param context of the activity or application
     * @return true if location is enabled, false if not
     */
    public static boolean isLocationEnabled(final Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsProviderEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkProviderEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return isGpsProviderEnabled || isNetworkProviderEnabled;
    }

    /**
     * Opens dialog which allows user to turn Location Services on in the settings
     *
     * @param activity where dialog is created
     */
    public static void requestLocationAccess(final Activity activity) {
        buildLocationAccessDialog(activity, (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            activity.startActivity(intent);
        }).show();
    }

    /**
     * Opens dialog which allows user to turn Location Services on in the settings
     *
     * @param activity where dialog is created
     * @param title    of the dialog window
     * @param message  of the dialog window
     */
    public static void requestLocationAccess(final Activity activity, final String title,
            final String message) {
        buildLocationAccessDialog(activity, (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            activity.startActivity(intent);
        }, title, message).show();
    }

    /**
     * Creates dialog for accessing Location Services
     *
     * @param activity          where dialog is build
     * @param onOkClickListener implementation for action customisation
     * @return dialog builder
     */
    public static AlertDialog.Builder buildLocationAccessDialog(final Activity activity,
            final DialogInterface.OnClickListener onOkClickListener) {
        final Resources resources = activity.getResources();
        final String title = resources.getString(R.string.requesting_location_access);
        final String message = resources.getString(R.string.do_you_want_to_open_location_settings);
        return buildLocationAccessDialog(activity, onOkClickListener, title, message);
    }

    /**
     * Creates dialog for accessing Location Services
     *
     * @param activity          where dialog is build
     * @param onOkClickListener implementation for action customisation
     * @param title             of the dialog window
     * @param message           of the dialog window
     * @return dialog builder
     */
    public static AlertDialog.Builder buildLocationAccessDialog(Activity activity,
            DialogInterface.OnClickListener onOkClickListener, String title, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.yes, onOkClickListener);
        builder.setNegativeButton(R.string.no, null);
        builder.setCancelable(true);
        return builder;
    }
}
