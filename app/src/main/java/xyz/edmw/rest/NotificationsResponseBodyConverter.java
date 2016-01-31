package xyz.edmw.rest;

import android.text.TextUtils;

import com.squareup.okhttp.ResponseBody;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Converter;
import xyz.edmw.User;
import xyz.edmw.notification.Notification;
import xyz.edmw.notification.Notifications;

public class NotificationsResponseBodyConverter implements Converter<ResponseBody, Notifications> {
    @Override
    public Notifications convert(ResponseBody value) throws IOException {
        String html = value.string();
        return getNotifications(html);
    }

    private Notifications getNotifications(String html) {
        Document doc = Jsoup.parse(html);
        Element notificationContent = doc.getElementById("notificationContent");
        Elements messages = notificationContent.select("li.list-item.message-item");

        Map<String, String> idsOnPage = new HashMap<>();
        List<Notification> notifications = new ArrayList<>(messages.size());
        for (Element message : messages) {
            String avatar = message.select("a.avatar img").attr("src").trim();

            Element messageText = message.select("div.notification-message-text").first();
            Element a = messageText.select("a.user-profile.author").first();
            String username = a.text().trim();
            String profile = a.attr("href").trim();

            User user = new User.Builder()
                    .avatar(avatar)
                    .profile(profile)
                    .name(username)
                    .build();

            a = messageText.select("a.post-title").first();
            String path = a.attr("href").trim();
            path = path.substring(RestClient.baseUrl.length());
            String title = a.text().trim();
            String postDate = message.select("div.post-date").first().text().trim();

            List<Node> nodes = messageText.childNodes();
            StringBuilder type = new StringBuilder();
            for (int i = 2; i < nodes.size()-2; ++i) {
                Node node = nodes.get(i);
                if (node instanceof TextNode) {
                    type.append(((TextNode) node).text());
                }else if (node instanceof Element) {
                    type.append(((Element) node).text());
                }
            }

            String id = message.select("button[id^=notificationBtnDelete_]").attr("data-notificationid");
            idsOnPage.put(id, id);

            Notification notification = new Notification.Builder()
                    .id(id)
                    .user(user)
                    .path(path)
                    .title(title)
                    .postDate(postDate)
                    .type(type.toString().trim())
                    .build();
            notifications.add(notification);
        }

        Map<String, String> filterParams = new HashMap<>();
        Element notificationfilters = doc.getElementById("notificationFilters");
        for (Element e : notificationfilters.select("input[type=hidden]")) {
            String val = e.val();
            if (!TextUtils.isEmpty(val)) {
                filterParams.put(e.attr("name"), val);
            }
        }
        for (Element e : notificationfilters.select("input[type=radio]")) {
            if (e.attr("checked").equals("checked")) {
                String val = e.val();
                if (!TextUtils.isEmpty(val)) {
                    filterParams.put(e.attr("name"), val);
                }
            }
        }
        String securitytoken = doc.select("input[name=securitytoken]").first().val();
        return new Notifications(notifications, idsOnPage, filterParams, securitytoken);
    }
}
