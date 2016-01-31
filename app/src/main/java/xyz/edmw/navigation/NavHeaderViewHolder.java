package xyz.edmw.navigation;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

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
        if (user == null) {

        } else {
            name.setText(user.getName());

            if (preferences.canDownloadImage()) {
                avatar.setVisibility(View.VISIBLE);
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setUri(Uri.parse(user.getAvatar()))
                        .setAutoPlayAnimations(true)
                        .build();
                avatar.setController(controller);
            } else {
                avatar.setVisibility(View.GONE);
            }
        }
        title.setVisibility(View.GONE);
    }
}