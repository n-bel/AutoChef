package com.g04.autochefmobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.g04.autochefmobile.R;
import com.g04.autochefmobile.model.ShoppingListName;
import com.g04.autochefmobile.activity.ShowIngredientsActivity;

import java.util.Vector;

/**
 * Adaptater for controlling the display of the shopping lists
 */
public final class ShoppingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final Vector<ShoppingListName> shoppingListVector;
    private final onSelectItemListener listener;
    private Boolean selectMode = false;

    public ShoppingListAdapter(Context context, Vector<ShoppingListName> shoppingListVector, onSelectItemListener listener){
        this.context = context;
        this.shoppingListVector = shoppingListVector;
        this.listener = listener;
    }

    /**
     * @param parent the ViewGroup parent
     * @param viewType the type of element we want to display
     * {@return the ShoppingListNameViewHolder}
     */
    @NonNull
    @Override
    public ShoppingListNameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_show_shopping_list, parent, false);
        return new ShoppingListNameViewHolder(view);
    }

    /**
     * @param holder
     * @param position
     * Set the content of the ViewHolder
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ShoppingListNameViewHolder shoppingListNameViewHolder = (ShoppingListNameViewHolder) holder;
        int pos = shoppingListNameViewHolder.getAdapterPosition();
        shoppingListNameViewHolder.shopping_list_tv.setText(String.valueOf(shoppingListVector.get(pos).getName()));
        shoppingListNameViewHolder.shopping_list_tv.setOnClickListener((view) -> {
            if (!shoppingListVector.get(pos).isSelected() && !selectMode){
                Intent intent = new Intent(context, ShowIngredientsActivity.class);
                intent.putExtra("shopping_list_name", shoppingListVector.get(pos).getName());
                context.startActivity(intent);
            }else{
                changeShoppingListState(pos, shoppingListNameViewHolder);
            }
        });
        shoppingListNameViewHolder.shopping_list_tv.setOnLongClickListener(view -> {
            changeShoppingListState(pos, shoppingListNameViewHolder);
            return true;
        });
        shoppingListNameViewHolder.check_img.setOnClickListener(view -> changeShoppingListState(pos, shoppingListNameViewHolder));
    }

    /**
     * @param pos the position of the shopping list element
     * @param shoppingListNameViewHolder the vector of all the shopping lists names
     * This method will check or uncheck a shopping list and will also make visible the check image
     */
    private void changeShoppingListState(int pos, ShoppingListNameViewHolder shoppingListNameViewHolder) {
        ShoppingListName shoppingListName = shoppingListVector.get(pos);
        if (shoppingListName.isSelected()){
            shoppingListName.setSelected(false);
            shoppingListNameViewHolder.check_img.setVisibility(View.GONE);
        }else{
            shoppingListName.setSelected(true);
            shoppingListNameViewHolder.check_img.setVisibility(View.VISIBLE);
        }
        selectMode = listener.onSelectItem(); // call to the activity for making visible (or not) the delete image
    }

    /**
     * @param childAt the corresponding ViewHolder
     * this method will remove the check image from a shopping list
     */
    public void resetSelection(RecyclerView.ViewHolder childAt) {
        ShoppingListNameViewHolder shoppingListNameViewHolder = (ShoppingListNameViewHolder) childAt;
        shoppingListNameViewHolder.check_img.setVisibility(View.GONE);
    }

    /**
     * this method will remove the selection mode (will be called when we delete a shopping list, it will
     * reset the selection state)
     */
    public void resetSelectMode() {
        this.selectMode = false;
    }

    @Override
    public int getItemCount() {
        return shoppingListVector.size();
    }

    /**
     * Interface for the communication with the ShoppingListActivity
     */
    public interface onSelectItemListener {
        boolean onSelectItem();
    }

    /**
     * ViewHolder class for displaying each item of the RecyclerView
     */
    private final class ShoppingListNameViewHolder extends RecyclerView.ViewHolder {

        final TextView shopping_list_tv;
        final ImageView check_img;

        private ShoppingListNameViewHolder(@NonNull View itemView) {
            super(itemView);
            shopping_list_tv = itemView.findViewById(R.id.shopping_list_name_tv);
            check_img = itemView.findViewById(R.id.check_img);
        }
    }
}
