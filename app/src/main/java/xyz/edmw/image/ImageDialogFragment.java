package xyz.edmw.image;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;
import com.ortiz.touch.TouchImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.edmw.R;

public class ImageDialogFragment extends DialogFragment {
    @Bind(R.id.image_view)
    TouchImageView imageView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    public static String source;

    public static final String tag = "ImageDialogFragment";

    public static ImageDialogFragment newInstance(String source) {
        ImageDialogFragment fragment = new ImageDialogFragment();

        fragment.setSource(source);
        return fragment;
    }

    public ImageDialogFragment() {

    }

    @Override

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Black);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_image, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String source = getSource();
        System.out.println(source);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Ion.with(imageView)
                .animateGif(AnimateGifMode.ANIMATE)
                .error(R.drawable.ic_error)
                .load(source)
                .setCallback(new FutureCallback<ImageView>() {

                    @SuppressLint("NewApi")
                    @Override
                    public void onCompleted(Exception arg0,
                                            ImageView arg1) {

                        progressBar.setVisibility(View.GONE);
                    }
                });

        imageView.setZoom(1, 1, 1, ImageView.ScaleType.FIT_CENTER);
    }

    public static String getSource() {
        return source;
    }

    public static void setSource(String source) {
        ImageDialogFragment.source = source;
    }
}