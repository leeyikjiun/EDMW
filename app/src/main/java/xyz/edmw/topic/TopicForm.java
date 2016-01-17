package xyz.edmw.topic;

public class TopicForm {
    private final String securityToken;
    private final int parentId;

    public TopicForm(String securityToken, int parentId) {
        this.securityToken = securityToken;
        this.parentId = parentId;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public int getParentId() {
        return parentId;
    }
}
