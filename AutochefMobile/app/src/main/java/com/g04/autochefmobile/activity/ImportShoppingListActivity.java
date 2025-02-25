package com.g04.autochefmobile.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScannerView;
import com.g04.autochefmobile.R;

import com.budiyev.android.codescanner.CodeScanner;
import com.g04.autochefmobile.model.ShoppingList;
import com.g04.autochefmobile.utils.AlertDialogGenerator;
import com.g04.autochefmobile.utils.AlertType;
import com.g04.autochefmobile.utils.JsonReader;

import org.json.JSONException;

import java.io.IOException;

/**
 * Activity for importing shopping lists by scanning a QR code
 */
public class ImportShoppingListActivity extends AppCompatActivity {

    private CodeScanner cameraScanner;
    private final JsonReader jsonReader = new JsonReader();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_shopping_list);

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        cameraScanner = new CodeScanner(this, scannerView);

        setupPermission(this);

        cameraScanner.setDecodeCallback(result -> runOnUiThread(() -> {
            // Action when scanning some QR code
            try {
                ShoppingList shoppingList = jsonReader.createAndReadJson(getApplicationContext(), result.getText());
                if (shoppingList == null)
                    throw new JSONException("Unable to read the JSON file");
                showAlertSuccess("La liste de courses " + shoppingList.getName() + " a bien été importé. Vous allez être redirigé vers l'écran d'accueil");
            } catch (IOException | JSONException e) {
                showAlertError();
            }
        }));

        scannerView.setOnClickListener(view -> cameraScanner.startPreview());
    }

    private void showAlertSuccess(final String message){
        AlertDialogGenerator.makeAlertDialog(ImportShoppingListActivity.this, ShoppingListActivity.class, AlertType.INFO, message);
    }

    private void showAlertError(){
        final String message = "Impossible de charger cette liste de course !";
        AlertDialogGenerator.makeAlertDialog(ImportShoppingListActivity.this, ShoppingListActivity.class, AlertType.ERROR, message);
    }

    /**
     * Setup the camera permission for using the QR scanner
     * @param activity the activity that want the permission
     */
    private void setupPermission(Activity activity) {
        if(PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)){
            final int CAMERA_REQUEST_CODE = 101;
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraScanner.startPreview();
    }

    @Override
    protected void onPause() {
        cameraScanner.releaseResources();
        super.onPause();
    }

    @Override
    protected void onStop () {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}