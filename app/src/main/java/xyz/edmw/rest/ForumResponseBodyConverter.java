package xyz.edmw.rest;

import com.squareup.okhttp.ResponseBody;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import retrofit.Converter;
import xyz.edmw.Forum;
import xyz.edmw.User;
import xyz.edmw.topic.Topic;

public class ForumResponseBodyConverter implements Converter<ResponseBody, Forum> {
    // TODO fix this if user changes this value
    private static final int responsesPerPage = 15;

    @Override
    public Forum convert(ResponseBody value) throws IOException {
        String html = value.string();
        return getForum(html);
    }

    private Forum getForum(String html) {
        Document doc = Jsoup.parse(html);
        Element topicTab = doc.getElementById("topic-tab");

        int pageNum;
        try {
            // Forum that has only 1 page will be catch by exception and set pageNum = 1
            pageNum = Integer.parseInt(topicTab.select("a.primary.page").first().text().trim());
        } catch (NullPointerException e) {
            pageNum = 1;
        }
        boolean hasNextPage = !topicTab.select("a.js-pagenav-next-button").first().attr("data-page").equals("0");
        Forum forum = new Forum.Builder()
                .pageNum(pageNum)
                .hasNextPage(hasNextPage)
                .build();

        Elements rows = topicTab.select("tr.topic-item");
        for (Element row : rows) {
            Topic.Builder builder = Topic.Builder.from(row);

            String responses = row.select("div.posts-count").first().text().trim();
            responses = responses.substring(0, responses.indexOf(" response"));
            responses = responses.replace(",", "");
            int numPages = (int) Math.ceil((double) Integer.parseInt(responses) / responsesPerPage);

            forum.addTopic(builder.numPages(numPages).build());
        }

        Element mainNavBar = doc.getElementById("main-navbar");
        Element usernameMenu = mainNavBar.getElementById("lnkUsernameMenu");
        if (usernameMenu != null) {
            String username = usernameMenu.select("span.b-menu__username-label").first().text().trim();
            String avatar = usernameMenu.select("img").first().attr("src").replace("thumb=1", "thumb=0");
            Elements anchors = doc.select("ul.submenu a");
            String profile = anchors.get(0).attr("href");
            String recentPosts = anchors.get(1).attr("href");
            String numNotifications = mainNavBar.select("span.notifications-count").text().trim();
            forum.setUser(new User.Builder()
                    .name(username)
                    .avatar(avatar)
                    .profile(profile)
                    .recentPosts(recentPosts)
                    .numNotifications(Integer.parseInt(numNotifications))
                    .build()
            );
        }

        return forum;
    }
}
