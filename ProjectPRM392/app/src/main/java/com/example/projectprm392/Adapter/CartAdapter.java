package com.example.projectprm392.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.projectprm392.Domain.ItemsDomain;
import com.example.projectprm392.Helper.ChangeNumberItemsListener;
import com.example.projectprm392.Helper.ManagmentCart;
import com.example.projectprm392.R;
import com.example.projectprm392.databinding.ViewholderCartBinding;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private ArrayList<ItemsDomain> listItemSelected;
    private ChangeNumberItemsListener changeNumberItemsListener;
    private ManagmentCart managmentCart;

    public CartAdapter(ArrayList<ItemsDomain> listItemSelected, ChangeNumberItemsListener changeNumberItemsListener, Context context) {
        this.listItemSelected = listItemSelected;
        this.changeNumberItemsListener = changeNumberItemsListener;
        managmentCart = new ManagmentCart(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderCartBinding binding = ViewholderCartBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemsDomain currentItem = listItemSelected.get(position);

        holder.binding.title.setText(currentItem.getTitle());
        holder.binding.feeEachItem.setText("$" + currentItem.getPrice());
        holder.binding.totalEachItem.setText("$" + currentItem.getPrice());

        // Load image using Glide with a check for URL
        if (currentItem.getPicUrl() != null && !currentItem.getPicUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(currentItem.getPicUrl().get(0))
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.binding.pic);
        }

        // Update the number TextView based on the current item count
        holder.binding.number.setText(String.valueOf(currentItem.getNumberinCart())); // Ensure you have a getQuantity method in ItemsDomain

        holder.binding.plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    managmentCart.plusItem(listItemSelected, currentPosition, new ChangeNumberItemsListener() {
                        @Override
                        public void changed() {
                            // Notify only the item that changed
                            holder.binding.number.setText(String.valueOf(currentItem.getNumberinCart())); // Update the displayed quantity
                            notifyItemChanged(currentPosition);
                            changeNumberItemsListener.changed();
                        }
                    });
                }
            }
        });

        holder.binding.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    managmentCart.minusItem(listItemSelected, currentPosition, new ChangeNumberItemsListener() {
                        @Override
                        public void changed() {
                            // Update displayed quantity after change
                            holder.binding.number.setText(String.valueOf(currentItem.getNumberinCart())); // Update the displayed quantity
                            notifyItemChanged(currentPosition);
                            changeNumberItemsListener.changed();
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItemSelected.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderCartBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = ViewholderCartBinding.bind(itemView);
        }
    }
}
