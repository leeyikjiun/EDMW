package xyz.edmw;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
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
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequest.RequestLevel;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import xyz.edmw.image.ImageActivity;
import xyz.edmw.post.Post;
import xyz.edmw.quote.Quote;
import xyz.edmw.quote.QuoteViewHolder;
import xyz.edmw.settings.MainSharedPreferences;
import xyz.edmw.youtube.YouTubeThumbnailViewHolder;

public class Message {
    private static final String tag = "Message";
    private static final String endl = System.getProperty("line.separator");
    private final TextDrawable tapToRetry;
    private final Context context;
    private final LinearLayout message;
    private final MainSharedPreferences preferences;

    public Message(Context context, LinearLayout message) {
        this.context = context;
        this.message = message;
        preferences = new MainSharedPreferences(context);

        Resources resources = context.getResources();
        tapToRetry = new TextDrawable(resources, "Tap to retry.");
    }

    public void setPost(Post post) {
        setMessage(post.getMessage());
        for (String source : post.getPhotos()) {
            setImage(source);
        }
    }

    public void setMessage(String message) {
        this.message.removeAllViews();

        message = message.replace("<br /> " + endl + "<br /> " + endl, endl);
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
        String text = node.getWholeText();
        if (!TextUtils.isEmpty(text.trim())) {
            text = text.replace(endl + endl, "<br /> ");
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
                        setImage(source);
                break;
            case "iframe":
                String videoId = element.attr("src");
                videoId = videoId.substring(videoId.lastIndexOf("/") + 1);
                setYoutube(videoId);
                break;
            case "div":
                if (element.hasClass("bbcode_container")) {
                    View view = LayoutInflater.from(context).inflate(R.layout.view_quote, null);
                    QuoteViewHolder viewHolder = new QuoteViewHolder(context, view);

                    String id = null;
                    String postedBy = null;
                    String message;
                    Element postedByElement = element.select("div.bbcode_postedby").first();
                    if (postedByElement != null) {
                        id = postedByElement.select("a").attr("href");
                        int index = id.lastIndexOf("#post");
                        if (index >= 0) {
                            id = id.substring(index + 5);
                            postedBy = postedByElement.text().trim();
                        } else {
                            Log.w(tag, id + " does not contain #post");
                        }
                        message = element.select("div.message").first().html();
                    } else {
                        Element div = element.select("div.quote_container").first();
                        if (div != null) {
                            message = div.html();
                        } else {
                            message = element.select("pre.bbcode_code").first().html();
                            TextView textView = new TextView(context);
                            textView.setText("Code:");
                            this.message.addView(textView);
                        }
                    }

                    Quote quote = new Quote(id, postedBy, message);
                    viewHolder.setQuote(quote);

                    this.message.addView(view);
                    break;
                } else if (element.hasClass("videocontainer")) {
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
                view.setMovementMethod(LinkMovementMethod.getInstance());
                view.setText(Html.fromHtml(element.outerHtml()));
                message.addView(view);
        }
    }

    private void setImage(final String source) {
        boolean canDownloadImage = preferences.canDownloadImage();

        if (source.contains("www.edmw.xyz/core/images/smilies")
                || source.contains("www.hardwarezone.com.sg/img/forums/hwz/smilies")
                || source.contains("forum.lowyat.net/style_emoticons/")
                || source.contains("illiweb.com/fa/i/smiles")) {
            if (!canDownloadImage) {
                return;
            }
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

            RequestLevel level = canDownloadImage ? RequestLevel.FULL_FETCH : RequestLevel.DISK_CACHE;
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(source))
                    .setLowestPermittedRequestLevel(level)
                    .build();

            GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(context.getResources())
                    .setRetryImage(tapToRetry)
                    .setProgressBarImage(new ProgressBarDrawable())
                    .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                    .build();
            imageView.setHierarchy(hierarchy);

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setTapToRetryEnabled(true)
                    .setAutoPlayAnimations(true)
                    .build();
            imageView.setController(controller);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageActivity.startInstance(context, source);
                }
            });
        }
    }

    private void setYoutube(final String videoID) {
        View view = LayoutInflater.from(context).inflate(R.layout.youtube_thumbnail, null, false);
        YouTubeThumbnailViewHolder viewHolder = new YouTubeThumbnailViewHolder(context, view);
        viewHolder.setVideoId(videoID);
        message.addView(view);
    }
}
