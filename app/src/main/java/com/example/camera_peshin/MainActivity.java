package com.example.camera_peshin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.camera_peshin.domains.callbacks.OnTabClickListner;

public class MainActivity extends AppCompatActivity {

    // Твой токен из Swagger
    public static String TOKEN = "d8f8811a-2a30-4eb4-875c-ed8783d4657f";
    public static MainActivity init;
    public Fragment openFragment;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init = this;
        context = this;

        // Сразу открываем экран создания продукта
        openFragment = new ProductFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, openFragment)
                .commit();
    }

    public OnTabClickListner MenuItemSelect = new OnTabClickListner() {
        @Override
        public void onTabClick(Integer position) {
            try {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                if (openFragment != null) {
                    ft.remove(openFragment);
                }
                if (position == -1) {
                    openFragment = new ProductFragment();
                    ft.add(R.id.content, openFragment);
                } else if (position == 2) {
                    ProductsFragment pf = new ProductsFragment();
                    pf.setListener(MenuItemSelect);
                    openFragment = pf;
                    ft.add(R.id.content, openFragment);
                }
                ft.commit();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Ошибка перехода: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (openFragment instanceof ProductFragment) {
            ((ProductFragment) openFragment).onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        menu.add(1, 101, Menu.NONE, "Изменить");
        menu.add(2, 102, Menu.NONE, "Удалить");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() == 1)
            Toast.makeText(this, "Изменение элемента", Toast.LENGTH_SHORT).show();
        else if (item.getGroupId() == 2)
            Toast.makeText(this, "Удаление элемента", Toast.LENGTH_SHORT).show();
        return true;
    }
}