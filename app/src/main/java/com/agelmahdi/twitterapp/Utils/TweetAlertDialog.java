package com.agelmahdi.twitterapp.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.agelmahdi.twitterapp.R;


/**
 * Created by Ahmed El-Mahdi on 1/19/2018.
 */

public class TweetAlertDialog {
    public void showAlertDialog(Context context, String message) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false)
                .setNegativeButton(R.string.canel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
}

