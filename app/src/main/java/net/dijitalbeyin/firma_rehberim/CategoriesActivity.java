package net.dijitalbeyin.firma_rehberim;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.dijitalbeyin.firma_rehberim.adapters.CategoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Category>> {
    private static final String CATEGORY_REQUEST_URL = "https://firmarehberim.com/sayfalar/radyo/json/kategoriler.php";
    private static final int CATEGORY_LOADER_ID = 1;

    ListView lw_categories;
    TextView tv_emptyCatView;
    CategoryAdapter categoryAdapter;
    ProgressBar pb_loadingCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                              && activeNetwork.isConnectedOrConnecting();

        pb_loadingCategories = findViewById(R.id.pb_loadingCategories);
        lw_categories = findViewById(R.id.lw_categories);
        tv_emptyCatView = findViewById(R.id.tv_emptyCatView);
        lw_categories.setEmptyView(tv_emptyCatView);
        if (isConnected) {
            getSupportLoaderManager().initLoader(CATEGORY_LOADER_ID, null, this).forceLoad();
        } else {
            tv_emptyCatView.setText(getString(R.string.no_internet_connection_text));
            pb_loadingCategories.setVisibility(View.GONE);
        }
        categoryAdapter = new CategoryAdapter(this, R.layout.item_category, new ArrayList<Category>());
        lw_categories.setAdapter(categoryAdapter);
    }

    @Override
    public Loader<List<Category>> onCreateLoader(int i, Bundle bundle) {
        return new CategoryLoader(this, CATEGORY_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Category>> loader, List<Category> categories) {
        categoryAdapter.clear();
        if (categories != null) {
            categoryAdapter.addAll(categories);
        }
        tv_emptyCatView.setText(getString(R.string.empty_categories_text));
        pb_loadingCategories.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<Category>> loader) {
        categoryAdapter.clear();
    }

    private static class CategoryLoader extends AsyncTaskLoader<List<Category>> {
        private String requestUrl;

        public CategoryLoader(@NonNull Context context, String requestUrl) {
            super(context);
            this.requestUrl = requestUrl;
        }

        @Override
        public List<Category> loadInBackground() {
            if (requestUrl == null) {
                return null;
            }
            ArrayList<Category> categories = QueryUtils.fetchCategoryData(requestUrl);
            return categories;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }
}
