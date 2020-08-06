package com.example.matisse.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matisse.R;
import com.example.matisse.internal.ui.adapter.AlbumsAdapter;
import com.example.matisse.internal.ui.widget.AlbumSpinner;
import com.example.matisse.model.AlbumCollection;
import com.example.matisse.util.PermissionHelper;

public class MatisseActivity extends AppCompatActivity implements AlbumCollection.AlbumCallbacks, 
        AdapterView.OnItemSelectedListener {

    private static final int REQUEST_CODE = 0;
    private static final String TAG = "MatisseActivity";
    private AlbumCollection mAlbumCollection = new AlbumCollection();
    
    private AlbumSpinner mAlbumSpinner;
    private AlbumsAdapter mAlbumsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matisse);

        setActionBar();
        initSpinner();
        requestPermission();
        
        
    }

    private void initAlbumData() {
        mAlbumCollection.onCreate(this, this);
        mAlbumCollection.loadAlbums();
    }

    private void initSpinner(){
        mAlbumsAdapter = new AlbumsAdapter(this,null,false);
        mAlbumSpinner = new AlbumSpinner(this);
        mAlbumSpinner.setOnItemSelectedListener(this);
        mAlbumSpinner.setSelectedText((TextView) findViewById(R.id.selected_album));
        mAlbumSpinner.setAnchorView(findViewById(R.id.toolbar));
        mAlbumSpinner.setAdapter(mAlbumsAdapter);
    }
    
    private void requestPermission(){
        if (!PermissionHelper.permissionAllow(this, new String[]{Manifest.permission.
                READ_EXTERNAL_STORAGE})) {
            PermissionHelper.requestPermissions(this, new String[]{Manifest.permission.
                    READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            initAlbumData();
        }
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
                Log.d(TAG, "onAlbumLoad: " + cursor.getString(cursor.getColumnIndex
                        ("bucket_display_name")) + ": "+cursor.getString(cursor.getColumnIndex("uri")));
            } while (cursor.moveToNext());
        }
        mAlbumsAdapter.swapCursor(cursor);
    }

    @Override
    public void onAlbumReset() {
        mAlbumsAdapter.swapCursor(null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initAlbumData();
            } else {
                Toast.makeText(this, "拒绝权限将无法选择图片", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
