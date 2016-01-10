package xyz.edmw.rest;

import com.squareup.okhttp.ResponseBody;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Converter;
import retrofit.Response;
import retrofit.Retrofit;
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
            boolean isSticky = row.hasClass("sticky");

            final Thread thread = new Thread(title, path, startedBy, lastPost, avatar, isSticky);
            threads.add(thread);

            String lastPostPath = row.select("a.go-to-last-post").first().attr("href");
            lastPostPath = lastPostPath.substring(RestClient.baseUrl.length());
            Call<Void> call = RestClient.getService().getNumPages(lastPostPath);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Response<Void> response, Retrofit retrofit) {
                    String url = response.raw().request().urlString();
                    int index = url.lastIndexOf("/page");
                    int numPages = 1;
                    if (index >= 0) {
                        url = url.substring(index + 5, url.lastIndexOf("#post"));
                        numPages = Integer.parseInt(url);
                    }
                    thread.setNumPages(numPages);
                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                    thread.setNumPages(15);
                }
            });


        }
        return threads;
    }
}
