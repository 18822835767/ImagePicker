package com.example.matisse.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import com.example.matisse.internal.ui.SelectedPreviewActivity;
import com.example.matisse.internal.ui.adapter.AlbumMediaAdapter;
import com.example.matisse.internal.ui.adapter.AlbumsAdapter;
import com.example.matisse.internal.ui.widget.AlbumSpinner;
import com.example.matisse.model.AlbumCollection;
import com.example.matisse.model.SelectedItemCollection;
import com.example.matisse.util.PermissionHelper;

import static com.example.matisse.internal.ui.AlbumPreviewActivity.EXTRA_ALBUM;
import static com.example.matisse.internal.ui.AlbumPreviewActivity.EXTRA_ITEM;
import static com.example.matisse.internal.ui.BasePreviewActivity.EXTRA_RESULT_APPLY;
import static com.example.matisse.internal.ui.BasePreviewActivity.SELECTED_ITEMS;

public class MatisseActivity extends AppCompatActivity implements AlbumCollection.AlbumCallbacks,
        AdapterView.OnItemSelectedListener, AlbumMediaAdapter.OnMediaClickListener,
        View.OnClickListener,AlbumMediaAdapter.CheckStateListener {

    private static final String TAG = "MatisseActivity";
    private static final int PERMISSION_REQUEST_CODE = 0;
    private static final int PREVIEW_REQUEST_CODE = 10;

    private AlbumCollection mAlbumCollection = new AlbumCollection();
    private SelectedItemCollection mSelectedItemCollection;

    private AlbumSpinner mAlbumSpinner;
    private AlbumsAdapter mAlbumsAdapter;

    private View mContainer;
    private TextView mButtonPreview;
    private TextView mButtonApply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matisse);

        setActionBar();

        mSelectedItemCollection = SelectedItemCollection.getInstance();
        //重置SelectedItemCollection的值.
        mSelectedItemCollection.reset();

        initView();
        initEvent();
        initSpinner();
        requestPermission();
    }

    private void initView() {
        mContainer = findViewById(R.id.container);
        mButtonPreview = findViewById(R.id.button_preview);
        mButtonApply = findViewById(R.id.button_apply);
    }

    private void initEvent() {
        mButtonPreview.setOnClickListener(this);
        mButtonApply.setOnClickListener(this);
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
                    READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
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
                .replace(R.id.container, fragment, MediaSelectionFragment.class.getSimpleName())
                .commitAllowingStateLoss();
    }

    /**
     * 更新底部的状态栏，比如勾选图片后更新多张图片可使用.
     */
    private void updateBottomToolbar() {
        int count = mSelectedItemCollection.getSize();
        if(count == 0){
            mButtonApply.setText(getResources().getString(R.string.apply));
        }else{
            mButtonApply.setText(String.valueOf(getResources().getString(R.string.apply)+"("+count+")"));
        }
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
        if (requestCode == PERMISSION_REQUEST_CODE) {
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
        intent.putExtra(EXTRA_ALBUM, album);
        intent.putExtra(EXTRA_ITEM, item);
        startActivityForResult(intent, PREVIEW_REQUEST_CODE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_preview) {
            if (mSelectedItemCollection.isEmpty()) {
                Toast.makeText(this, "亲，还没有图片呢", Toast.LENGTH_SHORT).show();
            }else {
                Intent intent = new Intent(this, SelectedPreviewActivity.class);
                intent.putParcelableArrayListExtra(SELECTED_ITEMS, mSelectedItemCollection.getItems());
                startActivityForResult(intent, PREVIEW_REQUEST_CODE);
            }
        }else if(v.getId() == R.id.button_apply){
            if (mSelectedItemCollection.isEmpty()) {
                Toast.makeText(this, "亲，还没有图片呢", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == PREVIEW_REQUEST_CODE) {
            boolean apply = data.getBooleanExtra(EXTRA_RESULT_APPLY, false);
            if (!apply) {
                MediaSelectionFragment fragment = (MediaSelectionFragment) getSupportFragmentManager().findFragmentByTag(
                        MediaSelectionFragment.class.getSimpleName());
                if (fragment != null) {
                    fragment.refreshMediaGrid();
                }
                updateBottomToolbar();
            }
        }
    }

    @Override
    public void update() {
        updateBottomToolbar();
    }
}
