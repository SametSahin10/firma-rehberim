package net.dijitalbeyin.firma_rehberim;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.dijitalbeyin.firma_rehberim.adapters.CategoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Object>> {
    private static final String CATEGORY_REQUEST_URL = "https://firmarehberim.com/sayfalar/radyo/json/kategoriler.php";
    private static final int CATEGORY_LOADER_ID = 1;

//    ListView lw_categories;
    GridView gv_categories;
    TextView tv_emptyCatView;
    CategoryAdapter categoryAdapter;
    ProgressBar pb_loadingCategories;

    OnFilterRespectToCategoryListener onFilterRespectToCategoryListener;

    public void setOnFilterRespectToCategoryListener(OnFilterRespectToCategoryListener onFilterRespectToCategoryListener) {
        this.onFilterRespectToCategoryListener = onFilterRespectToCategoryListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_categories, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

        pb_loadingCategories = view.findViewById(R.id.pb_loadingCategories);
//        lw_categories = view.findViewById(R.id.lw_categories);
        gv_categories = view.findViewById(R.id.gv_categories);
        tv_emptyCatView = view.findViewById(R.id.tv_emptyCatView);
        gv_categories.setEmptyView(tv_emptyCatView);
        if (isConnected) {
            getLoaderManager().initLoader(CATEGORY_LOADER_ID, null, this).forceLoad();
        } else {
            tv_emptyCatView.setText(getString(R.string.no_internet_connection_text));
            pb_loadingCategories.setVisibility(View.GONE);
        }
        categoryAdapter = new CategoryAdapter(getContext(), R.layout.item_category, new ArrayList<>());
        gv_categories.setAdapter(categoryAdapter);

        gv_categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category currentCategory = (Category) parent.getItemAtPosition(position);
                onFilterRespectToCategoryListener.OnFilterRespectToCategory(currentCategory.getCategoryId());
            }
        });
    }

    @Override
    public Loader<List<Object>> onCreateLoader(int i, Bundle bundle) {
        return new CategoryLoader(getContext(), CATEGORY_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Object>> loader, List<Object> categories) {
        categoryAdapter.clear();
        if (categories != null) {
            categoryAdapter.addAll(categories);
        }
        tv_emptyCatView.setText(getString(R.string.empty_categories_text));
        pb_loadingCategories.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<Object>> loader) {
        categoryAdapter.clear();
    }

    private static class CategoryLoader extends AsyncTaskLoader<List<Object>> {
        private String requestUrl;

        public CategoryLoader(@NonNull Context context, String requestUrl) {
            super(context);
            this.requestUrl = requestUrl;
        }

        @Override
        public List<Object> loadInBackground() {
            if (requestUrl == null) {
                return null;
            }
            ArrayList<Object> categories = QueryUtils.fetchCategoryData(requestUrl);
            return categories;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }

    public interface OnFilterRespectToCategoryListener {
        void OnFilterRespectToCategory(int categoryIdToFilter);
    }
}
