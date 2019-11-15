package com.firmarehberim.canliradyo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.firmarehberim.canliradyo.datamodel.Category;
import com.firmarehberim.canliradyo.R;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<Object> {
    Context context;
    int layoutResourceId;
    ArrayList<Object> categories;

    public CategoryAdapter(Context context, int resource, ArrayList<Object> categories) {
        super(context, resource, categories);
        this.context = context;
        this.layoutResourceId = resource;
        this.categories = categories;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CategoryHolder holder;
        if (row == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            row = layoutInflater.inflate(R.layout.item_category, parent, false);
            holder = new CategoryHolder();
            holder.tv_category_name = row.findViewById(R.id.tv_category_name);
            row.setTag(holder);
        } else {
            holder = (CategoryHolder) row.getTag();
        }
        Category currentCategory = (Category) categories.get(position);
        holder.tv_category_name.setText(currentCategory.getCategoryName());
        return row;
    }

    private class CategoryHolder {
        TextView tv_category_name;
    }
}