package xyz.edmw.rest;

import com.squareup.okhttp.ResponseBody;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import retrofit.Converter;

public class LoginResponseBodyConverter implements Converter<ResponseBody, Boolean> {
    @Override
    public Boolean convert(ResponseBody value) throws IOException {
        Document doc = Jsoup.parse(value.string());
        return doc.getElementById("redirectMessage") != null;
    }
}
