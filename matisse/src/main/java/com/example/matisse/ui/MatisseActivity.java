package com.example.matisse.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matisse.R;
import com.example.matisse.entity.Album;
import com.example.matisse.entity.Item;
import com.example.matisse.internal.ui.AlbumPreviewActivity;
import com.example.matisse.internal.ui.MediaSelectionFragment;
import com.example.matisse.internal.ui.adapter.AlbumMediaAdapter;
import com.example.matisse.internal.ui.adapter.AlbumsAdapter;
import com.example.matisse.internal.ui.widget.AlbumSpinner;
import com.example.matisse.model.AlbumCollection;
import com.example.matisse.model.SelectedItemCollection;
import com.example.matisse.util.PermissionHelper;

import static com.example.matisse.internal.ui.AlbumPreviewActivity.EXTRA_ALBUM;
import static com.example.matisse.internal.ui.AlbumPreviewActivity.EXTRA_ITEM;

public class MatisseActivity extends AppCompatActivity implements AlbumCollection.AlbumCallbacks,
        AdapterView.OnItemSelectedListener, AlbumMediaAdapter.OnMediaClickListener {

    private static final int REQUEST_CODE = 0;
    private static final String TAG = "MatisseActivity";
    private AlbumCollection mAlbumCollection = new AlbumCollection();

    private AlbumSpinner mAlbumSpinner;
    private AlbumsAdapter mAlbumsAdapter;

    private View mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matisse);

        setActionBar();

        //重置SelectedItemCollection的值.
        SelectedItemCollection.getInstance().reset();

        initView();
        initSpinner();
        requestPermission();
    }

    private void initView() {
        mContainer = findViewById(R.id.container);
    }

    private void initAlbumData() {
        mAlbumCollection.onCreate(this, this);
        mAlbumCollection.loadAlbums();
    }

    private void initSpinner() {
        mAlbumsAdapter = new AlbumsAdapter(this, null, false);
        mAlbumSpinner = new AlbumSpinner(this);
        mAlbumSpinner.setOnItemSelectedListener(this);
        mAlbumSpinner.setSelectedText((TextView) findViewById(R.id.selected_album));
        mAlbumSpinner.setAnchorView(findViewById(R.id.toolbar));
        mAlbumSpinner.setAdapter(mAlbumsAdapter);
    }

    private void requestPermission() {
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

    private void onAlbumSelected(Album album) {
        mContainer.setVisibility(View.VISIBLE);
        Fragment fragment = MediaSelectionFragment.newInstance(album);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commitAllowingStateLoss();
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
    public void onAlbumLoad(final Cursor cursor) {
        mAlbumsAdapter.swapCursor(cursor);

        Handler handler = new Handler(getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                cursor.moveToPosition(mAlbumCollection.getCurrentSelection());
                mAlbumSpinner.setSelection(mAlbumCollection.getCurrentSelection());
                Album album = Album.valueOf(cursor);
                onAlbumSelected(album);
            }
        });
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
        mAlbumCollection.setStateCurrentSelection(position);
        mAlbumsAdapter.getCursor().moveToPosition(position);
        Album album = Album.valueOf(mAlbumsAdapter.getCursor());
        onAlbumSelected(album);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        mAlbumCollection.onDestroy();
    }

    @Override
    public void onThumbnailClick(Album album, Item item) {
        Intent intent = new Intent(this, AlbumPreviewActivity.class);
        intent.putExtra(EXTRA_ALBUM,album);
        intent.putExtra(EXTRA_ITEM,item);
        startActivity(intent);
    }
}
