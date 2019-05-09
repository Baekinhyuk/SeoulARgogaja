package cau.seoulargogaja;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import uk.co.appoly.arcorelocation.LocationMarker;
import uk.co.appoly.arcorelocation.LocationScene;
import uk.co.appoly.arcorelocation.utils.ARLocationPermissionHelper;

public class ARActivity extends AppCompatActivity implements View.OnClickListener{

    double start_latitude;
    double start_longitude;
    LocationManager manager;
    private boolean installRequested;
    private boolean hasFinishedLoading = false;

    private ArSceneView arSceneView;

    private Snackbar loadingMessageSnackbar = null;

    private LocationScene locationScene;

    private ModelRenderable andyRenderable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        arSceneView = findViewById(R.id.ar_scene_view);

        //test add
        CompletableFuture<ModelRenderable> andy = ModelRenderable.builder()
                .setSource(this, R.raw.andy)
                .build();

        CompletableFuture.allOf(andy)
                .handle(
                        (notUsed, throwable) ->
                        {
                            if (throwable != null) {
                                DemoUtils.displayError(this, "Unable to load renderables", throwable);
                                return null;
                            }

                            try {
                                andyRenderable = andy.get();

                            } catch (InterruptedException | ExecutionException ex) {
                                DemoUtils.displayError(this, "Unable to load renderables", ex);
                            }
                            return null;
                        });


        arSceneView
                .getScene()
                .setOnUpdateListener(
                        frameTime -> {
                            startLocationService();
                            if (locationScene == null) {
                                locationScene = new LocationScene(this, this, arSceneView);
                                /* 동네 테스트용
                                locationScene.mLocationMarkers.add(
                                        new LocationMarker(
                                                127.1308388,
                                                37.3768711,
                                                getAndy(127.1308388, 37.3768711, "419동중앙")));
                                locationScene.mLocationMarkers.add(
                                        new LocationMarker(
                                                127.1312358,
                                                37.3770951,
                                                getAndy(127.1312358, 37.3770951, "420동방향")));
                                locationScene.mLocationMarkers.add(
                                        new LocationMarker(
                                                127.1305038,
                                                37.3766561,
                                                getAndy(127.1305038, 37.3766561, "418동방향")));
                                locationScene.mLocationMarkers.add(
                                        new LocationMarker(
                                                127.1311258,
                                                37.3766371,
                                                getAndy(127.1311258, 37.3766371, "419동당촌초길가방향")));
                                 */
                                locationScene.mLocationMarkers.add(
                                        new LocationMarker(
                                                126.9573886,
                                                37.5049253,
                                                getAndy(126.9573886, 37.5049253, "중앙대학교 중앙도서관")));
                                locationScene.mLocationMarkers.add(
                                        new LocationMarker(
                                                126.9563678,
                                                37.5047367,
                                                getAndy(126.9563678, 37.5047367, "중앙대학교 서라벌홀")));
                                locationScene.mLocationMarkers.add(
                                        new LocationMarker(
                                                126.9546487,
                                                37.504623,
                                                getAndy(126.9546487, 37.504623, "중앙대학교 법학관")));
                                locationScene.mLocationMarkers.add(
                                        new LocationMarker(
                                                126.9559847,
                                                37.503576,
                                                getAndy(126.9559847, 37.503576, "중앙대학교 제2공학관")));
                                locationScene.mLocationMarkers.add(
                                        new LocationMarker(
                                                126.9553197,
                                                37.503636,
                                                getAndy(126.9553197, 37.503636, "중앙대학교 대운동장")));

                            }
                            Frame frame = arSceneView.getArFrame();
                            if (locationScene != null) {
                                locationScene.processFrame(frame);
                            }
                        });

        ARLocationPermissionHelper.requestPermission(this);

        findViewById(R.id.mapButton).setOnClickListener(this::onClick);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (locationScene != null) {
            locationScene.resume();
        }

        if (arSceneView.getSession() == null) {
            // If the session wasn't created yet, don't resume rendering.
            // This can happen if ARCore needs to be updated or permissions are not granted yet.
            try {
                Session session = Utils.createArSession(this, installRequested);
                if (session == null) {
                    installRequested = ARLocationPermissionHelper.hasPermission(this);
                    return;
                } else {
                    arSceneView.setupSession(session);
                }
            } catch (UnavailableException e) {
                Utils.handleSessionException(this, e);
            }
        }

        try {
            arSceneView.resume();
        } catch (CameraNotAvailableException ex) {
            Utils.displayError(this, "Unable to get camera", ex);
            finish();
            return;
        }

        if (arSceneView.getSession() != null) {
            showLoadingMessage();
        }
    }

    /**
     * Make sure we call locationScene.pause();
     */
    @Override
    public void onPause() {
        super.onPause();

        if (locationScene != null) {
            locationScene.pause();
        }

        arSceneView.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        arSceneView.destroy();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        if (!ARLocationPermissionHelper.hasPermission(this)) {
            if (!ARLocationPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                ARLocationPermissionHelper.launchPermissionSettings(this);
            } else {
                Toast.makeText(
                        this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                        .show();
            }
            finish();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Standard Android full-screen functionality.
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void showLoadingMessage() {
        if (loadingMessageSnackbar != null && loadingMessageSnackbar.isShownOrQueued()) {
            return;
        }

        loadingMessageSnackbar =
                Snackbar.make(
                        ARActivity.this.findViewById(android.R.id.content),
                        "Detecting Plane/Surface",
                        Snackbar.LENGTH_INDEFINITE);
        loadingMessageSnackbar.getView().setBackgroundColor(0xbf323232);
        loadingMessageSnackbar.show();
    }

    private void hideLoadingMessage() {
        if (loadingMessageSnackbar == null) {
            return;
        }

        loadingMessageSnackbar.dismiss();
        loadingMessageSnackbar = null;
    }

    @Override
    public void onClick(View v) {
            /*
            if (v.getId() == R.id.mapButton){
                Intent mapIntent = new Intent(this, MapActivity.class);
                mapIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(mapIntent);
            }
            */
            //테스트용 현재위치 파악
        Toast.makeText(this, "현재위치 longitude : "+start_longitude+" latitude : "+start_latitude, Toast.LENGTH_SHORT).show();
        VectorCalc dist = new VectorCalc();
        Toast.makeText(this, "거리계산결과 : " +Boolean.toString(dist.distance(37.3768711,127.1308388,start_latitude,start_longitude,"k")<=5), Toast.LENGTH_SHORT).show();
    }

    private Node getAndy(double longitude,double latitude,String name) {
        Node base = new Node();
        base.setRenderable(andyRenderable);
        Context c = this;
        VectorCalc dist = new VectorCalc();
        base.setOnTapListener((v, event) -> {
            Toast.makeText(
                    c, "거리 : "+        String.format("%.2f", dist.distance(latitude,longitude,start_latitude,start_longitude,"m")) +"m Name : "+name +" longitude : "+longitude+"  latitude : "+latitude, Toast.LENGTH_LONG)
                    .show();
        });
        return base;
    }

    public void startLocationService() {
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, gpsListener);
    }

    private final LocationListener gpsListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            start_latitude = location.getLatitude();
            start_longitude = location.getLongitude();
            Log.d("s_latitude : "+start_latitude,"s_longitude : "+start_longitude); //여기서는 받아온다
            stopLocationService();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("error","1");

        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("error","2");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("error","3");
        }
    };

    public void stopLocationService(){
        manager.removeUpdates(gpsListener);
    }
}

