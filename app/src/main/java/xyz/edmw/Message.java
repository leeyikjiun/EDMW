package xyz.edmw;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.camera.drawable.TextDrawable;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import xyz.edmw.image.ImageDialogFragment;
import xyz.edmw.quote.Quote;
import xyz.edmw.quote.QuoteViewHolder;
import xyz.edmw.sharedpreferences.MainSharedPreferences;
import xyz.edmw.thread.ThreadActivity;

public class Message {
    private static final String tag = "Message";
    private final TextDrawable tapToRetry;
    private final Context context;
    private final LinearLayout message;

    public Message(Context context, LinearLayout message) {
        this.context = context;
        this.message = message;

        Resources resources = context.getResources();
        tapToRetry = new TextDrawable(resources, "Tap to retry.");
    }

    public void setMessage(String message) {
        this.message.removeAllViews();

        Element body = Jsoup.parseBodyFragment(message).body();
        for (Node node : body.childNodes()) {
            if (node instanceof TextNode) {
                setTextNode((TextNode) node);
            } else if (node instanceof Element) {
                setElement((Element) node);
            } else {
                Log.w(tag, "Unknown node.");
            }
        }
    }

    private void setTextNode(TextNode node) {
        TextView view = new TextView(context);
        view.setTextColor(context.getResources().getColor(R.color.font_color_black));
        String text = node.text().trim();
        if (!text.isEmpty()) {
            view.setText(Html.fromHtml(text));
            message.addView(view);
        }
    }

    private void setElement(final Element element) {
        switch (element.tagName()) {
            case "br":
                // do nothing
                // TODO this removes intentional newlines.
                break;
            case "img":
                String source = element.attr("src");

                // Prevent image from loading
                if(MainSharedPreferences.getLoadImageAutomatically())
                    setImage(source);
                break;
            case "iframe":
                String videoId = element.attr("src");
                videoId = videoId.substring(videoId.lastIndexOf("/") + 1);
                setYoutube(videoId);
                break;
            case "div":
                String className = element.className();
                if (className.equals("bbcode_container")) {
                    View view = LayoutInflater.from(context).inflate(R.layout.view_quote, null);
                    QuoteViewHolder viewHolder = new QuoteViewHolder(context, view);

                    String postedBy = null;
                    String message;
                    Element postedByElement = element.select("div.bbcode_postedby").first();
                    if (postedByElement != null) {
                        postedBy = postedByElement.text().trim();
                        message = element.select("div.message").first().html();
                    } else {
                        message = element.select("div.quote_container").first().html();
                    }

                    Quote quote = new Quote(postedBy, message);
                    viewHolder.setQuote(quote);

                    this.message.addView(view);
                    break;
                } else if (className.equals("videocontainer")) {
                    videoId = element.select("a.video-frame").first().attr("data-vcode");
                    setYoutube(videoId);
                    break;
                }
                // fall through
            case "a":
                Log.i("a", element.outerHtml());
                Element img = element.select("img").first();
                if (img != null) {
                    setImage(img.attr("src"));
                    break;
                }
            case "b":
                // TODO take care of bold and font size
                // <b><span style="font-size:72px">親, 你们喜欢玩3P 吗？<br /> 不会的话，我愿意教你们玩 <img src="http://www.edmw.xyz/core/images/smilies/smilies-extra/bye.gif" border="0" alt="" title="Bye" smilieid="74" class="inlineimg" /><img src="http://www.edmw.xyz/core/images/smilies/smilies-extra/bouncy.gif" border="0" alt="" title="Bouncy" smilieid="73" class="inlineimg" /><img src="http://www.edmw.xyz/core/images/smilies/smilies-extra/bouncy.gif" border="0" alt="" title="Bouncy" smilieid="73" class="inlineimg" /></span></b>
            default:
                TextView view = new TextView(context);
                view.setTextColor(context.getResources().getColor(R.color.font_color_black));
                view.setMovementMethod(LinkMovementMethod.getInstance());
                view.setText(Html.fromHtml(element.html()));
                Linkify.addLinks(view, Linkify.ALL);
                message.addView(view);
        }
    }

    private void setImage(final String source) {
        if (source.contains("www.edmw.xyz/core/images/smilies")
                || source.contains("www.hardwarezone.com.sg/img/forums/hwz/smilies")
                || source.contains("forum.lowyat.net/style_emoticons/")
                || source.contains("illiweb.com/fa/i/smiles")) {
            final ImageView imageView = new ImageView(context);
            Ion.with(imageView)
                    .animateGif(AnimateGifMode.ANIMATE)
                    //.placeholder(R.drawable.progress_animation)
                    .error(R.drawable.ic_error)
                    .load(source)
                    .setCallback(new FutureCallback<ImageView>() {
                        @SuppressLint("NewApi")
                        @Override
                        public void onCompleted(Exception e, final ImageView imageView) {
                            imageView.getViewTreeObserver().addOnPreDrawListener(
                                    new ViewTreeObserver.OnPreDrawListener() {
                                        public boolean onPreDraw() {
                                            imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                            imageView.setAdjustViewBounds(true);
                                            imageView.getLayoutParams().width = imageView.getDrawable().getIntrinsicWidth() * 3;
                                            return true;
                                        }
                                    });
                        }
                    });
            message.addView(imageView);

        } else {
            SimpleDraweeView imageView = new SimpleDraweeView(context);
            imageView.setAdjustViewBounds(true);
            message.addView(imageView);

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(Uri.parse(source))
                    .setTapToRetryEnabled(true)
                    .setAutoPlayAnimations(true)
                    .build();
            imageView.setController(controller);

            GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(context.getResources())
                    .setRetryImage(tapToRetry)
                    .setProgressBarImage(new ProgressBarDrawable())
                    .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                    .build();
            imageView.setHierarchy(hierarchy);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fm = ((ThreadActivity) context).getSupportFragmentManager();
                    ImageDialogFragment a = ImageDialogFragment.newInstance(source);
                    a.show(fm, "dialog_image");
                }
            });
        }
    }

    // TODO replace with developer key
    private static final String DeveloperKey = null;
    private void setYoutube(final String videoID) {
        YouTubePlayerSupportFragment youTubePlayerSupportFragment = YouTubePlayerSupportFragment.newInstance();
        youTubePlayerSupportFragment.initialize(DeveloperKey, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
                youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
                youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);
                youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);

                if (!wasRestored) {
                    ThreadActivity.youTubePlayer = youTubePlayer;

                    youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {

                        @Override
                        public void onFullscreen(boolean isFullScreen) {
                            ThreadActivity.isFullscreen = isFullScreen;
                        }
                    });
                    ThreadActivity.youTubePlayer.cueVideo(videoID);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
        ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                .add(message.getId(), youTubePlayerSupportFragment)
                .commit();
    }
}
