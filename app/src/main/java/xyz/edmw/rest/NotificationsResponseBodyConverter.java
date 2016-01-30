package xyz.edmw.rest;

import com.squareup.okhttp.ResponseBody;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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

            List<TextNode> textNodes = messageText.textNodes();
            StringBuilder type = new StringBuilder();
            for (int i = 1; i < textNodes.size()-1; ++i) {
                type.append(textNodes.get(i).text().trim());
            }

            Notification notification = new Notification.Builder()
                    .user(user)
                    .path(path)
                    .title(title)
                    .postDate(postDate)
                    .type(type.toString())
                    .build();
            notifications.add(notification);
        }
        return notifications;
    }
}
