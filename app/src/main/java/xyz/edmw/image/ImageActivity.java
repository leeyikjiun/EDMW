package xyz.edmw.image;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;
import com.ortiz.touch.TouchImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.edmw.R;

public class ImageActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.image_view)
    TouchImageView imageView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    private static final String TAG = "ImageActivity";
    private static final String ARG_SOURCE = "arg_source";
    public Bitmap bitmap;

    public static void startInstance(Context context, String source) {
        Intent intent = new Intent(context, ImageActivity.class);
        intent.putExtra(ARG_SOURCE, source);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }

        String source = getIntent().getStringExtra(ARG_SOURCE);
        if (source.endsWith(".gif")) {
            loadGif(source);
        } else {
            loadImage(source);
        }
    }

    private void loadGif(String source) {
        progressBar.setVisibility(View.VISIBLE);
        Ion.with(imageView)
                .animateGif(AnimateGifMode.ANIMATE)
                .error(R.drawable.ic_error)
                .load(source)
                .setCallback(new FutureCallback<ImageView>() {
                    @Override
                    public void onCompleted(Exception e, ImageView result) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void loadImage(String source) {
        ImagePipeline pipeline = Fresco.getImagePipeline();
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(source))
                .build();
        DataSource<CloseableReference<CloseableImage>> dataSource = pipeline.fetchDecodedImage(request, this);
        try {
            dataSource.subscribe(subscriber, CallerThreadExecutor.getInstance());
        } finally {
            if (dataSource != null) {
                dataSource.close();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_dialog, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return bitmap != null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.action_share):
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                intent.setType("image/*");
                startActivity(Intent.createChooser(intent, "Share Image"));
                return true;
            case (R.id.action_save):
                path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null);
                intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.parse(path);
                intent.setData(contentUri);
                sendBroadcast(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private final BaseBitmapDataSubscriber subscriber =  new BaseBitmapDataSubscriber() {
        @Override
        protected void onNewResultImpl(Bitmap bitmap) {
            if (bitmap == null) {
                Log.w(TAG, "Bitmap data source returned success, but bitmap null.");
                return;
            }
            imageView.setImageBitmap(ImageActivity.this.bitmap = bitmap);
            invalidateOptionsMenu();
        }

        @Override
        protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

        }
    };
}
