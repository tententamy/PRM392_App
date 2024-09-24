package com.example.projectprm392.Activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.projectprm392.Adapter.CartAdapter;
import com.example.projectprm392.Helper.ChangeNumberItemsListener; // Ensure this import is included
import com.example.projectprm392.Helper.ManagmentCart;
import com.example.projectprm392.R;
import com.example.projectprm392.databinding.ActivityCartBinding;

public class CartActivity extends BaseActivity {

    private ActivityCartBinding binding;
    private double tax;
    private ManagmentCart managmentCart;

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
        // Pass the listener correctly to the adapter
        binding.cardView.setAdapter(new CartAdapter(managmentCart.getListCart(), new ChangeNumberItemsListener() {
            @Override
            public void changed() {
                calculatorCart(); // Call calculatorCart when items change
            }
        }, this)); // Pass context here
    }

    private void setVariable() {
        binding.back.setOnClickListener(v -> finish());
    }

    private void calculatorCart() {
        double percentTax = 0.02;
        double delivery = 10;
        double totalFee = managmentCart.getTotalFee();
        tax = Math.round(totalFee * percentTax * 100.0) / 100.0;
        double total = Math.round((managmentCart.getTotalFee() + tax + delivery) * 100.0) / 100.0; // Fix rounding logic
        double itemTotal = Math.round((managmentCart.getTotalFee() * 100.0)) / 100.0;

        binding.subtotalValue.setText("$" + itemTotal);
        binding.totalTaxValue.setText("$" + tax);
        binding.deliveryValue.setText("$" + delivery);
        binding.totalValue.setText("$" + total);
    }
}
