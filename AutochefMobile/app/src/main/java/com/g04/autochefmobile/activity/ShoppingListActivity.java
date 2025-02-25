package com.g04.autochefmobile.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.g04.autochefmobile.R;
import com.g04.autochefmobile.adapter.ShoppingListAdapter;
import com.g04.autochefmobile.model.ShoppingListName;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Vector;

/**
 * Main Activity that will display all the shopping lists
 */
public class ShoppingListActivity extends AppCompatActivity implements ShoppingListAdapter.onSelectItemListener {

    private RecyclerView recyclerView;
    private ImageView delete_img;

    private ShoppingListAdapter shoppingListAdapter;
    private final Vector<ShoppingListName> shoppingListNamesVector = new Vector<>();

    /**
     * @param savedInstanceState the saved instance
     * this method will create the main layout of the page
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton addShoppingListButton = findViewById(R.id.floatingActionButton);
        addShoppingListButton.setOnClickListener(view -> {
            Intent intent = new Intent(ShoppingListActivity.this, ImportShoppingListActivity.class);
            startActivity(intent);
        });
        delete_img = findViewById(R.id.image_delete);
        delete_img.setOnClickListener(view -> deleteShoppingList());

        shoppingListAdapter = new ShoppingListAdapter(ShoppingListActivity.this, shoppingListNamesVector, this);

        recyclerView.setAdapter(shoppingListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ShoppingListActivity.this));
    }

    /**
     * This method will delete the shopping list as well as its json file associated
     */
    private void deleteShoppingList() {
        int sizeOfShoppingLisVector = shoppingListNamesVector.size();
        int i=0;
        while (i<sizeOfShoppingLisVector){
            ShoppingListName s = shoppingListNamesVector.get(i);
            if (s.isSelected()){
                String shoppingListToDelete = s.getName();
                File file = new File(getFilesDir(),shoppingListToDelete+".json");
                file.delete();
                removePreferences(shoppingListToDelete); // remove also its shared preferences
                shoppingListNamesVector.remove(s);
                i--;
                sizeOfShoppingLisVector = shoppingListNamesVector.size();
            }
            i++;
        }
        for (int h = 0; h<recyclerView.getChildCount(); h++){
            shoppingListAdapter.resetSelection(recyclerView.findViewHolderForAdapterPosition(h));
        }

        delete_img.setVisibility(View.GONE);
        shoppingListAdapter.resetSelectMode();
        shoppingListAdapter.notifyDataSetChanged();
    }

    public void loadShoppingListsNames(){
        String[] jsonNamesVector = lsAllJSONFiles();

        boolean flag;
        for (String jsonName : jsonNamesVector){
            flag = true;
            ShoppingListName shoppingListName = new ShoppingListName(jsonName.replace(".json", ""));
            for (ShoppingListName s: shoppingListNamesVector){
                if (s.getName().equals(shoppingListName.getName())){
                    flag = false;
                    break;
                }
            }
            if (flag)
                shoppingListNamesVector.add(shoppingListName);
        }
    }

    /**
     * {@return array of all the json names of the shopping list}
     */
    private String[] lsAllJSONFiles() {
        final File f = new File(String.valueOf(this.getFileStreamPath("")));
        final FilenameFilter filter = (f1, name) -> name.endsWith(".json");
        final String[] pathnames = f.list(filter);
        return pathnames;
    }

    /**
     * @param shoppingListName the shopping list name that we want to remove
     * this method will remove the shared preference of the shopping list
     */
    private void removePreferences(String shoppingListName){
        getSharedPreferences(shoppingListName, MODE_PRIVATE).edit().clear().commit();
    }

    /**
     * Verify if there is a shopping list selected
     * if it's the case, it will enable the delete function
     *
     * @return true if there is at least one shopping list selected
     * otherwise false
     */
    @Override
    public boolean onSelectItem() {
        boolean flag = false;
        for (ShoppingListName s: shoppingListNamesVector){
            if (s.isSelected()){
                flag = true;
                break;
            }
        }
        if (flag){
            delete_img.setVisibility(View.VISIBLE);
        }else{
            delete_img.setVisibility(View.GONE);
        }
        return flag;
    }

    /**
     * Action of the lifecycle activity
     */
    // Empty methods (only calling super) left for easier readability for new android developers
    @Override
    protected void onStop () {
        super.onStop();
    }

    @Override
    protected void onPause () {
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        loadShoppingListsNames();
        shoppingListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
}