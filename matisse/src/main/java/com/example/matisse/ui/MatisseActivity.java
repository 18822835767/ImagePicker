package com.example.matisse.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.matisse.R;
import com.example.matisse.model.AlbumCollection;
import com.example.matisse.util.PermissionHelper;

public class MatisseActivity extends AppCompatActivity implements AlbumCollection.AlbumCallbacks {

    private static final int REQUEST_CODE = 0;
    private static final String TAG = "MatisseActivity";
    private AlbumCollection mAlbumCollection = new AlbumCollection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matisse);

        setActionBar();

        if (!PermissionHelper.permissionAllow(this, new String[]{Manifest.permission.
                READ_EXTERNAL_STORAGE})) {
            PermissionHelper.requestPermissions(this, new String[]{Manifest.permission.
                    READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            initData();
        }
    }

    private void initData() {
        mAlbumCollection.onCreate(this, this);
        mAlbumCollection.loadAlbums();
    }

    private void setActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAlbumLoad(Cursor cursor) {
        if (cursor.moveToFirst()) {
            do {
                Log.d(TAG, "onAlbumLoad: " + cursor.getString(cursor.getColumnIndex("bucket_display_name")));
            } while (cursor.moveToNext());
        }
    }

    @Override
    public void onAlbumReset() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initData();
            } else {
                Toast.makeText(this, "拒绝权限将无法选择图片", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
