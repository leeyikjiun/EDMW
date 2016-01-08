package xyz.edmw;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import xyz.edmw.quote.Quote;
import xyz.edmw.quote.QuoteViewHolder;

public class Message {
    private static final String tag = "Message";
    private final Context context;
    private final LinearLayout message;

    public Message(Context context, LinearLayout message) {
        this.context = context;
        this.message = message;
    }

    public void setMessage(String message) {
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
                Log.d(tag, source);
                setImage(source);
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
                    final String videoId = element.select("a.video-frame").first().attr("data-vcode");
                    setYoutube(videoId);
                    break;
                }
                // fall through
            case "a":
                Log.i("a", element.outerHtml());
            default:
                TextView view = new TextView(context);
                view.setMovementMethod(LinkMovementMethod.getInstance());
                view.setText(Html.fromHtml(element.html()));
                message.addView(view);
        }
    }

    private void setImage(String source) {
        final ImageView imageView = new ImageView(context);
        imageView.setAdjustViewBounds(true);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        message.addView(imageView);

        Picasso.with(context)
                .load(source)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.ic_error)
                .into(imageView);
    }

    // TODO replace with developer key
    private static final String DeveloperKey = null;
    private void setYoutube(final String videoID) {
        YouTubePlayerSupportFragment youTubePlayerSupportFragment = YouTubePlayerSupportFragment.newInstance();
        youTubePlayerSupportFragment.initialize(DeveloperKey, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
                youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);
                youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);
                youTubePlayer.cueVideo(videoID);
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
