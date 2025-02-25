package com.g04.autochefmobile.activity;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.g04.autochefmobile.R;
import com.g04.autochefmobile.adapter.IngredientAdapter;
import com.g04.autochefmobile.model.IngredientWithQuantity;
import com.g04.autochefmobile.model.ShoppingList;
import com.g04.autochefmobile.utils.AlertDialogGenerator;
import com.g04.autochefmobile.utils.AlertType;
import com.g04.autochefmobile.utils.JsonReader;
import com.g04.autochefmobile.componentView.DisplayableElement;
import com.g04.autochefmobile.componentView.IngredientTypeView;
import com.g04.autochefmobile.componentView.IngredientView;
import com.g04.autochefmobile.utils.exceptions.LoadingException;

import org.json.JSONException;

import java.io.IOException;
import java.util.Vector;

/**
 * Activity that will display the list of ingredients from a shopping list
 */
public class ShowIngredientsActivity extends AppCompatActivity {
    private final Vector<DisplayableElement> ingredientVector = new Vector<>();
    private String shoppingListName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ingredients);

        try {
            getIntentData();
        } catch (LoadingException e) {
            AlertDialogGenerator.makeAlertDialog(this, ShoppingListActivity.class, AlertType.ERROR, e.getMessage());
        }

        // Load the registered data from the list (the ingredients that were checked)
        loadPreferences();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewIngredient);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        IngredientAdapter ingredientAdapter = new IngredientAdapter(ingredientVector); // creation of the ingredientAdapter for the display of the items
        recyclerView.setAdapter(ingredientAdapter);
    }

    /**
     * Load the appropriate shopping list from the json file
     */
    public void getIntentData() throws LoadingException {
        final String extraString = "shopping_list_name";
        if (getIntent().hasExtra(extraString)){
            shoppingListName = getIntent().getStringExtra(extraString);
            JsonReader jsonReader = new JsonReader();
            ShoppingList shoppingList;
            try{
                shoppingList = jsonReader.readJson(getApplicationContext(),shoppingListName);
            } catch (JSONException | IOException e) {
                throw new LoadingException(e.getCause());
            }
            if (shoppingList != null){
                // Load data if no exception occurred
                loadIngredientsList(shoppingList);
            }
        }

    }

    /**
     * Get the ingredients of the shopping list and will add it to a vector
     * @param shoppingList the shopping list we want to display
     */
    public void loadIngredientsList(ShoppingList shoppingList){
        Vector<IngredientWithQuantity> ingredientWithQuantityVector = new Vector<>(shoppingList.getIngredients());
        String category = "";
        for (IngredientWithQuantity ingredientWithQuantity: ingredientWithQuantityVector){
            if (category.equals("") || !ingredientWithQuantity.getType().equals(category)){
                category = ingredientWithQuantity.getType();
                ingredientVector.add(new IngredientTypeView(category));
            }
            String infoingredient = ingredientWithQuantity.getQuantity() + " " + ingredientWithQuantity.getUnit();
            if (ingredientWithQuantity.getQuantity() > 1) { infoingredient += 's';}
            ingredientVector.add(new IngredientView(infoingredient, ingredientWithQuantity.getName(), false));
        }
    }

    /**
     * Save all the states of the ingredients, whether there are checked or not
     */
    private void savePreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences(shoppingListName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (DisplayableElement e: ingredientVector){
            editor.putBoolean(e.getName(), e.isSelected());
        }
        editor.apply();
    }

    /**
     * Load the states of the ingredients
     */
    private void loadPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences(shoppingListName, MODE_PRIVATE);
        String SLName = sharedPreferences.getString("shopping_list_name", "no_name");
        for (DisplayableElement e: ingredientVector){
            boolean state = sharedPreferences.getBoolean(e.getName(), false);
            e.setSelected(state);
        }
    }

    /**
     * Save the states of the ingredients when hitting the back button
     */
    @Override
    public void onBackPressed(){
        savePreferences();
        super.onBackPressed();
    }

    /**
     * Save the states of the ingredients when closing the activity
     */
    @Override
    protected void onStop () {
        savePreferences();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

}
