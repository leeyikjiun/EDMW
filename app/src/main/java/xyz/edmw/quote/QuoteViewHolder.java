package xyz.edmw.quote;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.edmw.Message;
import xyz.edmw.R;
import xyz.edmw.thread.ThreadActivity;

public class QuoteViewHolder {
    @Bind(R.id.quote_posted_by)
    TextView postedBy;
    @Bind(R.id.quote_message)
    LinearLayout message;

    private final Context context;

    public QuoteViewHolder(Context context, View view) {
        this.context = context;
        ButterKnife.bind(this, view);
    }

    public void setQuote(Quote quote) {
        if (quote.getPostedBy() == null) {
            postedBy.setVisibility(View.GONE);
        } else {
            postedBy.setText(Html.fromHtml(quote.getPostedBy().replace(" View Post", "")));
        }
        Message message = new Message(context, this.message);
        message.setMessage(quote.getMessage());

        final String id = quote.getId();
        if (!TextUtils.isEmpty(id)) {
            postedBy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ThreadActivity) context).onPostSelected(id);
                }
            });
        }
    }
}
