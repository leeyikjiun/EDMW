package xyz.edmw.thread;

import android.os.Parcel;
import android.os.Parcelable;

public class ReplyForm implements Parcelable {
    private final String securityToken;
    private final int channelId;
    private final int parentId;

    public ReplyForm(String securityToken, int channelId, int parentId) {
        this.securityToken = securityToken;
        this.channelId = channelId;
        this.parentId = parentId;
    }

    protected ReplyForm(Parcel in) {
        securityToken = in.readString();
        channelId = in.readInt();
        parentId = in.readInt();
    }

    public static final Creator<ReplyForm> CREATOR = new Creator<ReplyForm>() {
        @Override
        public ReplyForm createFromParcel(Parcel in) {
            return new ReplyForm(in);
        }

        @Override
        public ReplyForm[] newArray(int size) {
            return new ReplyForm[size];
        }
    };

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
        dest.writeString(securityToken);
        dest.writeInt(channelId);
        dest.writeInt(parentId);
    }
}
