package xyz.edmw.post;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.edmw.Message;
import xyz.edmw.R;
import xyz.edmw.rest.RestClient;
import xyz.edmw.settings.MainSharedPreferences;
import xyz.edmw.thread.ReplyForm;
import xyz.edmw.thread.ThreadActivity;

public class PostViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {
    @Bind(R.id.post_author)
    TextView author;
    @Bind(R.id.post_timestamp)
    TextView timestamp;
    @Bind(R.id.post_num)
    TextView postNum;
    @Bind(R.id.post_message)
    LinearLayout message;
    @Bind(R.id.post_avatar)
    SimpleDraweeView authorAvatar;
    @Bind(R.id.post_user_title)
    TextView userTitle;

    private final Context context;
    private final MainSharedPreferences preferences;
    private PopupMenu popup;
    private Post post;

    public PostViewHolder(Context context, View view, boolean isItem) {
        super(view);
        this.context = context;
        preferences = new MainSharedPreferences(context);

        if (isItem) {
            ButterKnife.bind(this, view);
            popup = new PopupMenu(context, postNum);
            popup.setOnMenuItemClickListener(PostViewHolder.this);
        }
    }

    public void setPost(final Post post) {
        this.post = post;
        message.removeAllViews();
        author.setText(Html.fromHtml(post.getAuthor()));
        timestamp.setText(Html.fromHtml(post.getTimestamp()));
        postNum.setText(post.getPostNum());
        userTitle.setText(post.getUserTitle());

        Message message = new Message(context, this.message);
        message.setPost(post);

        setAvatar(post.getAuthorAvatar());

        final Menu menu = popup.getMenu();
        menu.clear();
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.post_guest, popup.getMenu());
        if (post.hasFooter()) {
            inflater.inflate(R.menu.post_member, menu);
            if (!post.canEdit()) {
                menu.removeItem(R.id.action_edit);
            }
        }

        postNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuItem item = menu.findItem(R.id.action_like);
                if (item != null) {
                    String title = post.hasLike() ? "Unlike" : "Like";
                    title += " (" + post.getNumLikes() + ")";
                    item.setTitle(title);
                }
                popup.show();
            }
        });
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
        authorAvatar.setController(controller);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                EditPostDialog dialog = new EditPostDialog(context, post.getId());
                dialog.setText(getBBCode(post.getMessage()));
                dialog.show();
                return true;
            case R.id.action_quote:
                String message = getBBCode(post.getMessage());
                String quote = String.format("[QUOTE=%s;n%s]%s[/QUOTE]", post.getAuthor(), post.getId(), message);
                ((ThreadActivity) context).addQuote(quote);
                return true;
            case R.id.action_share:
                String url = RestClient.baseUrl + post.getPath();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_SUBJECT, "EDMW.XYZ");
                intent.putExtra(Intent.EXTRA_TEXT, url);
                intent.setType("text/plain");
                context.startActivity(Intent.createChooser(intent, "Share Post"));
                return true;
            case R.id.action_like:
                final boolean hasLike = post.hasLike();
                final String action = hasLike ? "unvote" : "vote";
                final String err = hasLike ? "unlike" : "like";
                ReplyForm replyForm = ((ThreadActivity) context).getReplyForm();
                Call<Void> call = RestClient.getService().reputation(action, post.getId(), replyForm.getSecurityToken());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Response<Void> response, Retrofit retrofit) {
                        if (response.isSuccess()) {
                            int numLikes = post.getNumLikes();
                            numLikes = post.hasLike() ? numLikes - 1 : numLikes + 1;
                            post.setNumLikes(numLikes);
                            post.setHasLike(!hasLike);
                        } else {
                            Toast.makeText(context, "Failed to " + err, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(context, "Failed to " + err, Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            default:
                return false;
        }
    }

    private String getBBCode(String html) {
        StringBuilder sb = new StringBuilder();
        Element body = Jsoup.parse(html).body();
        for (Node node : body.childNodes()) {
            if (node instanceof TextNode) {
                sb.append(((TextNode) node).text());
            } else if (node instanceof Element) {
                sb.append(getBBCode((Element) node));
            }
        }
        return sb.toString();
    }

    private String getBBCode(Element element) {
        switch (element.tagName()) {
            case "a":
                String text = element.text().trim();
                if (element.hasClass("b-bbcode-user")) {
                    return String.format("[user=\"%s\"]%s[/user]", element.attr("data-userid"), text);
                } else {
                    return String.format("[url=%s]%s[/url]", element.attr("href"), text);
                }
            case "b":
                return String.format("[b]%s[/b]", element.text().trim());
            case "i":
                return String.format("[i]%s[/i]", element.text().trim());
            case "img":
                return getImage(element.attr("src"));
            case "br":
                return System.getProperty("line.separator");
            case "font":
                String face = element.attr("face");
                if (!TextUtils.isEmpty(face)) {
                    text = element.children().isEmpty() ? element.text().trim() : getBBCode(element.child(0));
                    return String.format("[font=%s]%s[/font]", face, text);
                }
                String color = element.attr("color");
                if (!TextUtils.isEmpty(color)) {
                    text = element.children().isEmpty() ? element.text().trim() : getBBCode(element.child(0));
                    return String.format("[color=%s]%s[/color]", color, text);
                }
            case "span":
                String style = element.attr("style");
                if (TextUtils.isEmpty(style)) {
                    return "";
                }

                String[] tokens = style.split(":");
                if (tokens.length == 2 && tokens[0].equals("font-size")) {
                    text = element.children().isEmpty() ? element.text().trim() : getBBCode(element.child(0));
                    return String.format("[size=%s]%s[/size]", tokens[1], text);
                }
            case "div":
                // fall through
            default:
                return "";
        }
    }

    private String getImage(String source) {
        if (source.startsWith("http://www.edmw.xyz/core/images/smilies/")) {
            String name = source.substring(source.lastIndexOf("/") + 1, source.lastIndexOf("."));
            if (name.equals("smile")) {
                return ":)";
            } else if (name.equals("mad")) {
                return ":mad2:";
            } else if (name.equals("redface")) {
                return ":o";
            } else {
                return String.format(":%s:", name);
            }
        } else {
            return String.format("[img]%s[/img]", source);
        }
    }
}
