package xyz.edmw.quote;

import android.support.annotation.Nullable;

public class Quote {
    private final String id;
    private final String postedBy;
    private final String message;

    public Quote(String id, @Nullable String postedBy, String message) {
        this.id = id;
        this.postedBy = postedBy;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public String getMessage() {
        return message;
    }
}
