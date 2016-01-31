package xyz.edmw.notification;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Notifications {
    private List<Notification> notifications;
    private Map<String, String> idsOnPage;
    private Map<String, String> filterParams;
    private String securityToken;

    public Notifications() {

    }

    public Notifications(List<Notification> notifications, Map<String, String> idsOnPage, Map<String, String> filterParams, String securityToken) {
        this.notifications = notifications;
        this.idsOnPage = idsOnPage;
        this.filterParams = filterParams;
        this.securityToken = securityToken;
    }

    public List<Notification> getNotifications() {
        return notifications == null ? Collections.<Notification>emptyList() : notifications;
    }

    public Map<String, String> getFilterParams() {
        return filterParams;
    }

    public Map<String, String> getIdsOnPage() {
        return idsOnPage;
    }

    public String getSecurityToken() {
        return securityToken;
    }
}
