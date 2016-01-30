package xyz.edmw.rest;

import android.util.Log;

import com.squareup.okhttp.ResponseBody;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Converter;
import xyz.edmw.User;
import xyz.edmw.notification.Notification;

public class NotificationsResponseBodyConverter implements Converter<ResponseBody, List<Notification>> {
    @Override
    public List<Notification> convert(ResponseBody value) throws IOException {
        String html = value.string();
        return getNotifications(html);
    }

    private List<Notification> getNotifications(String html) {
        Document doc = Jsoup.parse(html);
        Element notificationContent = doc.getElementById("notificationContent");
        Elements messages = notificationContent.select("li.list-item.message-item");

        List<Notification> notifications = new ArrayList<>(messages.size());
        for (Element message : messages) {
            String avatar = message.select("a.avatar img").first().attr("src").trim();

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

            Notification notification = new Notification.Builder()
                    .user(user)
                    .path(path)
                    .title(title)
                    .postDate(postDate)
                    .type(type.toString().trim())
                    .build();
            notifications.add(notification);
        }
        return notifications;
    }
}
