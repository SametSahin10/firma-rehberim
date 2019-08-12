package net.dijitalbeyin.firma_rehberim.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import net.dijitalbeyin.firma_rehberim.C0662R;
import net.dijitalbeyin.firma_rehberim.Category;

public class CategoryAdapter extends ArrayAdapter<Object> {
    ArrayList<Object> categories;
    Context context;
    int layoutResourceId;

    private class CategoryHolder {
        TextView tv_category_name;

        private CategoryHolder() {
        }
    }

    public CategoryAdapter(Context context2, int i, ArrayList<Object> arrayList) {
        super(context2, i, arrayList);
        this.context = context2;
        this.layoutResourceId = i;
        this.categories = arrayList;
    }

    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        return getCustomView(i, view, viewGroup);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        return getCustomView(i, view, viewGroup);
    }

    public View getCustomView(int i, View view, ViewGroup viewGroup) {
        CategoryHolder categoryHolder;
        if (view == null) {
            view = LayoutInflater.from(this.context).inflate(C0662R.layout.item_category, viewGroup, false);
            categoryHolder = new CategoryHolder();
            categoryHolder.tv_category_name = (TextView) view.findViewById(C0662R.C0664id.tv_category_name);
            view.setTag(categoryHolder);
        } else {
            categoryHolder = (CategoryHolder) view.getTag();
        }
        categoryHolder.tv_category_name.setText(((Category) this.categories.get(i)).getCategoryName());
        return view;
    }
}
