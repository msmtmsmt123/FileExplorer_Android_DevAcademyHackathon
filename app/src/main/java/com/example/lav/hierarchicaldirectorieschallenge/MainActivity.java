package com.example.lav.hierarchicaldirectorieschallenge;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lav.hierarchicaldirectorieschallenge.model.FileSystem;
import com.example.lav.hierarchicaldirectorieschallenge.model.FileSystemEntry;
import com.example.lav.hierarchicaldirectorieschallenge.utils.NetworkUtils;
import com.google.gson.Gson;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    RecyclerView directories;
    RecyclerView subDirectories;

    FileSystemEntryAdapter directoriesAdapter;
    FileSystemEntryAdapter subDirectoriesAdapter;

    FileSystemEntry context;
    EditText mSearchEditText;
    FileSystem root;

    private boolean isSearchOpen = false;
    private String DATA_FILE = "input1.json";
    private static final ArrayList<String> availableDataFiles =
            new ArrayList<>(Arrays.asList("input1.json", "input2.json", "input3.json"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        directories = findViewById(R.id.rv_directories);
        subDirectories = findViewById(R.id.rv_subDirectories);

        LinearLayoutManager directoriesLayout = new LinearLayoutManager(this);
        directories.setLayoutManager(directoriesLayout);
        directories.setHasFixedSize(false);

        LinearLayoutManager subDirectoriesLayout = new LinearLayoutManager(this);
        subDirectories.setLayoutManager(subDirectoriesLayout);
        subDirectories.setHasFixedSize(false);

        directoriesAdapter = new FileSystemEntryAdapter(new DirectoriesOnClickHandler());
        directories.setAdapter(directoriesAdapter);

        subDirectoriesAdapter = new FileSystemEntryAdapter(new SubDirectoriesOnClickHandler());
        subDirectories.setAdapter(subDirectoriesAdapter);

        loadData(DATA_FILE);
    }

    private void loadData(String dataFile) {
        new FetchHierarchicalDataTask().execute(dataFile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_back) {

            if(context == null || context.getParent() == null) {
                Toast.makeText(this, R.string.back_error_message, Toast.LENGTH_LONG).show();
                return true;
            }

            FileSystemEntry current = context.getParent();
            if(current.getParent() != null) {
                directoriesAdapter.setFileSystem(new FileSystem(current.getParent().getContents()));
            } else {
                directoriesAdapter.setFileSystem(root);
            }
            context = current;
            subDirectoriesAdapter.setFileSystem(new FileSystem(current.getContents()));
            return true;
        } else if (item.getItemId() == R.id.action_search) {
            handleMenuSearch();
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleMenuSearch() {
        ActionBar action = getSupportActionBar();

        View view = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if(!isSearchOpen) {
            action.setDisplayShowCustomEnabled(true);
            action.setCustomView(R.layout.search_bar);
            mSearchEditText= action.getCustomView().findViewById(R.id.edtSearch);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            isSearchOpen = true;
        } else {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            String resultFromSearch = mSearchEditText.getText().toString();
            action.setDisplayShowCustomEnabled(false);
            isSearchOpen = false;

            if(!availableDataFiles.contains(resultFromSearch)) {
                Toast.makeText(this, R.string.search_error_message, Toast.LENGTH_LONG).show();
                return;
            }
            loadData(DATA_FILE);
        }
    }

    public class FetchHierarchicalDataTask extends AsyncTask<String, Void, FileSystem> {

        @Override
        protected FileSystem doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String dataFile = params[0];
            URL requestUrl = NetworkUtils.buildUrl(dataFile);

            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(requestUrl);
                FileSystem fileSystem = new Gson().fromJson(jsonResponse, FileSystem.class);
                fileSystem.setParentForEntries();
                return fileSystem;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(FileSystem fileSystem) {
            if (fileSystem != null) {
                root = fileSystem;
                directoriesAdapter.setFileSystem(fileSystem);
                subDirectoriesAdapter.setFileSystem(null);
            }
        }
    }

    public class DirectoriesOnClickHandler implements FileSystemEntryAdapter.EntryClickListener {

        @Override
        public void onClickHandler(FileSystemEntry fileSystemEntry) {

            if(!isDirectory(fileSystemEntry))
                return;

            context = fileSystemEntry;
            subDirectoriesAdapter.setFileSystem(new FileSystem(fileSystemEntry.getContents()));
        }
    }

    public class SubDirectoriesOnClickHandler implements FileSystemEntryAdapter.EntryClickListener {

        @Override
        public void onClickHandler(FileSystemEntry fileSystemEntry) {

            if(!isDirectory(fileSystemEntry))
                return;

            context = fileSystemEntry;
            directoriesAdapter.setFileSystem(new FileSystem(fileSystemEntry.getParent().getContents()));
            subDirectoriesAdapter.setFileSystem(new FileSystem(fileSystemEntry.getContents()));
        }
    }

    private boolean isDirectory(FileSystemEntry fileSystemEntry) {
        return "directory".equals(fileSystemEntry.getType());
    }
}
