package xyz.edmw.quote;

import android.support.annotation.Nullable;

public class Quote {
    private final String postedBy;
    private final String message;

    public Quote(@Nullable String postedBy, String message) {
        this.postedBy = postedBy;
        this.message = message;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public String getMessage() {
        return message;
    }
}
