package com.example.projectprm392.Activity;

import android.os.Bundle;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectprm392.Adapter.ColorAdapter;
import com.example.projectprm392.Adapter.SizeAdapter;
import com.example.projectprm392.Adapter.SliderAdapter;
import com.example.projectprm392.Domain.ItemsDomain;
import com.example.projectprm392.Domain.SliderItems;
import com.example.projectprm392.Helper.ManagmentCart;
import com.example.projectprm392.databinding.ActivityDetailBinding;

import java.util.ArrayList;

public class DetailActivity extends BaseActivity {
    private ActivityDetailBinding binding;
    private ItemsDomain object;
    private final int numberOrder = 1;
    private ManagmentCart managmentCart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart = new ManagmentCart(this);

        getBundles();
        initBanners();
        initSize();
        initColor();
    }

    private void initColor() {
        ArrayList<String> list = new ArrayList<>();
        list.add("#006fc4");
        list.add("#daa048");
        list.add("#398d41");
        list.add("0c3c72");
        list.add("#829db5");

        binding.recyclerColor.setAdapter(new ColorAdapter(list));
        binding.recyclerColor.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
    }

    private void initSize() {
        ArrayList<String> list = new ArrayList<>();
        list.add("S");
        list.add("M");
        list.add("L");
        list.add("XL");
        list.add("XXL");
        binding.recyclerSize.setAdapter(new SizeAdapter(list));
        binding.recyclerSize.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
    }

    private void initBanners() {
        ArrayList<SliderItems> sliderItems = new ArrayList<>();
        for(int i = 0; i < object.getPicUrl().size(); i++ ){
            sliderItems.add(new SliderItems(object.getPicUrl().get(i)));
        }
        binding.viewpaperSlider.setAdapter(new SliderAdapter(sliderItems,binding.viewpaperSlider));
        binding.viewpaperSlider.setClipToPadding(false);
        binding.viewpaperSlider.setClipChildren(false);
        binding.viewpaperSlider.setOffscreenPageLimit(3);
        binding.viewpaperSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
    }

    private void getBundles() {
        object= (ItemsDomain) getIntent().getSerializableExtra("object");
        binding.titleTxt.setText(object.getTitle());
        binding.pricetxt.setText("$"+object.getPrice());
        binding.ratingBar.setRating((float) object.getRating());
        binding.ratingtxt.setText(object.getRating()+" Rating");
        binding.descriptionTxt.setText(object.getDescription());

        binding.AddtoCartBtn.setOnClickListener(v -> {
            object.setNumberinCart(numberOrder);
            managmentCart.insertItem(object);
        });

        binding.backBtn.setOnClickListener(v -> finish());
    }
}