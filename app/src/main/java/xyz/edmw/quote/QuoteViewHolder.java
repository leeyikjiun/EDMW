package xyz.edmw.quote;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.edmw.Message;
import xyz.edmw.R;

public class QuoteViewHolder {
    @Bind(R.id.linear_layout)
    LinearLayout layout;
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
            layout.removeView(postedBy);
        } else {
            postedBy.setText(Html.fromHtml(quote.getPostedBy()));
        }
        Message message = new Message(context, this.message);
        message.setMessage(quote.getMessage());
    }
}
