package com.example.photouploader;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.rengwuxian.materialedittext.MaterialEditText;

public class editProfileDialog extends AppCompatDialogFragment {

    public MaterialEditText editName, editBio;
    public editProfileDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        editName = (MaterialEditText) view.findViewById(R.id.edit_name1);
        editBio = (MaterialEditText) view.findViewById(R.id.edit_bio1);

        builder.setView(view)
                .setTitle("Edit profile")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Save Changes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = editName.getText().toString();
                        Log.w("lol", name);
                        String bio = editBio.getText().toString();
                        Log.w("lol", bio);
                        listener.applyTexts(name,bio);
                    }
                });

        return builder.create();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (editProfileDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement editProfileDialogListener");
        }
    }

    public interface editProfileDialogListener {
        void applyTexts(String name, String bio);
    }
}
