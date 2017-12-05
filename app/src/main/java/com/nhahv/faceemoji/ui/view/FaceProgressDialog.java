package com.nhahv.faceemoji.ui.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.ProgressBar;

public class FaceProgressDialog extends ProgressDialog {
    public FaceProgressDialog(Context context) {
        super(context);
    }

    public FaceProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public void show() {
        super.show();
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        setContentView(new ProgressBar(getContext()));
    }
}