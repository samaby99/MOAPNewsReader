package at.technikum_wien.sommerbauer.newsreader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;

import java.util.List;

import at.technikum_wien.sommerbauer.newsreader.data.NewsItem;
import at.technikum_wien.sommerbauer.newsreader.rv.NewsListAdapter;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsItem>>, SharedPreferences.OnSharedPreferenceChangeListener {
    private NewsListAdapter mAdapter;
    private static final int LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
      sharedPreferences.registerOnSharedPreferenceChangeListener(this);
      String defaultDataURL = getResources().getString(R.string.settings_data_URL_default);
      String dataURL = sharedPreferences.getString("data_URL", defaultDataURL);

        setContentView(R.layout.activity_main);

            Bundle args = new Bundle();
            args.putString(DataLoader.ARG_URL, dataURL);
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.restartLoader(LOADER_ID, args, MainActivity.this);

            mAdapter = new NewsListAdapter(null, new NewsListAdapter.ListItemClickListener() {
            @Override
            public void onListItemClick(NewsItem clickedItem) {
                Intent intent = new Intent(MainActivity.this, NewsDetailsActivity.class);
                intent.putExtra(NewsDetailsActivity.ITEM_KEY, clickedItem);
                startActivity(intent);
          }
        });

        RecyclerView recyclerView = findViewById(R.id.rv_list);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        if (id == LOADER_ID)
            return new DataLoader(this, args);
        else
            return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<NewsItem>> loader, List<NewsItem> data) {
        mAdapter.swapItems(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<NewsItem>> loader) {
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("data_URL")) {
            //do nothing
        }
    }
}
