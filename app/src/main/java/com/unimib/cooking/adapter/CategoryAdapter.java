package com.unimib.cooking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unimib.cooking.R;
import com.unimib.cooking.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categoryList;
    private OnItemClickListener listener;

    // Costruttore con il listener per il click
    public CategoryAdapter(Context context, List<Category> categoryList, OnItemClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryName.setText(category.getName());
        holder.categoryImage.setImageResource(category.getFlagResourceId());

        // Gestione del click su ogni item
        holder.itemView.setOnClickListener(v -> listener.onItemClick(category));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        ImageView categoryImage;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.item_text);
            categoryImage = itemView.findViewById(R.id.item_icon);
        }
    }

    // Interfaccia per il click listener
    public interface OnItemClickListener {
        void onItemClick(Category category);
    }
}
