package com.example.camera_peshin;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NavigationMenu extends Fragment {

    private final Context context;
    private final OnTabClickListner listener;
    private RecyclerView recyclerView;

    public NavigationMenu(Context context, OnTabClickListner listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_menu, container, false);
        recyclerView = view.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new MenuAdapter());
        return view;
    }

    private class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
        private final String[] titles = {"Добавить", "Корзина", "Продукты"};
        private final int[] icons = {R.drawable.ic_plus, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground};

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.textView.setText(titles[position]);
            holder.imageView.setImageResource(icons[position]);

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    if (position == 0) listener.onTabClick(-1); // Добавить
                    else if (position == 1) listener.onTabClick(1); // Корзина (заглушка)
                    else if (position == 2) listener.onTabClick(2); // Мои продукты
                }
            });
        }

        @Override
        public int getItemCount() {
            return titles.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView textView;

            ViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);
                textView = itemView.findViewById(R.id.textView);
            }
        }
    }
}