package com.challenge.download.scraping.walmart;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.challenge.download.scraping.utils.Utils.getResponseDocumentPost;
import static com.challenge.download.scraping.utils.Utils.ramdomDelay;

public class ScrapingCategories {

    public String scrapCategories() {

        JSONArray jsonCategories = null;

        String body = "";
        try {
            String url = "https://super.walmart.com.mx/orchestra/graphql";
            Map<String, String> headers = new HashMap<>();
            headers.put("accept", "application/json");
            headers.put("x-o-platform", "rweb");
            headers.put("x-o-platform-version", "main-1.26.0-6bd8f9");

            /* Primer POST para traer el nivel 1 y 2 de categorías */
            body = "{\n" +
                    "    \"query\": \"query AllDepartmentsPage($tenant:String! $pageType:String!){contentLayout(channel:\\\"WWW\\\" pageType:$pageType tenant:$tenant){modules{name version type moduleId matchedTrigger{pageId zone inheritable}configs{__typename...on TempoWM_GLASSWWWL2DepartmentsGridConfigs{headingText categories{name{title clickThrough{value}}image{alt src clickThrough{value}}subcategories{subCategoryLink{title clickThrough{value}}}}}}}layouts{id layout}}}\",\n" +
                    "    \"variables\":\n" +
                    "    {\n" +
                    "        \"tenant\": \"MX_GLASS\",\n" +
                    "        \"pageType\": \"AllDepartmentsPage\"\n" +
                    "    }\n" +
                    "}";
            String firstResponse = getResponse(url, body, headers);
            jsonCategories = new JSONObject(firstResponse)
                    .getJSONObject("data")
                    .getJSONObject("contentLayout")
                    .getJSONArray("modules")
                    .getJSONObject(0)
                    .getJSONObject("configs")
                    .getJSONArray("categories");
            /* Fin del proceso primer POST */
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return jsonCategories.toString();
    }

    public String getResponse(String url, String body, Map<String, String> headers) {
        String response = "";
        int tries = 5;
        do {
            tries--;
            try {
                ramdomDelay(60, 80);
                response = getResponseDocumentPost(url, body, headers);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } while ((response.isEmpty() || response.contains("Access Denied")) && tries >= 0);
        return response;
    }
}