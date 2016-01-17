package xyz.edmw.rest;

import com.squareup.okhttp.ResponseBody;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import retrofit.Converter;
import xyz.edmw.topic.TopicForm;

public class TopicFormResponseBodyConverter implements Converter<ResponseBody, TopicForm> {
    @Override
    public TopicForm convert(ResponseBody value) throws IOException {
        String html = value.string();
        return getTopicForm(html);
    }

    private TopicForm getTopicForm(String html) {
        Document doc = Jsoup.parse(html);
        Element div = doc.getElementById("canvas-layout-full");
        div = div.select("div.b-content-entry").first();

        Element form = div.select("form").first();
        if (form == null) {
            return null;
        }

        String securityToken = form.select("input[name=securitytoken]").first().val();
        String parentId = form.select("input[name=parentid]").first().val();
        return new TopicForm(securityToken, Integer.parseInt(parentId));
    }
}
