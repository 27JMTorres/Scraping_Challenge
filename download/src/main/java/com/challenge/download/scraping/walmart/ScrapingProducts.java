package com.challenge.download.scraping.walmart;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.challenge.download.scraping.utils.Utils.getResponseDocumentPost;
import static com.challenge.download.scraping.utils.Utils.ramdomDelay;
import static com.challenge.download.scraping.walmart.JsonTemplate.bodyCategoryTemplate;
import static com.challenge.download.scraping.walmart.ScrapingURLCategories.getUrl;

public class ScrapingProducts {
    public String scrapProducts(String category){
        List<JSONObject> objs = new ArrayList<>();

        String dcUrl = getUrl(category);

        String templateCategory = bodyCategoryTemplate;
        String urlcategory = "";
        String body = "";
        String response = "";
        JSONObject searchResult = null;
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("accept", "application/json");
            headers.put("x-o-platform", "rweb");
            headers.put("x-o-platform-version", "main-1.26.0-6bd8f9");
            headers.put("x-o-segment", "oaoh");

            urlcategory = getUrlCategory(1, 40, dcUrl);
            body = getBodyCategory(urlcategory, 1, 40, templateCategory);
            response = getResponse(urlcategory, body, headers);
            searchResult = new JSONObject(response)
                    .getJSONObject("data")
                    .getJSONObject("search")
                    .getJSONObject("searchResult");
            int totalElements = searchResult.getInt("aggregatedCount");
            int totalPages = searchResult
                    .getJSONObject("paginationV2")
                    .getInt("maxPage");

            int page = 1;
            int globalPosition = 1;
            while (page <= totalPages) {
                urlcategory = getUrlCategory(page, 40, dcUrl);
                body = getBodyCategory(urlcategory, page, 40, templateCategory);
                response = getResponse(urlcategory, body, headers);
                searchResult = new JSONObject(response)
                        .getJSONObject("data")
                        .getJSONObject("search")
                        .getJSONObject("searchResult");
                JSONArray itemsV2 = searchResult
                        .getJSONArray("itemStacks")
                        .getJSONObject(0)
                        .getJSONArray("itemsV2");

                int position = 1;
                for (int i = 0; i < itemsV2.length(); i++) {
                    JSONObject obj = new JSONObject();

                    /* UPC */
                    String upc = itemsV2.getJSONObject(i)
                            .getString("usItemId");
                    obj.put("UPC", upc);

                    /* SkuDisplayName */
                    String skuDisplayName = itemsV2.getJSONObject(i)
                            .getString("name");
                    obj.put("DisplayName", skuDisplayName);

                    /* BrandName */
                    String brandName = itemsV2.getJSONObject(i)
                            .getString("brand");
                    obj.put("BrandName", brandName);

                    /* ImageURL */
                    String imageUrl = itemsV2.getJSONObject(i)
                            .getJSONObject("imageInfo")
                            .getString("thumbnailUrl");
                    obj.put("ImageURL", imageUrl);

                    String canonicalUrl = itemsV2.getJSONObject(i)
                            .getString("canonicalUrl");
                    obj.put("CanonicalURL", canonicalUrl);

                    /* Status */
                    String availabilityStatusV2 = itemsV2.getJSONObject(i)
                            .getJSONObject("availabilityStatusV2")
                            .getString("value");
                    obj.put("Availability", availabilityStatusV2);

                    /* Price */
                    double currentPrice = 0.00;
                    JSONObject priceInfoObj = itemsV2.getJSONObject(i)
                            .getJSONObject("priceInfo");
                    try {
                        currentPrice = priceInfoObj
                                .getJSONObject("currentPrice")
                                .getDouble("price");
                        obj.put("CurrentPrice", currentPrice);
                    } catch (Exception e) {
                        System.out.println("Producto sin precio:" + skuDisplayName);
                    }

                    double listPrice;
                    Object listPriceContent = priceInfoObj
                            .get("listPrice");
                    if (!listPriceContent.equals(null)) {
                        listPrice = itemsV2.getJSONObject(i)
                                .getJSONObject("priceInfo")
                                .getJSONObject("listPrice")
                                .getDouble("price");
                    } else {
                        listPrice = currentPrice;
                    }
                    obj.put("ListPrice", listPrice);

                    /* ProductSEOUrl */
                    String productSEOUrl = "https://super.walmart.com.mx" + canonicalUrl;
                    obj.put("ProductURL", productSEOUrl);

                    obj.put("Position", position);
                    obj.put("GlobalPosition", globalPosition);
                    obj.put("Page", page);

                    objs.add(obj);

                    position++;
                    globalPosition++;
                }
                page++;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Bad Request";
        }
        return objs.toString();
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

    public String getUrlCategory(int page, int ps, String dcUrl) {
        String url = "";
        try {
            url = dcUrl.replace("(PAGE)", String.valueOf(page))
                    .replace("(PS)", String.valueOf(ps));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return url;
    }

    public String getBodyCategory(String url, int page, int ps, String template) {
        String body = "";
        try {
            body = template.replace("(PAGE)", String.valueOf(page))
                    .replace("(PS)", String.valueOf(ps))
                    .replace("(CATID)", getCatId(url));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return body;
    }

    public String getCatId(String url) {
        String catId = "";
        try {
            URL urlDotNet = new URL(url);
            String query = urlDotNet.getQuery();
            Pattern pattern = Pattern.compile("(?<=catId=)[^&]*");
            Matcher m = pattern.matcher(query);
            while (m.find()) {
                catId = m.group(0);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return catId;
    }
}
