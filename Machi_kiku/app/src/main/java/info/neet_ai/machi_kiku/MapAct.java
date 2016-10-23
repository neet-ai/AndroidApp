package info.neet_ai.machi_kiku;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;


import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.barcode.Barcode;

import android.app.*;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MapAct extends CommonAct implements
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener,
        OnMapReadyCallback {
    MapFragment mf;
    GoogleMap mMap;
    Timer timer = new Timer();
        boolean tFlag = false;
    boolean mFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //mf = (MapFragment) getFragmentManager().findFragmentById(R.id.map


        setContentView(R.layout.map_layout);
        super.onCreate(savedInstanceState);

        mf = MapFragment.newInstance();

        FragmentManager fm = getFragmentManager();
        android.app.FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.map, mf);
        ft.commit();
        //mLocationClient = new LocationClient(this, mConnectionCallbacks, mOnConnectionFailedListener);
        mf.getMapAsync(this);


        //findViewById(R.id.include_m_ctrl).bringToFront();
        //findViewById(R.id.include_drawer).bringToFront();

        //MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveCanceledListener(this);

        // We will provide our own zoom controls.
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        // Show Sydney
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-33.87365, 151.20689), 10));
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener(){
            @Override
            public void onCameraMove() {
                if(mFlag == false) {
                    tFlag = true;
                } else {
                    mFlag = false;
                    tFlag = false;
                }
            }
        });
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                tFlag = false;
                mFlag = true;
                return true;
            }
        });
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener(){
            @Override
            public void onMyLocationChange(Location loc) {
                if(tFlag == false) {
                    //LatLng curr = new LatLng(loc.getLatitude(), loc.getLongitude());
                    //mMap.animateCamera(CameraUpdateFactory.newLatLng(curr));
                    changeCamera();
                }
            }
        });
    }

    public void TimerStart(){
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                changeCamera();
            }
        }, 0, 500);
    }
    private void TimerFinish(){
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    public void changeCamera(){
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location myLocate = locationManager.getLastKnownLocation("gps");


        if(myLocate != null){
            //現在地情報取得成功
            //緯度の取得
            //int latitude = (int) (myLocate.getLatitude() * 1e6);
            //経度の取得
            //int longitude = (int) (myLocate.getLongitude() * 1e6);
            //GeoPointに緯度・経度を指定
            //Barcode.GeoPoint GP = new Barcode.GeoPoint(latitude, longitude);
            double lat = myLocate.getLatitude();
            double lon = myLocate.getLongitude();
            //Log.v("log",String.valueOf(lat)+String.valueOf(lon));
            CameraUpdate cmf = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), (float) 18.0);
            //現在地までアニメーションで移動
            //CameraPosition position = new CameraPosition.Builder().target(new LatLng(lat, lon)).build();
            mMap.animateCamera(cmf);
            //現在地までパッと移動
            //MapCtrl.setCenter(GP);
        }else{
            //現在地情報取得失敗時の処理
            Toast.makeText(this, "現在地取得できませーん！", Toast.LENGTH_SHORT).show();
        }

        tFlag = false;
        mFlag = true;
    }

    @Override
    public void onCameraIdle() {

    }

    @Override
    public void onCameraMoveCanceled() {

    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraMoveStarted(int i) {

    }
    /*@Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        /*mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener(){
            @Override
            public void onMyLocationChange(Location loc) {
                LatLng curr = new LatLng(loc.getLatitude(), loc.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLng(curr));
            }
        });

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }*/
    public void onGetCenter(View view) {
        CameraPosition cameraPos = mMap.getCameraPosition();
        Toast.makeText(this, "中心位置\n緯度:" + cameraPos.target.latitude + "\n経度:" + cameraPos.target.longitude, Toast.LENGTH_LONG).show();
    }
}
