package xyz.edmw.thread;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xyz.edmw.post.Post;

public class Thread implements Parcelable {
    private String id;
    private String path;
    private String title;
    private int pageNum;
    private boolean hasNextPage;
    private List<Post> posts;
    private ReplyForm replyForm;
    private boolean isSubscribed;

    private Thread() {

    }

    protected Thread(Parcel in) {
        id = in.readString();
        path = in.readString();
        title = in.readString();
        pageNum = in.readInt();
        hasNextPage = in.readByte() != 0;
        replyForm = in.readParcelable(ReplyForm.class.getClassLoader());
        isSubscribed = in.readByte() != 0;
    }

    public static final Creator<Thread> CREATOR = new Creator<Thread>() {
        @Override
        public Thread createFromParcel(Parcel in) {
            return new Thread(in);
        }

        @Override
        public Thread[] newArray(int size) {
            return new Thread[size];
        }
    };

    public void addPost(Post post) {
        if (posts == null) {
            posts = new ArrayList<>();
        }
        posts.add(post);
    }

    public List<Post> getPosts() {
        return posts == null ? Collections.<Post>emptyList() : posts;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public boolean hasNextPage() {
        return hasNextPage;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ReplyForm getReplyForm() {
        return replyForm;
    }

    public String getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(path);
        dest.writeString(title);
        dest.writeInt(pageNum);
        dest.writeByte((byte) (hasNextPage ? 1 : 0));
        dest.writeParcelable(replyForm, flags);
        dest.writeByte((byte) (isSubscribed ? 1 : 0));
    }

    public boolean isSubscribed() {
        return isSubscribed;
    }

    public static class Builder {
        private String id;
        private String path;
        private String title;
        private int pageNum = 1;
        private boolean hasNextPage;
        private ReplyForm replyForm;
        private boolean isSubscribed;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder replyForm(ReplyForm replyForm) {
            this.replyForm = replyForm;
            return this;
        }

        public Builder pageNum(int pageNum) {
            this.pageNum = pageNum;
            return this;
        }

        public Builder hasNextPage(boolean hasNextPage) {
            this.hasNextPage = hasNextPage;
            return this;
        }

        public Builder isSubscribed(boolean isSubscribed) {
            this.isSubscribed = isSubscribed;
            return this;
        }

        public Thread build() {
            Thread thread = new Thread();
            thread.id = id;
            thread.path = path;
            thread.title = title;
            thread.pageNum = pageNum;
            thread.hasNextPage = hasNextPage;
            thread.replyForm = replyForm;
            thread.isSubscribed = isSubscribed;
            return thread;
        }
    }
}
