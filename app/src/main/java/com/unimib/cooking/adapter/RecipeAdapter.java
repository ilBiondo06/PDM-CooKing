package com.unimib.cooking.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.unimib.cooking.R;
import com.unimib.cooking.model.Recipe;
import com.bumptech.glide.Glide;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    public void setFilteredList(List<Recipe> filteredList) {
        this.recipeList = filteredList;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onRecipeItemClick(Recipe recipe);
        void onFavoriteButtonPressed(int position);
        void onRemoveFromFavorites(int position);
    }

    private int layout;
    private List<Recipe> recipeList;
    private boolean heartVisible;
    private Context context;
    private final OnItemClickListener onItemClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textViewMeal;
        private final TextView textViewCategory;
        private final TextView textViewArea;
        private final CheckBox favoriteCheckbox;
        private final ImageView imageView;
        private final Button menuButton;

        public ViewHolder(View view) {
            super(view);

            textViewMeal = view.findViewById(R.id.textViewMeal);
            textViewCategory = view.findViewById(R.id.textViewCategory);
            textViewArea = view.findViewById(R.id.textViewArea);
            favoriteCheckbox = view.findViewById(R.id.favoriteButton);
            imageView = view.findViewById(R.id.imageView);
            menuButton = view.findViewById(R.id.buttonMenu);

            favoriteCheckbox.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        public TextView getTextViewMeal() {
            return textViewMeal;
        }

        public TextView getTextViewCategory() {
            return textViewCategory;
        }

        public TextView getTextViewArea() {
            return textViewArea;
        }

        public CheckBox getFavoriteCheckbox() {
            return favoriteCheckbox;
        }

        public Button getMenuButton() {
            return menuButton;
        }

        public ImageView getImageView() {
            return imageView;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.favoriteButton) {
                onItemClickListener.onFavoriteButtonPressed(getAdapterPosition());
            } else {
                onItemClickListener.onRecipeItemClick(recipeList.get(getAdapterPosition()));
            }
        }
    }

    public RecipeAdapter(int layout, List<Recipe> recipeList, boolean heartVisible, OnItemClickListener onItemClickListener) {
        this.layout = layout;
        this.recipeList = recipeList;
        this.heartVisible = heartVisible;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(layout, viewGroup, false);

        if (this.context == null) this.context = viewGroup.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTextViewMeal().setText(recipeList.get(position).getStrMeal());
        viewHolder.getTextViewCategory().setText(recipeList.get(position).getStrCategory());
        viewHolder.getTextViewArea().setText(recipeList.get(position).getStrArea());
        viewHolder.getFavoriteCheckbox().setChecked(recipeList.get(position).getLiked());

        viewHolder.getMenuButton().setOnClickListener(view -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view.getContext());
            View bottomSheetView = LayoutInflater.from(view.getContext()).inflate(R.layout.bottom_sheet_card_recipe, null);
            bottomSheetDialog.setContentView(bottomSheetView);

            // Gestione del tasto "Condividi"
            bottomSheetView.findViewById(R.id.shareButton).setOnClickListener(view1 -> {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Check this recipe: " + recipeList.get(position).getStrSource());
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, "");
                context.startActivity(shareIntent);

                bottomSheetDialog.dismiss();
            });

            // Gestione del tasto "Rimuovi dai Preferiti"
            Button deleteButton = bottomSheetView.findViewById(R.id.deleteFavoritesButton);
            if (recipeList.get(position).getLiked()) {
                deleteButton.setVisibility(View.VISIBLE);
                deleteButton.setOnClickListener(v -> {
                    onItemClickListener.onRemoveFromFavorites(position); // Chiama il metodo dell'interfaccia
                    bottomSheetDialog.dismiss();
                });
            } else {
                deleteButton.setVisibility(View.GONE);
            }

            bottomSheetDialog.show();
        });

        Glide.with(context)
                .load(recipeList.get(position).getStrMealThumb())
                .placeholder(new ColorDrawable(context.getColor(R.color.md_theme_onSurfaceVariant_mediumContrast)))
                .into(viewHolder.getImageView());

        if (!heartVisible) {
            viewHolder.getFavoriteCheckbox().setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public void updateData(List<Recipe> newDataList) {
        this.recipeList = newDataList;
        notifyDataSetChanged();
    }
}