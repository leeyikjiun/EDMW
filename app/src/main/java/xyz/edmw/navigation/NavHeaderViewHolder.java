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

public class NavHeaderViewHolder {
    @Bind(R.id.member_name)
    TextView name;
    @Bind(R.id.member_avatar)
    SimpleDraweeView avatar;
    @Bind(R.id.member_title)
    TextView title;

    private final Context context;
    private final View view;

    public NavHeaderViewHolder(Context context, View view) {
        this.context = context;
        this.view = view;
        ButterKnife.bind(this, view);
    }

    public void setUser(User user) {
        if (user == null) {
            title.setVisibility(View.GONE);
        } else {
            name.setText(user.getName());

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(Uri.parse(user.getAvatar()))
                    .setTapToRetryEnabled(true)
                    .setAutoPlayAnimations(true)
                    .build();
            avatar.setController(controller);

            title.setVisibility(View.GONE);
        }
    }
}