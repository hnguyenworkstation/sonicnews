package com.greenfam.sonicnews;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class MapsActivity extends SonicNewsActivity
        implements OnMapReadyCallback, PlaceSelectionListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "Maps";
    private GoogleMap mMap;
    private Button dropOffButton;
    private final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private GoogleApiClient mGoogleClient;
    private Location lastKnownLocation;
    private CameraPosition lastKnownCameraPos;

    /**
     * Request code passed to the PlacePicker intent to identify its result when it returns.
     */
    private static final int REQUEST_PLACE_PICKER = 1;

    private static final int REQUEST_LOCATION_ACCESS = 0;

    /**
     * Request code for the autocomplete activity. This will be used to identify results from the
     * autocomplete activity in onActivityResult.
     */
    @Override
    @RequiresPermission(anyOf = {android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        dropOffButton = (Button) findViewById(R.id.pick_location_button);
        dropOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropOff(mMap.getCameraPosition().target);
            }
        });

        mapFragment.getMapAsync(this);

        if (mGoogleClient == null) {
            mGoogleClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        }

        getLocation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.place_search:
                try {
                    // The autocomplete activity requires Google Play Services to be available. The intent
                    // builder checks this and throws an exception if it is not the case.
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
                    startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
                } catch (GooglePlayServicesRepairableException e) {
                    // Indicates that Google Play Services is either not installed or not up to date. Prompt
                    // the user to correct the issue.
                    GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                            0 /* requestCode */).show();
                } catch (GooglePlayServicesNotAvailableException e) {
                    // Indicates that Google Play Services is not available and the problem is not easily
                    // resolvable.
                    String message = "Google Play Services is not available: " +
                            GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

                    Log.e(TAG, message);
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Nullable
    private Location getLocation() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }

            Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocationGPS != null) {
                return lastKnownLocationGPS;
            } else {
                Location loc =  locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                System.out.println("1::" + loc);
                System.out.println("2::" + loc.getLatitude());
                return loc;
            }
        } else {
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place Selected: " + place.getName());

                Toast.makeText(this, formatPlaceDetails(getResources(), place.getName(),
                        place.getId(), place.getAddress(), place.getPhoneNumber(),
                        place.getWebsiteUri()), Toast.LENGTH_LONG).show();

                // Display attributions if required.
                CharSequence attributions = place.getAttributions();
                if (!TextUtils.isEmpty(attributions)) {
                    Toast.makeText(this, Html.fromHtml(attributions.toString()), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "", Toast.LENGTH_LONG).show();
                }

                // Then move to this new location
                moveToNewPlace(place.getLatLng());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e(TAG, "Error: Status = " + status.toString());
            } else if (resultCode == RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }
        }
    }

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }

    private void moveToNewPlace(LatLng newPlace) {
        CameraPosition newPos = new CameraPosition.Builder()
                .target(newPlace)
                .zoom(12)
                .bearing(300)
                .build();

        // Update last known Cameraposition
        lastKnownCameraPos = newPos;

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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void requestLocationPermission() {
        // Camera permission has not been granted yet. Request it directly.
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                },
                REQUEST_LOCATION_ACCESS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_ACCESS:
                // BEGIN_INCLUDE(permission_result)
                // Received permission result for camera permission.
                Log.i(TAG, "Received response for Camera permission request.");

                // Check if the only required permission has been granted
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Camera permission has been granted, preview can be displayed
                    Log.i(TAG, "LOCATION permission has now been granted. Showing preview.");
                    lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                            mGoogleClient);

                    // then setup the map
                    setUpInitialMap();
                } else {
                    Log.i(TAG, "CAMERA permission was NOT granted.");
                }
                // END_INCLUDE(permission_result)
                return;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                return;
        }
    }

    @Override
    protected void onStart() {
        mGoogleClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleClient.disconnect();
        super.onStop();
    }


    private void dropOff(LatLng here) {
        // Todo: Write function to drop prople to this location
        Toast.makeText(MapsActivity.this, "Dropped!", Toast.LENGTH_SHORT).show();

        // BEGIN_INCLUDE(intent)
            /* Use the PlacePicker Builder to construct an Intent.
            Note: This sample demonstrates a basic use case.
            The PlacePicker Builder supports additional properties such as search bounds.
             */
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
            // Start the Intent by requesting a result, identified by a request code.

            intentBuilder.setLatLngBounds(getLatLngBounds(here));
            startActivityForResult(intent, REQUEST_PLACE_PICKER);

        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil
                    .getErrorDialog(e.getConnectionStatusCode(), this, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(this, "Google Play Services is not available.",
                    Toast.LENGTH_LONG)
                    .show();
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

    @Override
    public void onPlaceSelected(Place place) {
    }

    @Override
    public void onError(Status status) {
        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void setUpInitialMap() {
        // Todo fix the initial position when map start and resume
        if (lastKnownCameraPos == null) {
            lastKnownCameraPos = new CameraPosition.Builder()
                    .target(new LatLng(lastKnownLocation.getLatitude(),
                            lastKnownLocation.getLongitude()))
                    .zoom(12)
                    .bearing(300)
                    .build();
        }

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(lastKnownCameraPos),
            new GoogleMap.CancelableCallback() {
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
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (lastKnownLocation == null)
        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleClient);

        // then setup the map
        setUpInitialMap();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
