package com.example.camera_peshin.presentations;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.camera_peshin.MainActivity;
import com.example.camera_peshin.ProductFragment;
import com.example.camera_peshin.R;
import com.example.uicomponents.button.BthBig;
import com.example.uicomponents.button.BthCustom;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class BottomSheetHepler {
    private static final String TAG = "BOTTOM_SHEET";
    public BottomSheetDialog dialog;

    public BottomSheetHepler(Context context) {
        try {
            dialog = new BottomSheetDialog(context);
            View view = LayoutInflater.from(context).inflate(R.layout.bs_select_image, null);
            BthBig bthGallery = view.findViewById(R.id.bthGallery);
            BthBig bthCamera = view.findViewById(R.id.bthCamera);

            bthGallery.init("🖼️ Выбрать из галереи", BthCustom.TypeButton.SECONDARY);
            bthCamera.init("📸 Сфотографировать", BthCustom.TypeButton.SECONDARY);

            bthGallery.Bth.setOnClickListener(v -> {
                if (MainActivity.init != null && MainActivity.init.openFragment instanceof ProductFragment) {
                    ((ProductFragment) MainActivity.init.openFragment).OpenGallery();
                }
                dialog.dismiss();
            });

            bthCamera.Bth.setOnClickListener(v -> {
                if (MainActivity.init != null && MainActivity.init.openFragment instanceof ProductFragment) {
                    ((ProductFragment) MainActivity.init.openFragment).OpenCamera();
                }
                dialog.dismiss();
            });

            dialog.setContentView(view);
        } catch (Exception e) {
            Log.e(TAG, "Error creating BottomSheetHepler", e);
        }
    }
}