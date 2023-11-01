package com.example.obyan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.StyleSpan;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class navigationApp extends FragmentActivity implements OnMapReadyCallback {
    private final Handler handler = new Handler();
    private final int FINE_PERMISSION_CODE = 1;
    private final boolean isMapReady = false;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private TextView nav_timeTextView;
    private GoogleMap myMap;
    private SearchView mapSearchView;
    private TextView nav_dateTextView;
    private final Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            updateTimeAndDate();
            handler.postDelayed(this, 1000); // 1000 ms = 1 second
        }
    };
    private TextView temperatureTV;
    private ImageView iconIV;
    private ImageView buttonImage1;
    private ImageView buttonImage2;
    private ImageView buttonImage3;
    private ImageView buttonImage4;
    private boolean isButton1Enabled;
    private boolean isButton2Enabled;
    private boolean isButton3Enabled;
    private boolean isButton4Enabled;
    private Polyline routePolyline;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_app);

        mapSearchView = findViewById(R.id.mapSearch);
        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = mapSearchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null) {
                    Geocoder geocoder = new Geocoder(navigationApp.this);

                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);

                    if (currentLocation != null) {
                        LatLng destination = new LatLng(address.getLatitude(), address.getLongitude());
                        float zoomLevel = 16.0f; //This goes up to 21
                        myMap.addMarker(new MarkerOptions().position(destination).title(location));
                        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destination, zoomLevel));
                    }

                    if (addressList != null && !addressList.isEmpty()) {

                        LatLng destination = new LatLng(address.getLatitude(), address.getLongitude());
                        float zoomLevel = 16.0f;

                        // Clear any existing polyline
                        if (routePolyline != null) {
                            routePolyline.remove();
                        }

                        // Create a new polyline with multicolored segments
                        PolylineOptions polylineOptions = new PolylineOptions()
                                .add(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), destination)
                                .addSpan(new StyleSpan(Color.RED))  // First segment color
                                .addSpan(new StyleSpan(Color.GREEN));  // Second segment color

                        routePolyline = myMap.addPolyline(polylineOptions);

                        // Add markers for current and destination locations
                        myMap.clear(); // Clear existing markers
                        myMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).title("My Location"));
                        myMap.addMarker(new MarkerOptions().position(destination).title(location));

                        // Move camera to show both markers
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                        builder.include(destination);
                        LatLngBounds bounds = builder.build();
                        myMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

                    }
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);


        nav_timeTextView = findViewById(R.id.nav_timeTextView);
        nav_dateTextView = findViewById(R.id.nav_dateTextView);
        updateTimeAndDate();
        handler.postDelayed(updateTimeRunnable, 1000);

//=================================================================================================

//==================================================================================================

//==================================================================================================
        buttonImage1 = findViewById(R.id.buttonImage1);
        buttonImage2 = findViewById(R.id.buttonImage2);

        buttonImage3 = findViewById(R.id.buttonImage3);
        buttonImage4 = findViewById(R.id.buttonImage4);
        setupButtonImages();

    }

    public void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
                    mapFragment.getMapAsync(navigationApp.this);

                }
            }
        });
    }

    private void updateTimeAndDate() {
        // Get current time and date
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        // Format time
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        String currentTime = timeFormat.format(date);

        // Format date
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
        String currentDate = dateFormat.format(date);

        // Display time and date
        nav_timeTextView.setText(currentTime);
        nav_dateTextView.setText(currentDate);
    }


    private void setupButtonImages() {
        buttonImage1.setOnClickListener(v -> {
            isButton1Enabled = !isButton1Enabled;
            updateButtonImages();
        });

        buttonImage2.setOnClickListener(v -> {
            isButton2Enabled = !isButton2Enabled;
            updateButtonImages();
        });

        buttonImage3.setOnClickListener(v -> {
            isButton3Enabled = !isButton3Enabled;
            updateButtonImages();
        });

        buttonImage4.setOnClickListener(v -> {
            isButton4Enabled = !isButton4Enabled;
            updateButtonImages();
        });

        updateButtonImages();
    }

    private void updateButtonImages() {
        if (isButton2Enabled) {
            buttonImage2.setImageResource(R.drawable.fog);
        } else {
            buttonImage2.setImageResource(R.drawable.yellowfog);
        }

        if (isButton3Enabled) {
            buttonImage3.setImageResource(R.drawable.strong);
        } else {
            buttonImage3.setImageResource(R.drawable.yellowstrong);
        }

        if (isButton1Enabled) {
            buttonImage1.setImageResource(R.drawable.arrow2);
        } else {
            buttonImage1.setImageResource(R.drawable.greenarrow2);
        }

        if (isButton4Enabled) {
            buttonImage4.setImageResource(R.drawable.arrow1);
        } else {
            buttonImage4.setImageResource(R.drawable.greenarrow1);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        if (currentLocation != null) {
            LatLng loc = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            myMap.addMarker(new MarkerOptions().position(loc).title("My Location"));
            float zoomLevel = 16.0f; //This goes up to 21
            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, zoomLevel));

            Geocoder geocoder = new Geocoder(this); // Replace 'this' with your activity context
            try {
                List<Address> addresses = geocoder.getFromLocation(
                        currentLocation.getLatitude(), currentLocation.getLongitude(), 1
                );
                if (addresses != null && addresses.size() > 0) {
                    String cityName = addresses.get(0).getLocality(); // Get the city name

                    // Save the city name in a variable for further use
                    String myCityName = cityName;

                    //==============================================================================
                    temperatureTV = findViewById(R.id.idTVTemperature);
                    iconIV = findViewById(R.id.idIVIcon);

                    // Make the API request
                    RequestQueue requestQueue = Volley.newRequestQueue(this);

                    String url = "https://api.weatherapi.com/v1/forecast.json?key=5f50a1c9b1b14352bef133551231607&q=" + myCityName + "&days=1&aqi=yes&alerts=yes";


                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        // Extract the desired values from the JSON response
                                        JSONObject current = response.getJSONObject("current");
                                        double tempC = current.getDouble("temp_c");
                                        String conditionText = current.getJSONObject("condition").getString("text");
                                        String conditionIcon = current.getJSONObject("condition").getString("icon");

                                        // Set the values in the appropriate views
                                        temperatureTV.setText(tempC + "°C");
                                        Picasso.get().load("https:".concat(conditionIcon)).into(iconIV);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    error.printStackTrace();
                                }
                            });

                    // Add the request to the RequestQueue
                    requestQueue.add(jsonObjectRequest);


                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


            } else {
                Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
            }

        }

    }

}