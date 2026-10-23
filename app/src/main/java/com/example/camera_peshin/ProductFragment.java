package com.example.camera_peshin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.camera_peshin.presentations.BottomSheetHepler;
import com.example.network.datas.products.ProductCreate;
import com.example.network.domains.callbacks.MyResponseCallback;
import com.example.network.domains.models.Product;
import com.example.uicomponents.button.BthBig;
import com.example.uicomponents.button.BthCustom;
import com.example.uicomponents.et.EtCustom;

import java.io.File;
import java.util.regex.Pattern;

public class ProductFragment extends Fragment {

    private static final String TAG = "PRODUCT_FRAGMENT";

    EtCustom etName, etDescription, etExpenditure, etPrice;
    Spinner sCategory;
    BthBig bthCreate;
    View bthImageSelect;
    BottomSheetHepler bottomSheetHepler;
    String currentPhotoPath;
    Uri imageUri;

    public ProductFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        Context ctx = requireContext();

        etName = view.findViewById(R.id.etName);
        etDescription = view.findViewById(R.id.etDescription);
        etExpenditure = view.findViewById(R.id.etExpenditure);
        etPrice = view.findViewById(R.id.etPrice);
        sCategory = view.findViewById(R.id.sCategory);
        bthCreate = view.findViewById(R.id.bthCreate);
        bthImageSelect = view.findViewById(R.id.bthImageSelect);

        try {
            bottomSheetHepler = new BottomSheetHepler(ctx);
        } catch (Exception e) {
            Log.e(TAG, "Error creating BottomSheetHepler", e);
        }

        etName.init("Название", "Введите название", EtCustom.TypeET.DEFAULT);
        etDescription.init("Описание", "Введите описание", EtCustom.TypeET.DEFAULT);
        etExpenditure.init("Расход", "Введите расход", EtCustom.TypeET.DEFAULT);
        etPrice.init("Цена", "Введите цену", EtCustom.TypeET.DEFAULT);

        bthCreate.init("Подтвердить", BthCustom.TypeButton.PRIMARY);
        bthCreate.setEnabled(false);

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bthCreate.setEnabled(isFormValid());
            }
        };
        if (etName.Et != null) etName.Et.addTextChangedListener(watcher);
        if (etDescription.Et != null) etDescription.Et.addTextChangedListener(watcher);
        if (etExpenditure.Et != null) etExpenditure.Et.addTextChangedListener(watcher);
        if (etPrice.Et != null) etPrice.Et.addTextChangedListener(watcher);

        bthImageSelect.setOnClickListener(v -> {
            try {
                if (bottomSheetHepler != null && bottomSheetHepler.dialog != null) {
                    bottomSheetHepler.dialog.show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error showing bottom sheet", e);
            }
        });

        if (bthCreate.Bth != null) {
            bthCreate.Bth.setOnClickListener(v -> {
                if (!isFormValid()) return;
                if (imageUri == null) {
                    Toast.makeText(ctx, "Выберите изображение", Toast.LENGTH_SHORT).show();
                    return;
                }
                int price;
                try {
                    price = Integer.parseInt(etPrice.Et.getText().toString().trim());
                } catch (NumberFormatException e) {
                    etPrice.setError("Некорректная цена");
                    return;
                }

                Product product = new Product(
                        etName.Et.getText().toString().trim(),
                        etDescription.Et.getText().toString().trim(),
                        sCategory.getSelectedItemPosition(),
                        etExpenditure.Et.getText().toString().trim(),
                        price
                );

                ProductCreate RequestPC = new ProductCreate(
                        ctx,
                        MainActivity.TOKEN,
                        product,
                        imageUri,
                        new MyResponseCallback() {
                            @Override
                            public void onCompile(String result) {
                                if (getActivity() == null) return;
                                getActivity().runOnUiThread(() -> {
                                    Toast.makeText(ctx, "Продукт создан!", Toast.LENGTH_SHORT).show();
                                    if (MainActivity.init != null) {
                                        MainActivity.init.MenuItemSelect.onTabClick(2);
                                    }
                                });
                            }
                            @Override
                            public void onError(String error) {
                                Log.e("PRODUCT CREATE", "onError: " + error);
                                if (getActivity() == null) return;
                                getActivity().runOnUiThread(() ->
                                        Toast.makeText(ctx, "Ошибка: " + error, Toast.LENGTH_LONG).show()
                                );
                            }
                        }
                );
                RequestPC.execute();
            });
        }

        return view;
    }

    private boolean isFormValid() {
        if (etName.Et == null || etName.Et.getText().toString().trim().isEmpty()) return false;
        if (etDescription.Et == null || etDescription.Et.getText().toString().trim().isEmpty()) return false;
        if (etExpenditure.Et == null || etExpenditure.Et.getText().toString().trim().isEmpty()) return false;
        String price = etPrice.Et == null ? "" : etPrice.Et.getText().toString().trim();
        if (price.isEmpty()) return false;
        if (!Pattern.matches("\\d+", price)) return false;
        return true;
    }

    public void OpenGallery() {
        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Выберите изображение"), 1);
        } catch (Exception e) {
            Log.e(TAG, "Error opening gallery", e);
        }
    }

    public void OpenCamera() {
        try {
            Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = File.createTempFile(
                    "MY_PHOTO_CADR", ".jpg",
                    requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            currentPhotoPath = photoFile.getAbsolutePath();
            imageUri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".provider",
                    photoFile);
            pictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            pictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(pictureIntent, 2);
        } catch (Exception e) {
            Log.e(TAG, "Error opening camera", e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (bottomSheetHepler != null && bottomSheetHepler.dialog != null) {
            bottomSheetHepler.dialog.cancel();
        }
        if (resultCode == -1) {
            if (requestCode == 1 && data != null && data.getData() != null) {
                imageUri = data.getData();
            }
            if (imageUri != null && bthImageSelect instanceof ImageView) {
                ((ImageView) bthImageSelect).setImageURI(imageUri);
            }
        }
    }
}