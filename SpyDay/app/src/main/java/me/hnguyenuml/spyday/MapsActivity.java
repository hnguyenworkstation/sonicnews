package me.hnguyenuml.spyday;

import android.*;
import android.Manifest;
import android.animation.Animator;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import android.support.design.widget.FloatingActionButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import me.hnguyenuml.spyday.BasicApp.SpyDayApplication;
import me.hnguyenuml.spyday.BasicApp.SpyDayPreferenceManager;

public class MapsActivity extends BaseActivity
        implements OnMapReadyCallback, PlaceSelectionListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "SpyMap";
    private final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private GoogleApiClient mGoogleClient;

    private static final int REQUEST_CODE = 1;
    private static final int REQUEST_PLACE_PICKER = 1;
    private static final int REQUEST_LOCATION_ACCESS = 0;

    private GoogleMap mMap;
    private SpyDayPreferenceManager mPreMan;
    private FloatingActionButton mFab;
    private Intent mainIntent;
    private Bundle mBundleAnimation;
    private View rootView;
    private Location lastKnownLocation;

    @Override
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mayRequestPermissions();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mainIntent = new Intent(MapsActivity.this, SpyDayActivity.class);
        mPreMan = new SpyDayPreferenceManager(getBaseContext());

        if (mGoogleClient == null) {
            mGoogleClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(AppIndex.API).build();
        }

        mFab = (FloatingActionButton) findViewById(R.id.map_fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == SpyDayApplication.getInstance()
                        .getPrefManager()
                        .getFirebaseAuth().getCurrentUser()) {
                    Intent temp = new Intent(MapsActivity.this, LoginActivity.class);
                    mBundleAnimation =
                            ActivityOptions.makeCustomAnimation(getApplicationContext(),
                                    R.anim.fade_in_from_left, R.anim.fade_out_to_right).toBundle();
                    startActivity(temp, mBundleAnimation);
                } else {
                    startActivity(mainIntent);
                }
            }
        });

            if (fetchLocation()) {
            updateCurrenInfo();
        }
    }

    private void updateCurrenInfo() {
        mPreMan.setInitLocation(lastKnownLocation);
    }

    @Nullable
    private boolean fetchLocation() {
        if (mayRequestPermissions()) {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                    requestPermission();
                }

                Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (lastKnownLocationGPS != null) {
                    lastKnownLocation = lastKnownLocationGPS;
                    return true;
                } else {
                    lastKnownLocation =  locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    System.out.println("1::" + lastKnownLocation);
                    System.out.println("2::" + lastKnownLocation.getLatitude());
                    return true;
                }
            }
        } else {
            requestPermission();
        }

        return false;
    }

    private boolean mayRequestPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {

        } else {
            requestPermission();
        }
        return false;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                },
                REQUEST_LOCATION_ACCESS);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (!mayRequestPermissions()){
            requestPermission();
        } else {
            initMap();
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (SpyDayApplication.getInstance().getPrefManager().getUser() == null) {
                    Intent temp = new Intent(MapsActivity.this, LoginActivity.class);
                    mBundleAnimation =
                            ActivityOptions.makeCustomAnimation(getApplicationContext(),
                                    R.anim.fade_in_from_left, R.anim.fade_out_to_right).toBundle();
                    startActivity(temp, mBundleAnimation);
                }
            }
        });
    }

    private void initMap() {
        // Add a marker in Sydney and move the camera
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lastKnownLocation.getLatitude(),
                        lastKnownLocation.getLongitude()))
                .title("Marker in Sydney"));

        CameraPosition newPos = new CameraPosition.Builder()
                .target(new LatLng(lastKnownLocation.getLatitude(),
                        lastKnownLocation.getLongitude()))
                .zoom(12)
                .bearing(300)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newPos), new GoogleMap.CancelableCallback() {

            @Override
            public void onFinish() {
                mMap.getUiSettings().setScrollGesturesEnabled(true);
            }

            @Override
            public void onCancel() {
                mMap.getUiSettings().setAllGesturesEnabled(true);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.place_search:
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
                    startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
                } catch (GooglePlayServicesRepairableException e) {
                    GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                            0).show();
                } catch (GooglePlayServicesNotAvailableException e) {
                    String message = "Google Play Services is not available: " +
                            GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

                    Log.e(TAG, message);
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place Selected: " + place.getName());

                Toast.makeText(this, formatPlaceDetails(getResources(), place.getName(),
                        place.getId(), place.getAddress(), place.getPhoneNumber(),
                        place.getWebsiteUri()), Toast.LENGTH_LONG).show();

                CharSequence attributions = place.getAttributions();
                if (!TextUtils.isEmpty(attributions)) {
                    Toast.makeText(this, Html.fromHtml(attributions.toString()), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "", Toast.LENGTH_LONG).show();
                }

                moveToNewPlace(place.getLatLng());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e(TAG, "Error: Status = " + status.toString());
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    private void moveToNewPlace(LatLng newPlace) {
        CameraPosition newPos = new CameraPosition.Builder()
                .target(newPlace)
                .zoom(12)
                .bearing(300)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newPos), new GoogleMap.CancelableCallback() {

            @Override
            public void onFinish() {
                mMap.getUiSettings().setScrollGesturesEnabled(true);
            }

            @Override
            public void onCancel() {
                mMap.getUiSettings().setAllGesturesEnabled(true);
            }
        });

    }

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPlaceSelected(Place place) {

    }

    @Override
    public void onError(Status status) {
        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_ACCESS:
                Log.i(TAG, "Received response for Location permission request.");

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "LOCATION permission has now been granted. Showing preview.");
                    fetchLocation();
                    initMap();
                } else {
                    Log.i(TAG, "LOCATION permission was NOT granted.");
                }
                return;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                return;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;

        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    @NonNull
    private static LatLngBounds getLatLngBounds(LatLng pos) {
        LatLngBounds.Builder builder = LatLngBounds.builder();
        builder.include(pos);
        return builder.build();
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleClient.connect();
        AppIndex.AppIndexApi.start(mGoogleClient, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        AppIndex.AppIndexApi.end(mGoogleClient, getIndexApiAction());
        mGoogleClient.disconnect();
    }
}
