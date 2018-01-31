package com.desertstar.noropefisher;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

/**
 * Created by DEV-1 on 1/17/2018.
 */


public class AcknowledgeDialogFragment extends DialogFragment {

    static final String dialog_text = "Deployment saved";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Deployment Information Saved")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                });


        // Create the AlertDialog object and return it
        return builder.create();
    }
}
