package net.dijitalbeyin.firma_rehberim;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.p000v4.app.Fragment;
import android.support.p000v4.app.LoaderManager.LoaderCallbacks;
import android.support.p000v4.content.AsyncTaskLoader;
import android.support.p000v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import net.dijitalbeyin.firma_rehberim.adapters.CategoryAdapter;

public class CategoriesFragment extends Fragment implements LoaderCallbacks<List<Object>> {
    private static final int CATEGORY_LOADER_ID = 1;
    private static final String CATEGORY_REQUEST_URL = "https://firmarehberim.com/sayfalar/radyo/json/kategoriler.php";
    CategoryAdapter categoryAdapter;
    GridView gv_categories;
    OnFilterRespectToCategoryListener onFilterRespectToCategoryListener;
    ProgressBar pb_loadingCategories;
    TextView tv_emptyCatView;

    private static class CategoryLoader extends AsyncTaskLoader<List<Object>> {
        private String requestUrl;

        public CategoryLoader(@NonNull Context context, String str) {
            super(context);
            this.requestUrl = str;
        }

        public List<Object> loadInBackground() {
            String str = this.requestUrl;
            if (str == null) {
                return null;
            }
            return QueryUtils.fetchCategoryData(str);
        }

        /* access modifiers changed from: protected */
        public void onStartLoading() {
            forceLoad();
        }
    }

    public interface OnFilterRespectToCategoryListener {
        void OnFilterRespectToCategory(int i);
    }

    public void setOnFilterRespectToCategoryListener(OnFilterRespectToCategoryListener onFilterRespectToCategoryListener2) {
        this.onFilterRespectToCategoryListener = onFilterRespectToCategoryListener2;
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        return (ViewGroup) layoutInflater.inflate(C0662R.layout.fragment_categories, viewGroup, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) getContext().getSystemService("connectivity")).getActiveNetworkInfo();
        boolean z = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        this.pb_loadingCategories = (ProgressBar) view.findViewById(C0662R.C0664id.pb_loadingCategories);
        this.gv_categories = (GridView) view.findViewById(C0662R.C0664id.gv_categories);
        this.tv_emptyCatView = (TextView) view.findViewById(C0662R.C0664id.tv_emptyCatView);
        this.gv_categories.setEmptyView(this.tv_emptyCatView);
        if (z) {
            getLoaderManager().initLoader(1, null, this).forceLoad();
        } else {
            this.tv_emptyCatView.setText(getString(C0662R.string.no_internet_connection_text));
            this.pb_loadingCategories.setVisibility(8);
        }
        this.categoryAdapter = new CategoryAdapter(getContext(), C0662R.layout.item_category, new ArrayList());
        this.gv_categories.setAdapter(this.categoryAdapter);
        this.gv_categories.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                CategoriesFragment.this.onFilterRespectToCategoryListener.OnFilterRespectToCategory(((Category) adapterView.getItemAtPosition(i)).getCategoryId());
            }
        });
    }

    public Loader<List<Object>> onCreateLoader(int i, Bundle bundle) {
        return new CategoryLoader(getContext(), CATEGORY_REQUEST_URL);
    }

    public void onLoadFinished(Loader<List<Object>> loader, List<Object> list) {
        this.categoryAdapter.clear();
        if (list != null) {
            this.categoryAdapter.addAll(list);
        }
        this.tv_emptyCatView.setText(getString(C0662R.string.empty_categories_text));
        this.pb_loadingCategories.setVisibility(8);
    }

    public void onLoaderReset(Loader<List<Object>> loader) {
        this.categoryAdapter.clear();
    }
}
