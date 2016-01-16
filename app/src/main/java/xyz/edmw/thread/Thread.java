package xyz.edmw.thread;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xyz.edmw.post.Post;

public class Thread implements Parcelable {
    private String path;
    private String title;
    private String securityToken;
    private int channelId;
    private int parentId;
    private int pageNum;
    private boolean hasNextPage;
    private List<Post> posts;

    private Thread() {

    }

    protected Thread(Parcel in) {
        path = in.readString();
        title = in.readString();
        securityToken = in.readString();
        channelId = in.readInt();
        parentId = in.readInt();
        pageNum = in.readInt();
        hasNextPage = in.readByte() != 0;
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

    public String getSecurityToken() {
        return securityToken;
    }

    public int getChannelId() {
        return channelId;
    }

    public int getParentId() {
        return parentId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(title);
        dest.writeString(securityToken);
        dest.writeInt(channelId);
        dest.writeInt(parentId);
        dest.writeInt(pageNum);
        dest.writeByte((byte) (hasNextPage ? 1 : 0));
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

    public static class Builder {
        private String path;
        private String title;
        private String securityToken;
        private int channelId;
        private int parentId;
        private int pageNum = 1;
        private boolean hasNextPage;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder securityToken(String securityToken) {
            this.securityToken = securityToken;
            return this;
        }

        public Builder channelId(int channelId) {
            this.channelId = channelId;
            return this;
        }

        public Builder parentId(int parentId) {
            this.parentId = parentId;
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

        public Thread build() {
            Thread thread = new Thread();
            thread.path = path;
            thread.title = title;
            thread.securityToken = securityToken;
            thread.channelId = channelId;
            thread.parentId = parentId;
            thread.pageNum = pageNum;
            thread.hasNextPage = hasNextPage;
            return thread;
        }
    }
}
