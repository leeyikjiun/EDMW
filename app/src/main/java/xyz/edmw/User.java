package xyz.edmw;

public class User {
    private final String name;
    private final String avatar;

    public User(String name, String avatar) {
        this.name = name;
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }
}
