package net.dijitalbeyin.firma_rehberim.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.dijitalbeyin.firma_rehberim.Category;
import net.dijitalbeyin.firma_rehberim.R;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends ArrayAdapter<Category> {
    Context context;
    int layoutResourceId;
    ArrayList<Category> categories;

    public CategoryAdapter(Context context, int resource, ArrayList<Category> categories) {
        super(context, resource, categories);
        this.context = context;
        this.layoutResourceId = resource;
        this.categories = categories;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
        Category currentCategory = categories.get(position);
        holder.tv_category_name.setText(currentCategory.getCategoryName());
        return row;
    }

    private class CategoryHolder {
        TextView tv_category_name;
    }
}
