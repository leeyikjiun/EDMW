package xyz.edmw.rest;

import com.squareup.okhttp.ResponseBody;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Converter;
import xyz.edmw.thread.Thread;

public class ThreadsResponseBodyConverter implements Converter<ResponseBody, List<Thread>> {

    @Override
    public List<Thread> convert(ResponseBody body) throws IOException {
        String html = body.string();
        return getThreads(html);
    }

    private List<Thread> getThreads(String html) {
        Document doc = Jsoup.parse(html);
        Element topicTab = doc.getElementById("topic-tab");
        Elements rows = topicTab.select("tr.topic-item");

        List<Thread> threads = new ArrayList<>(rows.size());
        for (Element row : rows) {
            Element anchor = row.select("a.topic-title").first();
            String title = anchor.text().trim();
            String path = anchor.attr("href").substring(RestClient.baseUrl.length());
            String lastPost = row.select("td.cell-lastpost").first().text().trim();
            String avatar = row.select("div.topic-avatar").first().getElementsByTag("img").attr("src");
            String startedBy = row.select("div.topic-info").first().text().trim();
            Boolean isSticky = row.hasClass("sticky");

            threads.add(new Thread(title, path, startedBy, lastPost, avatar, isSticky));
        }
        return threads;
    }
}
