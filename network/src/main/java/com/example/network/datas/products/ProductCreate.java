package com.example.network.datas.products;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.network.domains.apis.MyAsyncTask;
import com.example.network.domains.callbacks.MyResponseCallback;
import com.example.network.domains.common.Settings;
import com.example.network.domains.models.Product;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ProductCreate extends MyAsyncTask {
    private static final String TAG = "PRODUCT_CREATE";

    Context context;
    String token;
    Product product;
    Uri uri;

    public ProductCreate(Context context, String token, Product product, Uri uri, MyResponseCallback callback) {
        super(callback);
        this.context = context;
        this.token = token;
        this.product = product;
        this.uri = uri;
    }

    @Override
    protected String doInBackground(Void... voids) {
        if (uri == null) {
            Log.e(TAG, "uri is null");
            return "Error: imageUri is null";
        }

        File tempFile = null;
        try {
            // 1. Читаем картинку
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                return "Error: cannot open image stream";
            }

            tempFile = createTempFileFromStream(inputStream);
            if (tempFile == null) {
                return "Error: cannot create temp file";
            }

            // 2. Параметры. ВАЖНО: ключи должны совпадать с тем, что ждет сервер (в Swagger смотри)
            Map<String, String> params = new HashMap<>();
            params.put("Name", product.name);
            params.put("Description", product.description);
            params.put("Gender", String.valueOf(product.gender));
            params.put("Expenditure", product.expenditure);
            params.put("Price", String.valueOf(product.price));

            // 3. Запрос
            Log.d(TAG, "Sending request to " + Settings.URL + "/api/product/create");
            Connection.Response response = Jsoup.connect(Settings.URL + "/api/product/create")
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .method(Connection.Method.POST)
                    .header("token", token)
                    // НЕ СТАВИМ Content-Type вручную! JSoup сам добавит boundary
                    .data(params)
                    .data("ImageFile", tempFile.getName(), new FileInputStream(tempFile))
                    .execute();

            Log.d(TAG, "Response code: " + response.statusCode());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                return "Error code " + response.statusCode() + ": " + response.body();
            }

        } catch (Exception e) {
            // Ловим ВСЁ, чтобы приложение не вылетало
            Log.e(TAG, "Exception in ProductCreate", e);
            return "Error: " + e.toString();
        } finally {
            // Удаляем временный файл, чтобы не забивать память
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    private File createTempFileFromStream(InputStream inputStream) {
        try {
            File tempFile = File.createTempFile("upload_", ".jpg", context.getCacheDir());
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            inputStream.close();
            return tempFile;
        } catch (IOException e) {
            Log.e(TAG, "Failed to create temp file", e);
            return null;
        }
    }
}