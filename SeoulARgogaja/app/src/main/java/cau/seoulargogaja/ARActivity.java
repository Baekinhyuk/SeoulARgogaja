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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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

import cau.seoulargogaja.arcorelocation.LocationMarker;
import cau.seoulargogaja.arcorelocation.LocationScene;
import cau.seoulargogaja.arcorelocation.rendering.LocationNode;
import cau.seoulargogaja.arcorelocation.rendering.LocationNodeRender;
import cau.seoulargogaja.arcorelocation.utils.ARLocationPermissionHelper;

public class ARActivity extends AppCompatActivity implements View.OnClickListener {

    double start_latitude ;
    double start_longitude;
    double end_latitude, end_longitude;
    String content;
    LocationManager manager;
    private boolean installRequested;
    private boolean hasFinishedLoading = false;

    private ArSceneView arSceneView;

    private Snackbar loadingMessageSnackbar = null;

    private LocationScene locationScene;

    private ModelRenderable andyRenderable;
    private ViewRenderable exampleLayoutRenderable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        arSceneView = findViewById(R.id.ar_scene_view);

        Intent intent = getIntent();


        end_latitude = Double.parseDouble(intent.getStringExtra("latitude"));
        end_longitude = Double.parseDouble(intent.getStringExtra("longitude"));
        content = intent.getStringExtra("content");

        CompletableFuture<ViewRenderable> exampleLayout =
                ViewRenderable.builder()
                        .setView(this, R.layout.example_layout)
                        .build();

        //test add
        CompletableFuture<ModelRenderable> andy = ModelRenderable.builder()
                .setSource(this, R.raw.andy)
                .build();

        CompletableFuture.allOf(exampleLayout, andy)
                .handle(
                        (notUsed, throwable) ->
                        {
                            if (throwable != null) {
                                DemoUtils.displayError(this, "Unable to load renderables", throwable);
                                return null;
                            }

                            try {
                                exampleLayoutRenderable = exampleLayout.get();
                                andyRenderable = andy.get();
                                //hasFinishedLoading = true;

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
                                locationScene = new LocationScene(this, arSceneView);
                                locationScene.setAnchorRefreshInterval(60);


                                /*LocationMarker marker = new LocationMarker(
                                        end_longitude,
                                        end_latitude,
                                        getAndy(end_longitude, end_latitude, content));
                                marker.setHeight(0);
                                locationScene.mLocationMarkers.add(marker);
                                */
                                LocationMarker layoutLocationMarker = new LocationMarker(
                                        end_longitude,
                                        end_latitude,
                                        getExampleView(end_longitude, end_latitude)
                                );
                                layoutLocationMarker.setHeight(-5);

                                layoutLocationMarker.setRenderEvent(new LocationNodeRender() {
                                    @Override
                                    public void render(LocationNode node) {
                                        View eView = exampleLayoutRenderable.getView();
                                        ImageView distanceTextView = eView.findViewById(R.id.textView2);
                                        android.view.ViewGroup.LayoutParams layoutParams = distanceTextView.getLayoutParams();
                                        int length = 1024 - node.getDistance() * 5;
                                        if(length < 150)
                                            length = 150;
                                        layoutParams.width = length;
                                        layoutParams.height = length;
                                        distanceTextView.setLayoutParams(layoutParams);

                                        }
                                });
                                // Adding the marker
                                locationScene.mLocationMarkers.add(layoutLocationMarker);





                                /*LocationMarker dragon5 = new LocationMarker(
                                        126.957288,
                                        37.505600,
                                        getAndy(126.957288, 37.505600, "청룡호수0f"));

                                locationScene.mLocationMarkers.add(dragon5);
                                LocationMarker dragon6 = new LocationMarker(
                                        126.9575776,
                                        37.5058302,
                                        getAndy(126.9575776, 37.5058302, "청룡호수5f"));
                                dragon6.setHeight(5F);
                                locationScene.mLocationMarkers.add(dragon6);
                                */

                                /*
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
                                                */

                            }
                            Frame frame = arSceneView.getArFrame();
                            if (locationScene != null) {
                                locationScene.processFrame(frame);
                            }

                            if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                                return;
                            }

                            if (locationScene != null) {
                                locationScene.processFrame(frame);
                            }
                        });

        ARLocationPermissionHelper.requestPermission(this);

        Button b = findViewById(R.id.mapButton);
        b.setText(content);

        findViewById(R.id.mapButton).setOnClickListener(this::onClick);

    }

    private Node getExampleView(double longitude,double latitude) {
        Node base = new Node();
        base.setRenderable(exampleLayoutRenderable);
        Context c = this;
        // Add  listeners etc here
        View eView = exampleLayoutRenderable.getView();
        VectorCalc dist = new VectorCalc();
        base.setOnTapListener((v, event) -> {
            double vector = VectorCalc.distance(latitude, longitude, start_latitude,start_longitude, "k") * 1000;
            base.
            if(vector < 50.0){
                Toast.makeText(
                        c, "도착했습니다.", Toast.LENGTH_LONG)
                        .show();
                Intent intent = new Intent();
                setResult(1, intent);
                finish();

            }else {

                Toast.makeText(
                        c, String.format("%.0f", vector) + "m 남았습니다.", Toast.LENGTH_LONG)
                        .show();
            }
        });

        return base;
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
        //loadingMessageSnackbar.show();
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

            /*if (v.getId() == R.id.mapButton){
                Intent mapIntent = new Intent(this, MapActivity.class);
                mapIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(mapIntent);
            }*/

        //테스트용 현재위치 파악
        Toast.makeText(this, "현재위치 longitude : " + start_longitude + " latitude : " + start_latitude, Toast.LENGTH_SHORT).show();
    }

    private Node getAndy(double longitude,double latitude,String name) {
        Node base = new Node();
        base.setRenderable(andyRenderable);
        Context c = this;
        VectorCalc dist = new VectorCalc();
        base.setOnTapListener((v, event) -> {
            double vector = VectorCalc.distance(latitude, longitude, start_latitude,start_longitude, "k") * 1000;
            if(vector < 50.0){
                Toast.makeText(
                        c, "도착했습니다.", Toast.LENGTH_LONG)
                        .show();
                Intent intent = new Intent();
                setResult(1, intent);
                finish();

            }else {

                Toast.makeText(
                        c, String.format("%.0f", vector) + "m 남았습니다.", Toast.LENGTH_LONG)
                        .show();
            }
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

