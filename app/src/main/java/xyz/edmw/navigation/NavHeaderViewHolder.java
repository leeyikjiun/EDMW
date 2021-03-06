package xyz.edmw.navigation;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.edmw.R;
import xyz.edmw.User;
import xyz.edmw.settings.MainSharedPreferences;

public class NavHeaderViewHolder {
    @Bind(R.id.member_name)
    TextView name;
    @Bind(R.id.member_avatar)
    SimpleDraweeView avatar;
    @Bind(R.id.member_title)
    TextView title;

    private final Context context;
    private final View view;
    private final MainSharedPreferences preferences;

    public NavHeaderViewHolder(Context context, View view) {
        this.context = context;
        this.view = view;
        ButterKnife.bind(this, view);
        preferences = new MainSharedPreferences(context);
    }

    public void setUser(User user) {
        if (user != null) {
            name.setText(user.getName());
            setAvatar(user.getAvatar());
        } else {
            name.setText(context.getResources().getText(R.string.login));
            setAvatar(R.drawable.temp_avatar);
        }
        title.setVisibility(View.GONE);
    }

    private void setAvatar(int resource) {
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                .path(String.valueOf(resource))
                .build();
        avatar.setImageURI(uri);
    }

    private void setAvatar(String source) {
        ImageRequest.RequestLevel level = preferences.canDownloadImage() ? ImageRequest.RequestLevel.FULL_FETCH : ImageRequest.RequestLevel.DISK_CACHE;
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(source))
                .setLowestPermittedRequestLevel(level)
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setAutoPlayAnimations(true)
                .build();
        avatar.setController(controller);
    }
}