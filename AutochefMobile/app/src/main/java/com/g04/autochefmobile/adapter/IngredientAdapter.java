package com.g04.autochefmobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.g04.autochefmobile.R;
import com.g04.autochefmobile.componentView.DisplayableElement;

import java.util.Vector;

/**
 * Adapter for controlling the display of the ingredients (category or ingredient with checkbox)
 */
public final class IngredientAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Vector<DisplayableElement> ingredientVector;

    public IngredientAdapter(Vector<DisplayableElement> ingredientVector){
        this.ingredientVector = ingredientVector;
    }

    /**
     * @param parent the ViewGroup parent
     * @param viewType the type of element we want to display
     * {@return the corresponding ViewHolder depending on the element}
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        RecyclerView.ViewHolder myViewHolder = null;
        switch (viewType){
            case 0:
                view = inflater.inflate(R.layout.header_type_ingredient, parent, false);
                myViewHolder = new CategoryViewHolder(view);
                break;
            case 1:
                view = inflater.inflate(R.layout.content_show_ingredients, parent, false);
                myViewHolder = new IngredientViewHolder(view);
                break;
        }
        return myViewHolder;
    }

    /**
     * @param holder the holder we want to update
     * @param position position corresponding to the element we want to link to the holder
     * Set the content of the holder whether it is a category or an ingredient
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final DisplayableElement item = ingredientVector.get(position);
        int type = item.getType();
        switch(type){
            case 0:
                CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
                categoryViewHolder.textView.setText(item.getName());
                break;
            case 1:
                IngredientViewHolder myviewholder = (IngredientViewHolder) holder;
                myviewholder.checkBox.setText(item.getName());
                myviewholder.textView.setText(item.getInfo());
                myviewholder.checkBox.setChecked(item.isSelected());
                myviewholder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
                    myviewholder.checkBox.setSelected(b);
                    ingredientVector.get(holder.getAdapterPosition()).setSelected(b);
                });
                break;
        }
    }

    /**
     * Calculates the viewType in the method onCreateViewHolder
     * @param position position of the element in the recyclerView
     * @return the type of element
     */
    @Override
    public int getItemViewType(int position) {
        return ingredientVector.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return ingredientVector.size();
    }

    /**
     * ViewHolder for displaying an ingredient with a checkbox
     */
    private final class IngredientViewHolder extends RecyclerView.ViewHolder{
        final CheckBox checkBox;
        final TextView textView;

        private IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox_ingredient);
            textView = itemView.findViewById(R.id.info_ingredient);
        }
    }

    /**
     * ViewHolder for displaying the category of an ingredient
     */
    private final class CategoryViewHolder extends RecyclerView.ViewHolder{
        final TextView textView;

        private CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_view_ingredient);
        }
    }
}
