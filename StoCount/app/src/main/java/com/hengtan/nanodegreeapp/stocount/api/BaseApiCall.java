package com.hengtan.nanodegreeapp.stocount.api;

import android.content.Context;
import android.widget.Toast;

public class BaseApiCall {

    protected Toast errorToast;

    protected void DisplayToast(Context ctx, String message)
    {
        if(errorToast == null)
        {
            errorToast = Toast.makeText(ctx, message, Toast.LENGTH_SHORT);
            errorToast.show();
        }
        else if (errorToast.getView().isShown())     // true if visible
        {
            errorToast.setText(message);
        }
        else
        {
            errorToast.setText(message);
            errorToast.show();
        }
    }
}
