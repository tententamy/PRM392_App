package com.example.projectprm392.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.projectprm392.Adapter.CartAdapter;
import com.example.projectprm392.Helper.ChangeNumberItemsListener;
import com.example.projectprm392.Helper.ManagmentCart;
import com.example.projectprm392.R;
import com.example.projectprm392.databinding.ActivityCartBinding;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class CartActivity extends BaseActivity {

    private ActivityCartBinding binding;
    private double tax;
    private ManagmentCart managmentCart;
    private static final String PAYPAL_CLIENT_ID = "AekmRj4s_nGVcEsrZxVJWTE7DGUQY7inpPAHZMxQTmOIcK6MszC9h7X2RU_lGYdmYk44nOOUr828hrev";
    private static final String PAYPAL_CLIENT_SECRET = "EAYo1fo-YkSfUXGwP9CrLAu7Ql5J_TAljAesK8tYH7ezuv3Qk-ezmDQDjUnPGQ9clZSgnjqexuw9p8Rl"; // Replace with your actual client secret

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart = new ManagmentCart(this);

        calculatorCart();
        setVariable();
        initCartList();
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
        new Thread(() -> {
            try {
                String orderId = createPayPalOrder(total);
                if (orderId != null) {
                    runOnUiThread(() -> {
                        Toast.makeText(CartActivity.this, "Order created: " + orderId, Toast.LENGTH_LONG).show();
                        // Here you would typically start the payment approval process
                        // For example, you might open a WebView to complete the payment
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(CartActivity.this, "Failed to create order", Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(CartActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private String createPayPalOrder(double amount) throws Exception {
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
                + "}]"
                + "}";

        try(OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getString("id");
        }
    }

    private String getBasicAuthHeader() {
        String auth = PAYPAL_CLIENT_ID + ":" + PAYPAL_CLIENT_SECRET;
        return android.util.Base64.encodeToString(auth.getBytes(StandardCharsets.UTF_8), android.util.Base64.NO_WRAP);
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