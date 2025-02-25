package com.g04.autochefmobile.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

public class AlertDialogGenerator {

    /**
     * Makes and displays popup alert dialog message
     *
     * @param context Context alert is made from (ex: ImportShoppingListActivity.this)
     * @param cls The component class that is to be used for the intent (ie: Class that will be run after alert)
     * @param type AlertType with title
     * @param message Alert Dialog message shown to user
     */
    public static void makeAlertDialog(final Context context, Class<?> cls, final AlertType type, final String message){
        final AlertDialog alert = new AlertDialog.Builder(context).
                setTitle(type.getTitle()).setMessage(message).setPositiveButton("OK", (dialogInterface, i) -> {
                    ((Activity) context).finish();
                }).create();
        alert.show();
    }
}
