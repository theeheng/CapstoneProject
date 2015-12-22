package com.hengtan.nanodegreeapp.stocount;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class ImageChooserDialogFragment extends DialogFragment {

    private ImageChooserDialogFragmentCallBack mCallback;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.image_chooser_title));
        builder.setItems(R.array.image_chooser_option, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int itemIndex) {

                if(mCallback != null) {
                    if (itemIndex == 0) //camera
                    {
                        mCallback.CallTakePicture();
                    }
                    else if (itemIndex == 1) //gallery
                    {
                        mCallback.CallChooseImage();
                    }
                }
            }
        });

        return builder.create();

    }

    public void setImageChooserDialogFragmentCallBack(ImageChooserDialogFragmentCallBack callback)
    {
        this.mCallback = callback;
    }
}
