package xyz.edmw.post;

public class Post {
    private final String author;
    private final String timestamp;
    private final String postNum;
    private final String message;
    private final String authorAvatar;
    private final String userTitle;

    public Post(String author, String timestamp, String postNum, String message, String authorAvatar, String userTitle) {
        this.author = author;
        this.timestamp = timestamp;
        this.postNum = postNum;
        this.message = message;
        this.authorAvatar = authorAvatar;
        this.userTitle = userTitle;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getPostNum() {
        return postNum;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public String getUserTitle() {
        return userTitle;
    }
}
