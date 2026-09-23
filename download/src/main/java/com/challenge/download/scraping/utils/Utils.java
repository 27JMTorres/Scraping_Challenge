package com.challenge.download.scraping.utils;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Utils {
    public static int TIMEOUT = 120000;
    public static String getResponseDocumentPost(String url, String body, Map<String, String> headers)
            throws IOException {
        OkHttpClient client = null;
        okhttp3.Headers.Builder headerBuilder = new okhttp3.Headers.Builder();
        for (String key : headers.keySet()) {
            headerBuilder.add(key, headers.get(key));
        }
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"), body);
        client = new OkHttpClient().newBuilder().connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS).build();
        Request request = new Request.Builder().url(url).method("POST", requestBody).headers(headerBuilder.build())
                .build();
        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static void ramdomDelay(int min, int max) {
        int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
        try {
            Thread.sleep(randomNum);
        } catch (Exception ex) {
        }
    }

    public static String normalizeString(String s) {
        s = s.trim();
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        s = s.toLowerCase(Locale.ROOT);
        s = s.replace(",", "").replace(" ", "-");
        return s;
    }

    public static double findSimilarity(String x, String y) {
        double maxLength = Double.max(x.length(), y.length());
        if (maxLength > 0) {
            return (maxLength - StringUtils.getLevenshteinDistance(x, y)) / maxLength;
        }
        return 1.0;
    }
}
