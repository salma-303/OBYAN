package com.example.obyan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final View[] batterySquares = new View[10];
    private final Handler handler = new Handler();
    private TextView timeTextView;
    private TextView dateTextView;
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
    private TextView curpercentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeTextView = findViewById(R.id.timeTextView);
        dateTextView = findViewById(R.id.dateTextView);
        updateTimeAndDate();
        handler.postDelayed(updateTimeRunnable, 1000);

//==================================================================================================
        TextView speedometerTextView = findViewById(R.id.speedometerTextView);

        // Set the initial value with unit
        int speed = 0; // Replace 0 with your desired initial speed value
        String unit = "km/h"; // Replace "km/h" with your desired unit
        speedometerTextView.setText(speed+" "+ unit );
//==================================================================================================
        temperatureTV = findViewById(R.id.idTVTemperature);
        iconIV = findViewById(R.id.idIVIcon);

        // Make the API request
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://api.weatherapi.com/v1/forecast.json?key=5f50a1c9b1b14352bef133551231607&q=Alexandria&days=1&aqi=yes&alerts=yes";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
//==================================================================================================
        buttonImage1 = findViewById(R.id.buttonImage1);
        buttonImage2 = findViewById(R.id.buttonImage2);

        buttonImage3 = findViewById(R.id.buttonImage3);
        buttonImage4 = findViewById(R.id.buttonImage4);
        setupButtonImages();
//==================================================================================================

        batterySquares[0] = findViewById(R.id.battery_square_1);
        batterySquares[1] = findViewById(R.id.battery_square_2);
        batterySquares[2] = findViewById(R.id.battery_square_3);
        batterySquares[3] = findViewById(R.id.battery_square_4);
        batterySquares[4] = findViewById(R.id.battery_square_5);
        batterySquares[5] = findViewById(R.id.battery_square_6);
        batterySquares[6] = findViewById(R.id.battery_square_7);
        batterySquares[7] = findViewById(R.id.battery_square_8);
        batterySquares[8] = findViewById(R.id.battery_square_9);
        batterySquares[9] = findViewById(R.id.battery_square_10);
        curpercentage = findViewById(R.id.batterypercentage);
        updateBatterySquares(0);
        curpercentage.setText(100 + "%");

//=================================================================================================
        ImageButton buttonRight3 = findViewById(R.id.buttonRight3);
        buttonRight3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, navigationApp.class);
                startActivity(intent);
            }
        });
//=================================================================================================
        ImageButton buttonLeft3 = findViewById(R.id.buttonLeft3);
        buttonLeft3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CastApp.class);
                startActivity(intent);
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
        timeTextView.setText(currentTime);
        dateTextView.setText(currentDate);
    }

    private void setupButtonImages() {
        isButton1Enabled = true;
        isButton2Enabled = true;
        isButton3Enabled = true;
        isButton4Enabled = true;

        updateButtonImages();
    }

    private void updateButtonImages() {
        if (isButton1Enabled) {
            buttonImage1.setImageResource(R.drawable.fog);
        } else {
            buttonImage1.setImageResource(R.drawable.yellowfog);
        }

        if (isButton2Enabled) {
            buttonImage2.setImageResource(R.drawable.strong);
        } else {
            buttonImage2.setImageResource(R.drawable.yellowstrong);
        }

        if (isButton3Enabled) {
            buttonImage3.setImageResource(R.drawable.arrow2);
        } else {
            buttonImage3.setImageResource(R.drawable.greenarrow2);
        }

        if (isButton4Enabled) {
            buttonImage4.setImageResource(R.drawable.arrow1);
        } else {
            buttonImage4.setImageResource(R.drawable.greenarrow1);
        }
    }

    private void updateBatterySquares(int batteryPercentage) {
        // Calculate the number of squares to make transparent based on battery percentage
        int transparentSquares = 10 - (batteryPercentage / 10);

        // Loop through each square and update their transparency with a delay
        for (int i = 9; i >= transparentSquares; i--) {
            final int squareIndex = i;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    batterySquares[squareIndex].setBackgroundColor(getResources().getColor(R.color.transparent));
                }
            }, (10 - i) * 200); // Adjust the duration (200 milliseconds here) to control the speed of disappearance
        }
    }


}