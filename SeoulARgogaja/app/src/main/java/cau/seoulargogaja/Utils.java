package cau.seoulargogaja;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import uk.co.appoly.arcorelocation.utils.ARLocationPermissionHelper;

public class Utils {
    private static final String TAG = "Utils";

    public static void displayError(
            final Context context, final String errorMsg, @Nullable final Throwable problem) {
        final String tag = context.getClass().getSimpleName();
        final String toastText;
        if (problem != null && problem.getMessage() != null) {
            Log.e(tag, errorMsg, problem);
            toastText = errorMsg + ": " + problem.getMessage();
        } else if (problem != null) {
            Log.e(tag, errorMsg, problem);
            toastText = errorMsg;
        } else {
            Log.e(tag, errorMsg);
            toastText = errorMsg;
        }

        displayMsg(context, toastText);

    }

    public static void displayMsg(Context context, String message){
        new Handler(Looper.getMainLooper())
                .post(
                        () -> {
                            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        });
    }

    public static Session createArSession(Activity activity, boolean installRequested)
            throws UnavailableException {
        Session session = null;
        // if we have the camera permission, create the session
        if (ARLocationPermissionHelper.hasPermission(activity)) {
            switch (ArCoreApk.getInstance().requestInstall(activity, !installRequested)) {
                case INSTALL_REQUESTED:
                    return null;
                case INSTALLED:
                    break;
            }
            session = new Session(activity);
            // IMPORTANT!!!  ArSceneView needs to use the non-blocking update mode.
            Config config = new Config(session);
            config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
            session.configure(config);
        }
        return session;
    }

    public static void handleSessionException(
            Activity activity, UnavailableException sessionException) {

        String message;
        if (sessionException instanceof UnavailableArcoreNotInstalledException) {
            message = "Please install ARCore";
        } else if (sessionException instanceof UnavailableApkTooOldException) {
            message = "Please update ARCore";
        } else if (sessionException instanceof UnavailableSdkTooOldException) {
            message = "Please update this app";
        } else if (sessionException instanceof UnavailableDeviceNotCompatibleException) {
            message = "This device does not support AR";
        } else {
            message = "Failed to create AR session";
            Log.e(TAG, "Exception: " + sessionException);
        }
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }
}