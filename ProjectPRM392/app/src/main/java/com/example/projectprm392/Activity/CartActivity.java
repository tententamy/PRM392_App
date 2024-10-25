package com.example.projectprm392.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.projectprm392.Adapter.CartAdapter;
import com.example.projectprm392.Helper.ChangeNumberItemsListener;
import com.example.projectprm392.Helper.ManagmentCart;
import com.example.projectprm392.Helper.GlobalState; // Import for GlobalState
import com.example.projectprm392.databinding.ActivityCartBinding;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.projectprm392.Domain.HistoryDomain;
import com.example.projectprm392.Domain.ItemsDomain;

public class CartActivity extends BaseActivity {

    private ActivityCartBinding binding;
    private double tax;
    private ManagmentCart managmentCart;
    private static final String PAYPAL_CLIENT_ID = "AekmRj4s_nGVcEsrZxVJWTE7DGUQY7inpPAHZMxQTmOIcK6MszC9h7X2RU_lGYdmYk44nOOUr828hrev";
    private static final String PAYPAL_CLIENT_SECRET = "EAYo1fo-YkSfUXGwP9CrLAu7Ql5J_TAljAesK8tYH7ezuv3Qk-ezmDQDjUnPGQ9clZSgnjqexuw9p8Rl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart = new ManagmentCart(this);

        calculatorCart();
        setVariable();
        initCartList();
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        Uri data = intent.getData();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            handlePayPalResult(data.toString());
        }
    }

    private void initCartList() {
        if (managmentCart.getListCart().isEmpty()) {
            binding.empty1.setVisibility(View.VISIBLE);
            binding.cardView.setVisibility(View.VISIBLE);
            binding.scrollViewCart.setVisibility(View.GONE);
        } else {
            binding.empty1.setVisibility(View.GONE);
            binding.scrollViewCart.setVisibility(View.VISIBLE);
        }

        binding.cardView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.cardView.setAdapter(new CartAdapter(managmentCart.getListCart(), new ChangeNumberItemsListener() {
            @Override
            public void changed() {
                calculatorCart();
            }
        }, this));
    }

    private void setVariable() {
        binding.back.setOnClickListener(v -> finish());
        binding.checkoutButton.setOnClickListener(v -> initiateCheckout());
    }

    private void initiateCheckout() {
        double total = Double.parseDouble(binding.totalValue.getText().toString().replace("$", ""));
        Log.d("PayPal", "Initiating checkout for amount: " + total);
        new Thread(() -> {
            try {
                JSONObject orderResponse = createPayPalOrder(total);
                Log.d("PayPal", "Order response: " + orderResponse);
                if (orderResponse != null) {
                    String approvalUrl = getApprovalUrl(orderResponse);
                    Log.d("PayPal", "Approval URL: " + approvalUrl);
                    if (approvalUrl != null) {
                        runOnUiThread(() -> openBrowser(approvalUrl));
                    } else {
                        runOnUiThread(() -> Toast.makeText(CartActivity.this, "Failed to get approval URL", Toast.LENGTH_LONG).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(CartActivity.this, "Failed to create order", Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                Log.e("PayPal", "Error in checkout process", e);
                runOnUiThread(() -> Toast.makeText(CartActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void openBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private JSONObject createPayPalOrder(double amount) throws Exception {
        URL url = new URL("https://api-m.sandbox.paypal.com/v2/checkout/orders");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Basic " + getBasicAuthHeader());
        conn.setDoOutput(true);

        String jsonInputString = "{"
                + "\"intent\": \"CAPTURE\","
                + "\"purchase_units\": [{"
                + "  \"amount\": {"
                + "    \"currency_code\": \"USD\","
                + "    \"value\": \"" + amount + "\""
                + "  }"
                + "}],"
                + "\"application_context\": {"
                + "  \"return_url\": \"myapp://payment/success\","
                + "  \"cancel_url\": \"myapp://payment/cancel\""
                + "}"
                + "}";

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return new JSONObject(response.toString());
        }
    }

    private String getApprovalUrl(JSONObject orderResponse) throws Exception {
        JSONObject links = orderResponse.getJSONArray("links").getJSONObject(1);
        if ("approve".equals(links.getString("rel"))) {
            return links.getString("href");
        }
        return null;
    }

    private String getBasicAuthHeader() {
        String auth = PAYPAL_CLIENT_ID + ":" + PAYPAL_CLIENT_SECRET;
        return android.util.Base64.encodeToString(auth.getBytes(StandardCharsets.UTF_8), android.util.Base64.NO_WRAP);
    }

    private void handlePayPalResult(String url) {
        if (url.contains("success")) {
            Uri uri = Uri.parse(url);
            String orderId = uri.getQueryParameter("token");
            if (orderId != null) {
                capturePayment(orderId);
            } else {
                Toast.makeText(this, "Order ID not found in return URL", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Payment cancelled or failed", Toast.LENGTH_LONG).show();
        }
    }

    private void capturePayment(String orderId) {
        new Thread(() -> {
            try {
                URL url = new URL("https://api-m.sandbox.paypal.com/v2/checkout/orders/" + orderId + "/capture");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Basic " + getBasicAuthHeader());
                conn.setDoOutput(true);

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    Log.d("PayPal", "Capture response: " + response.toString());

                    // Add new record to History after successful payment capture
                    addToHistory(orderId); // Call the updated method

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Payment captured successfully!", Toast.LENGTH_LONG).show();
                        managmentCart.clearCart();
                        calculatorCart();
                        initCartList();
                    });
                } else {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    Log.e("PayPal", "Capture error response: " + response.toString());

                    handlePaymentError("Failed to capture payment. Response code: " + responseCode + ", Response: " + response.toString());
                }
            } catch (Exception e) {
                handlePaymentError("Error capturing payment: " + e.getMessage());
            }
        }).start();
    }

    private void addToHistory(String orderId) {
        for (ItemsDomain item : managmentCart.getListCart()) {
            double pricing = item.getPrice();
            String productId = item.getTitle();
            String username = GlobalState.getInstance().getUserEmail();

            HistoryDomain historyEntry = new HistoryDomain(orderId, pricing, productId, username);
            DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("History");

            historyRef.child(orderId).setValue(historyEntry)
                    .addOnSuccessListener(aVoid -> Log.d("History", "History entry added successfully"))
                    .addOnFailureListener(e -> Log.e("History", "Failed to add history entry: " + e.getMessage()));
        }
    }

    private void handlePaymentError(String errorMessage) {
        Log.e("PayPal", "Payment error: " + errorMessage);
        runOnUiThread(() -> {
            String userMessage = "Payment error: " + errorMessage;
            // Truncate the message if it's too long
            if (userMessage.length() > 100) {
                userMessage = userMessage.substring(0, 97) + "...";
            }
            Toast.makeText(this, userMessage, Toast.LENGTH_LONG).show();
        });
    }

    private void calculatorCart() {
        double percentTax = 0.02;
        double delivery = 10;
        double totalFee = managmentCart.getTotalFee();
        tax = Math.round(totalFee * percentTax * 100.0) / 100.0;
        double total = Math.round((managmentCart.getTotalFee() + tax + delivery) * 100.0) / 100.0;
        double itemTotal = Math.round((managmentCart.getTotalFee() * 100.0)) / 100.0;

        binding.subtotalValue.setText("$" + itemTotal);
        binding.totalTaxValue.setText("$" + tax);
        binding.deliveryValue.setText("$" + delivery);
        binding.totalValue.setText("$" + total);
    }
}