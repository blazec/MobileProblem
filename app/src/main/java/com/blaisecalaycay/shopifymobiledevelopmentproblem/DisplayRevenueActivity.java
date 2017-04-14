package com.blaisecalaycay.shopifymobiledevelopmentproblem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DisplayRevenueActivity extends AppCompatActivity {

    private OkHttpClient okHttpClient;
    private Request request;
    private String url = "https://shopicruit.myshopify.com/admin/orders.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6";
    private double revenue = 0;
    private int totalSold = 0;
    TextView revenueTextView;
    TextView totalSoldTextView;

    public static final String productName = "Aerodynamic Cotton Keyboard";
    public static final String TAG = DisplayRevenueActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title);
        setContentView(R.layout.activity_display_revenue);


        /* Set up okhttp to get data from server */
        okHttpClient = new OkHttpClient();
        request = new Request.Builder().url(url).build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, e.getMessage());
                Log.i(TAG, "FAILLLL\n\n\n");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                /* Get JSON data */
                String data = response.body().string();
                System.out.println(data);

                try {

                    JSONObject ordersJSON = new JSONObject(data);
                    JSONArray ordersArray = ordersJSON.getJSONArray("orders");

                    /* Get revenue and numbers */
                    for (int i = 0; i < ordersArray.length(); i++) {
                        JSONObject order = ordersArray.getJSONObject(i);
                        JSONArray orderItemsArray = order.getJSONArray("line_items");

                        for (int j = 0; j < orderItemsArray.length(); j++) {
                            JSONObject orderItems = orderItemsArray.getJSONObject(j);
                            if (orderItems.getString("title").equals(productName)) {
                                // Get price
                                double price = orderItems.getDouble("price");
                                int quantity = orderItems.getInt("quantity");
                                revenue += price * quantity;
                                totalSold += quantity;

                            }
                        }

                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateDisplay(revenue, totalSold);
                        }
                    });
//                    Log.i(TAG, "Revenue: " + Double.toString(revenue));
//                    Log.i(TAG, "Total sold: " + Integer.toString(totalSold));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });



    }

    private void updateDisplay(double revenue, int totalSold) {
        revenueTextView = (TextView) findViewById(R.id.revenueTextView);
        revenueTextView.setText(String.format("%.2f", revenue) + " CAD");
        totalSoldTextView = (TextView) findViewById(R.id.totalSoldTextView);
        totalSoldTextView.setText(Integer.toString(totalSold));
    }

}

