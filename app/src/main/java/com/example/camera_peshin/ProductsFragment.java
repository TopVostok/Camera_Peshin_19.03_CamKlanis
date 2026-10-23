package com.example.camera_peshin;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.network.datas.products.ProductGetUser;
import com.example.network.domains.callbacks.MyResponseCallback;
import com.example.network.domains.models.Product;
import com.example.uicomponents.button.BthBig;
import com.example.uicomponents.button.BthCustom;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class ProductsFragment extends Fragment {

    private static final String TAG = "PRODUCTS_FRAGMENT";

    View bthOpenAddProduct;
    LinearLayout llContent;
    List<Product> Products = new ArrayList<>();
    OnTabClickListner listener;

    public ProductsFragment() {
        super();
    }

    public void setListener(OnTabClickListner listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);
        Context ctx = requireContext();

        bthOpenAddProduct = view.findViewById(R.id.bthOpenAddProduct);
        llContent = view.findViewById(R.id.llProductsList);

        bthOpenAddProduct.setOnClickListener(v -> {
            try {
                if (listener != null) {
                    listener.onTabClick(-1);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error on plus click", e);
            }
        });

        ProductGetUser();
        return view;
    }

    public void ProductGetUser() {
        ProductGetUser RequestPGU = new ProductGetUser(
                MainActivity.TOKEN,
                new MyResponseCallback() {
                    @Override
                    public void onCompile(String result) {
                        if (!isAdded()) return;
                        try {
                            Products = new GsonBuilder().create().fromJson(
                                    result,
                                    new TypeToken<ArrayList<Product>>(){}.getType()
                            );
                            if (Products == null) Products = new ArrayList<>();
                            CreateElements();
                        } catch (Exception e) {
                            Log.e(TAG, "JSON parse error", e);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("PRODUCT GET USER", error);
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), "Ошибка: " + error, Toast.LENGTH_LONG).show());
                        }
                    }
                }
        );
        RequestPGU.execute();
    }

    public void CreateElements() {
        if (llContent == null || !isAdded()) return;
        llContent.removeAllViews();

        Context ctx = requireContext();
        for (Product product : Products) {
            try {
                View itemProduct = LayoutInflater.from(ctx)
                        .inflate(R.layout.card, llContent, false);

                BthBig bthOpen = itemProduct.findViewById(R.id.bthOpenProduct);
                TextView tvName = itemProduct.findViewById(R.id.tvName);
                TextView tvPrice = itemProduct.findViewById(R.id.tvPrice);

                bthOpen.init("Открыть", BthCustom.TypeButton.PRIMARY);
                bthOpen.Bth.setTextSize(16);
                bthOpen.Bth.setOnClickListener(v -> {
                    MainActivity.init.openContextMenu(itemProduct);
                });

                tvName.setText(product.name);
                tvPrice.setText(product.price + "Р");

                registerForContextMenu(itemProduct);
                llContent.addView(itemProduct);
            } catch (Exception e) {
                Log.e(TAG, "Error creating card", e);
            }
        }
    }
}