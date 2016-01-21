package xyz.edmw;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NavViewHolder {
    @Bind(R.id.member_name)
    TextView name;
    @Bind(R.id.member_avatar)
    SimpleDraweeView avatar;
    @Bind(R.id.member_title)
    TextView title;

    private final Context context;
    private final View view;

    public NavViewHolder(Context context, View view) {
        this.context = context;
        this.view = view;
        ButterKnife.bind(this, view);
    }

    public void setUser(User user) {
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