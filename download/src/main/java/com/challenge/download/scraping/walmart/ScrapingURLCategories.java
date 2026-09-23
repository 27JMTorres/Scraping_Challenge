package com.challenge.download.scraping.walmart;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.challenge.download.scraping.utils.Utils.*;

public class ScrapingURLCategories {

    public static String template = "https://super.walmart.com.mx/orchestra/graphql/browse?" +
            "page=(PAGE)&" +
            "prg=desktop&" +
            "catId=(CATID)&" +
            "sort=best_match&" +
            "ps=(PS)&" +
            "additionalQueryParams.isMoreOptionsTileEnabled=true&" +
            "searchArgs.cat_id=(CATID)&" +
            "searchArgs.prg=desktop&" +
            "fitmentFieldParams=true&" +
            "enableFashionTopNav=false&" +
            "fetchMarquee=true&" +
            "fetchSkyline=true&" +
            "fetchSbaTop=false&" +
            "fetchGallery=false&" +
            "enablePortableFacets=false&" +
            "tenant=MX_GLASS&" +
            "enableFacetCount=true&" +
            "marketSpecificParams={\"banner\":\"od\",\"pageType\":\"browse\",\"locale\":\"es_MX\"}&enableFlattenedFitment=false";

    public static String getUrl(String category) {
        String url = scrapUrlCategories(category);
        if (!url.isEmpty()) {
            return url;
        } else {
            return null;
        }
    }

    public static String scrapUrlCategories(String category) {
        String body = "";
        try {
            String[] splitCat = category.split("/");
            String level1 = null;
            String level2 = null;
            String level3 = null;
            try {
                level1 = splitCat[1];
                level2 = splitCat[2];
                level3 = splitCat[3];
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (level1.isEmpty() || level2.isEmpty() || level3.isEmpty()) {
                throw new Exception("");
            }

            String url = "https://super.walmart.com.mx/orchestra/graphql";
            ;
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
            JSONArray jsonCategories = new JSONObject(firstResponse)
                    .getJSONObject("data")
                    .getJSONObject("contentLayout")
                    .getJSONArray("modules")
                    .getJSONObject(0)
                    .getJSONObject("configs")
                    .getJSONArray("categories");
            for (int i = 0; i < jsonCategories.length(); i++) {
                /******** Level 1 value *********/
                String title1 = jsonCategories.getJSONObject(i)
                        .getJSONObject("name")
                        .getString("title");
                String title1Normalized = normalizeString(title1);
                System.out.println("Level 1: " + title1);
                /********************************/
                if (title1Normalized.equals(level1) || findSimilarity(title1Normalized, level1) > 0.9) {
                    JSONArray jsonSubCategories = jsonCategories.getJSONObject(i).getJSONArray("subcategories");
                    for (int j = 0; j < jsonSubCategories.length(); j++) {
                        /******** Level 2 value *********/
                        JSONObject subCatObj = jsonSubCategories.getJSONObject(j)
                                .getJSONObject("subCategoryLink");
                        String title2 = subCatObj
                                .getString("title");
                        String title2Normalized = normalizeString(title2);

                        String value2 = subCatObj
                                .getJSONObject("clickThrough")
                                .getString("value");
                        String[] split = value2.split("/");
                        String subCatId = split[split.length - 1];

                        System.out.println("    Level 2: " + title2Normalized);
                        /********************************/
                        if (title2Normalized.equals(level2) || findSimilarity(title2Normalized, level2) > 0.9) {
                            /* Primer POST para traer el nivel 3 de categorías */
                            body = "{\n" +
                                    "    \"query\": \"query ContentPage( $p13n:JSON $categoryPageId:String! $tenant:String! $layout:String! $isTopNavEnabled:Boolean! $pageType:String! $marketSpecificParams:String $fitmentFieldParams:JSON ={}){contentLayout( channel:\\\"WWW\\\" pageType:$pageType tenant:$tenant version:\\\"v1\\\" ){host upstreamStatus modules(p13n:$p13n tempo:{category:$categoryPageId}){name type version status schedule{start end priority expEnabled}triggers{pageType pageId zone inheritable}configs{__typename...on EnricherModuleConfigsV1{zoneV1}...on TempoWM_GLASSWWWSponsoredProductCarouselConfigs{_rawConfigs}...BreadcrumbFragment...SavedRewardsFragment...PageTitleFragment...HeroPOVFragment...HubSpoke4x1Fragment...HubSpoke4x4Fragment...HubSpokeNxMFragment...POVCardsFragment...ItemCarouselFragment...InspirationFragment...LeftHandNavigationFragment...ResponsiveSideNavigationFragment...StaticNavigationFragment...CustomHtmlFragment...EkoVideoFragment...FaqFragment...RegistryBenefitsFragment...SkinnyBanner...CopyBlockFragment...DepartmentsGridFragment...YourRewardsFragment...TopNavFragment @include(if:$isTopNavEnabled)...HorizontalChipModuleFragment...InspirationalVideoFragment...FitmentModuleFragment...OneAndTwoSkuFragment...LeadFormFragment}publishedDate moduleId module_id matchedTrigger{pageType pageId zone inheritable}}layouts(layout:[$layout]){id layout}pageMetadata{location{pickupStore deliveryStore intent postalCode stateOrProvinceCode city storeId accessPointId accessType spokeNodeId}pageContext}}seoCategoryMetaData( id:$categoryPageId marketSpecificParams:$marketSpecificParams ){metaDesc metaTitle metaCanon metaKW noIndex}}fragment HeroPOVFragment on TempoWM_GLASSWWWHeroPovConfigsV1{autoRotation p13nCards:p13nCardsV1{...P13nHeroPOVCardFragment}povCards{card:cardV1{...CLSHeroPOVCardFragment}}}fragment P13nHeroPOVCardFragment on CLETempoWM_GLASSWWWHeroPovConfigsCards{povStyle ctaButton{button{...TempoCommonLinkFragment}textColor ctaButtonBackgroundColor}heading{text textSize textColor textColorMobile textFontWeight}subheading{text textColor textColorMobile}image{mobileImage{...TempoCommonImageFragment}desktopImage{...TempoCommonImageFragment}}eyebrow{text textColor textColorMobile textFontWeight}detailsView{backgroundColor alignment isTransparent}links{link{...TempoCommonLinkFragment}textColor textColorMobile}legalDisclosure{regularText shortenedText textColor textColorMobile legalBottomSheetTitle legalBottomSheetDescription}sponsoredLabel{text textColor textColorMobile}logo{...TempoCommonImageFragment}}fragment CLSHeroPOVCardFragment on CLSTempoWM_GLASSWWWHeroPovConfigsCards{povStyle ctaButton{button{...TempoCommonLinkFragment}textColor ctaButtonBackgroundColor}heading{text textSize textColor textColorMobile textFontWeight}subheading{text textColor textColorMobile}image{mobileImage{...TempoCommonImageFragment}desktopImage{...TempoCommonImageFragment}}eyebrow{text textColor textColorMobile textFontWeight}detailsView{backgroundColor alignment isTransparent}links{link{...TempoCommonLinkFragment}textColor textColorMobile}legalDisclosure{regularText shortenedText textColor textColorMobile legalBottomSheetTitle legalBottomSheetDescription}sponsoredLabel{text textColor textColorMobile}logo{...TempoCommonImageFragment}}fragment TempoCommonImageFragment on TempoCommonImage{src alt assetId uid clickThrough{value}}fragment TempoCommonLinkFragment on TempoCommonStringLink{linkText title uid clickThrough{value}}fragment SavedRewardsFragment on TempoWM_GLASSWWWSavedRewardsConfigsV1{title viewAllLink{linkText clickThrough{value}}potentialEarnings additionalItems productItems{name imageInfo{thumbnailUrl}}}fragment PageTitleFragment on TempoWM_GLASSWWWPageTitleConfigsV1{pageTitle}fragment HubSpoke4x1Fragment on TempoWM_GLASSWWWHubSpokes4x1Configs{headingText categories4x1:categories{name image{alt assetId src clickThrough{value}}}}fragment HubSpoke4x4Fragment on TempoWM_GLASSWWWHubSpokes4x4Configs{headingText displayMode rows{categories4x4:categories{name image{alt assetId src clickThrough{value}}}}}fragment HubSpokeNxMFragment on TempoWM_GLASSWWWHubSpokesNxMConfigs{headingText:heading colNumber rows4{__typename categories{name image{alt assetId src clickThrough{value}}}}rows6{__typename categories{name image{alt assetId src clickThrough{value}}}}}fragment POVCardsFragment on TempoWM_GLASSWWWPOVCardsConfigsV1{headingText viewAllLink{clickThrough{value}linkText}imageAspectRatio cards{headingText descriptionText image{alt assetId src width height}link{clickThrough{value}linkText}}}fragment ItemCarouselFragment on TempoWM_GLASSWWWItemCarouselConfigsV1{type title subTitle viewAllLink{linkText title clickThrough{type value}}spBeaconInfo{adUuid moduleInfo pageViewUUID placement max}tileOptions{productTitle}productsConfig{products{...ProductItemCarouselFragment}}}fragment ProductItemCarouselFragment on Product{usItemId sellerId sellerName hasSellerBadge offerId fulfillmentSpeed fulfillmentType unitQuantity type salesUnitType weightIncrement itemType groupMetaData{groupType groupSubType numberOfComponents groupComponents{quantity offerId componentType productDisplayName}}priceInfo{...ProductPriceInfoFragment}preOrder{...PreorderFragment}rewards{state selectionToken rewardAmt minQuantity cbOffer eligible}canonicalUrl numberOfReviews averageRating availabilityStatus showAtc imageInfo{thumbnailUrl allImages{id url}}name fulfillmentBadge badges{flags{...on BaseBadge{key text type id rank}...on PreviouslyPurchasedBadge{key text id rank}}labels{key text}tags{key text id rank}}p13nDataV1{predictedQuantity flags{PREVIOUSLY_PURCHASED{text}CUSTOMERS_PICK{text}}labels{PREVIOUSLY_PURCHASED{text}CUSTOMERS_PICK{text}}}sponsoredProduct{spQs clickBeacon spTags}classType variantCount brand}fragment ProductPriceInfoFragment on ProductPriceInfo{priceDisplayCodes{rollback reducedPrice clearance strikethrough submapType priceDisplayCondition}currentPrice{price priceString priceDisplay}wasPrice{price priceString}priceRange{minPrice maxPrice priceString}unitPrice{price priceString}subscriptionPrice{priceString}savings{amount percent priceString}comparisonPrice{price priceString priceType}}fragment PreorderFragment on PreOrder{streetDate streetDateDisplayable streetDateType isPreOrder preOrderMessage preOrderStreetDateMessage}fragment InspirationFragment on TempoWM_GLASSWWWInspirationModuleConfigs{mainHeading inspirationModule{cardImage{alt assetId assetName clickThrough{type value rawValue tag}height src title width size contentType uid}cardTitle cardSubTitle itemNumber twoItems threeItems ctaInfo{uid clickThrough{type value rawValue tag}}products{usItemId salesUnitType offerId classType badges{flags{key text}labels{key text}tags{key text}}priceInfo{priceDisplayCodes{rollback reducedPrice eligibleForAssociateDiscount clearance strikethrough submapType priceDisplayCondition unitOfMeasure pricePerUnitUom}currentPrice{price priceString priceDisplay}wasPrice{price priceString}unitPrice{price priceString}priceRange{minPrice maxPrice priceString}savings{amount percent priceString}comparisonPrice{price priceString priceType}}availabilityStatus showAtc showOptions imageInfo{thumbnailUrl}canonicalUrl name departmentName brand weightIncrement fulfillmentBadge fulfillmentSpeed fulfillmentType averageRating numberOfReviews sponsoredProduct{spQs clickBeacon}p13nDataV1{predictedQuantity flags{PREVIOUSLY_PURCHASED{text}CUSTOMERS_PICK{text}}labels{PREVIOUSLY_PURCHASED{text}CUSTOMERS_PICK{text}}}}}}fragment LeftHandNavigationFragment on TempoWM_GLASSWWWCategoryLeftHandNavConfigs{title categories{name image{alt src clickThrough{type value}}subcategories{subCategoryLink{linkText title clickThrough{type value}}openInNewTab}}}fragment ResponsiveSideNavigationFragment on TempoWM_GLASSWWWHWSideNavigationConfigs{title categories{categoryLink{clickThrough{type value}title}openInNewTab defaultImage:image{src alt title}activeImage{src alt title}subcategories{subcategoryLink{clickThrough{type value}title}openInNewTab}}}fragment BreadcrumbFragment on TempoWM_GLASSWWWBreadcrumbConfigs{breadCrumb{id name url}}fragment StaticNavigationFragment on TempoWM_GLASSWWWStaticNavigationPillsConfigs{NavPills{title description url{clickThrough{value rawValue}}}}fragment CustomHtmlFragment on TempoWM_GLASSWWWCustomHtmlConfigs{markup products{...customHtmlProductFragment}}fragment customHtmlProductFragment on Product{usItemId sellerId sellerName hasSellerBadge offerId fulfillmentSpeed fulfillmentType unitQuantity type salesUnitType weightIncrement itemType groupMetaData{groupType groupSubType numberOfComponents groupComponents{quantity offerId componentType productDisplayName}}priceInfo{priceDisplayCodes{rollback reducedPrice clearance strikethrough submapType priceDisplayCondition}currentPrice{price priceString}wasPrice{price priceString}priceRange{minPrice maxPrice priceString}unitPrice{price priceString}subscriptionPrice{priceString}}preOrder{streetDate streetDateDisplayable streetDateType isPreOrder preOrderMessage preOrderStreetDateMessage}rewards{state selectionToken rewardAmt minQuantity cbOffer eligible}canonicalUrl numberOfReviews averageRating availabilityStatus showAtc imageInfo{thumbnailUrl allImages{id url}}name fulfillmentBadge badges{flags{key text id rank}labels{key text}}p13nDataV1{predictedQuantity flags{PREVIOUSLY_PURCHASED{text}CUSTOMERS_PICK{text}}labels{PREVIOUSLY_PURCHASED{text}CUSTOMERS_PICK{text}}}sponsoredProduct{spQs clickBeacon spTags}classType variantCount}fragment EkoVideoFragment on TempoWM_GLASSWWWEkoVideoContainerConfigs{ekoVideoSrc ekoVideoTitle:title allowFullScreen ekoType}fragment FaqFragment on TempoWM_GLASSWWWFAQConfigs{title faqList{questionText answerParagraphs{paragraph}}}fragment RegistryBenefitsFragment on TempoWM_GLASSWWWRegistryBenefitsModuleConfigs{variant content{image{src}heading description link{linkText title clickThrough{value}}}}fragment SkinnyBanner on TempoWM_GLASSWWWSkinnyBannerConfigs{bannerType desktopBannerHeight bannerImage{src title alt}mobileBannerHeight mobileImage{src title alt}backgroundColor heading{title fontColor}subHeading{title fontColor}bannerCta{textColor uid ctaType ctaLink{title linkText clickThrough{value rawValue}}}}fragment CopyBlockFragment on TempoWM_GLASSWWWGenericCopyBlockConfigs{catCopyBlock( id:$categoryPageId pageType:\\\"ContentPage\\\" marketSpecificParams:$marketSpecificParams )}fragment DepartmentsGridFragment on TempoWM_GLASSWWWDepartmentsGridConfigsV1{headingText viewAllLink{title uid clickThrough{value}}categoriesHome{name image{alt src assetId clickThrough{value}}}}fragment YourRewardsFragment on TempoWM_GLASSWWWYourRewardsConfigsV1{title rewardsBalance{balanceHeading balanceDescription balanceAmount}lifetimeEarnings{earningsHeading earningsDescription earningsAmount}image{mobileImage{src}desktopImage{src}uid}membershipStatus walmartPlusCashRewards{walmartPlusLogo{alt assetId assetName height src title width size uid}walmartPlusCashRewardsBanner{cashRewardsHeading cashRewardsSubheading ctaLink{title linkText clickThrough{rawValue tag type value}}}itemRewards{itemRewardsIcon{alt assetId assetName height src title width size contentType uid}itemRewardsText itemRewardsAmount}cashRewards{cashRewardsIcon{alt assetId assetName height src title width size contentType uid}cashRewardsText cashRewardsAmount}}}fragment TopNavFragment on TempoWM_GLASSWWWCategoryTopNavConfigs{navHeaders{header{linkText clickThrough{value}}headerImageGroup{headerImage{alt src}imgTitle imgSubText imgLink{linkText title clickThrough{value}}}categoryGroup{category{linkText clickThrough{value}}startNewColumn subCategoryGroup{subCategory{linkText clickThrough{value}}isBold openInNewTab}}}}fragment HorizontalChipModuleFragment on TempoWM_GLASSWWWHorizontalChipModuleConfigs{moduleSource chipModule{title url{linkText title clickThrough{type value}}}chipModuleWithImages{title url{linkText title clickThrough{type value}}image{alt src}}}fragment InspirationalVideoFragment on TempoWM_GLASSWWWInspirationalVideoConfigs{title subTitle videoLink card:cards{...VideoCardFragment}}fragment VideoCardFragment on TempoWM_GLASSWWWInspirationalVideoConfigsCards{headingText subHeadingText cta{clickThrough{value}linkText title uid}}fragment FitmentModuleFragment on TempoWM_GLASSWWWSearchFitmentModuleConfigs{fitments(fitmentFieldParams:$fitmentFieldParams){partTypeIDs redirectUrl{title clickThrough{value}}result{status formId position quantityTitle extendedAttributes{...FitmentFieldFragment}labels{...LabelFragment}resultSubTitle}labels{...LabelFragment}savedVehicle{vehicleType{...VehicleFieldFragment}vehicleYear{...VehicleFieldFragment}vehicleMake{...VehicleFieldFragment}vehicleModel{...VehicleFieldFragment}additionalAttributes{...VehicleFieldFragment}}fitmentFields{...VehicleFieldFragment}fitmentForms{id fields{...FitmentFieldFragment}title labels{...LabelFragment}}}}fragment LabelFragment on FitmentLabels{ctas{...FitmentLabelEntityFragment}messages{...FitmentLabelEntityFragment}links{...FitmentLabelEntityFragment}images{...FitmentLabelEntityFragment}}fragment FitmentLabelEntityFragment on FitmentLabelEntity{id label labelV1}fragment VehicleFieldFragment on FitmentVehicleField{id label value}fragment FitmentFieldFragment on FitmentField{id displayName value extended data{value label}dependsOn}fragment OneAndTwoSkuFragment on TempoWM_GLASSWWWStockKeepingUnitConfigs{oneAndTwoSkuCtaLink:ctaLink{title linkText clickThrough{rawValue tag type value}}oneAndTwoSkuImage:image{alt title src uid clickThrough{rawValue tag type value}}imageAlignment items products{usItemId salesUnitType offerId classType badges{flags{key text}labels{key text}tags{key text}}priceInfo{priceDisplayCodes{rollback reducedPrice eligibleForAssociateDiscount clearance strikethrough submapType priceDisplayCondition unitOfMeasure pricePerUnitUom}currentPrice{price priceString priceDisplay}wasPrice{price priceString}unitPrice{price priceString}priceRange{minPrice maxPrice priceString}savings{amount percent priceString}comparisonPrice{price priceString priceType}}availabilityStatus showAtc showOptions imageInfo{thumbnailUrl}canonicalUrl name departmentName brand weightIncrement fulfillmentBadge fulfillmentSpeed fulfillmentType averageRating numberOfReviews sponsoredProduct{spQs clickBeacon}p13nDataV1{predictedQuantity flags{PREVIOUSLY_PURCHASED{text}CUSTOMERS_PICK{text}}labels{PREVIOUSLY_PURCHASED{text}CUSTOMERS_PICK{text}}}}subCopy title}fragment LeadFormFragment on TempoWM_GLASSWWWLeadFormConfigs{__typename entry{__typename header markup ctaText image{__typename src alt title}bgColor}form{__typename input{__typename type label placeholder error validationRegex responseKey maxCharacters}}confirmation{__typename header markup cta{__typename refresh dismiss refreshActive}}disclaimer{__typename markup}}\",\n" +
                                    "    \"variables\":\n" +
                                    "    {\n" +
                                    "        \"p13n\":\n" +
                                    "        {\n" +
                                    "            \"userClientInfo\":\n" +
                                    "            {\n" +
                                    "                \"deviceType\": \"desktop\"\n" +
                                    "            }\n" +
                                    "        },\n" +
                                    "        \"categoryPageId\": \"" + subCatId + "\",\n" +
                                    "        \"tenant\": \"MX_GLASS\",\n" +
                                    "        \"layout\": \"\",\n" +
                                    "        \"isTopNavEnabled\": true,\n" +
                                    "        \"pageType\": \"ContentPage\",\n" +
                                    "        \"fitmentFieldParams\":\n" +
                                    "        {\n" +
                                    "            \"powerSportEnabled\": true,\n" +
                                    "            \"pageType\": \"ContentPage\"\n" +
                                    "        },\n" +
                                    "        \"marketSpecificParams\": \"{\\\"banner\\\":\\\"od\\\",\\\"pageType\\\":\\\"cp\\\",\\\"locale\\\":\\\"es_MX\\\"}\"\n" +
                                    "    }\n" +
                                    "}";
                            String secondResponse = getResponse(url, body, headers);
                            JSONArray modules = new JSONObject(secondResponse)
                                    .getJSONObject("data")
                                    .getJSONObject("contentLayout")
                                    .getJSONArray("modules");

                            if (modules.length() > 0) {
                                JSONArray jsonSubCatsLvl3 = modules
                                        .getJSONObject(modules.length() - 1)
                                        .getJSONObject("configs")
                                        .getJSONArray("NavPills");
                                for (int k = 0; k < jsonSubCatsLvl3.length(); k++) {
                                    /******** Level 3 value *********/
                                    String title3 = jsonSubCatsLvl3.getJSONObject(k).getString("title");
                                    String title3Normalized = normalizeString(title3);
                                    System.out.println("        Level 3: " + title3Normalized);
                                    String cat = "/mismo-dia/" +
                                            normalizeString(title1) + "/" +
                                            normalizeString(title2) + "/" +
                                            normalizeString(title3) + "/";

                                    String value3 = jsonSubCatsLvl3.getJSONObject(k)
                                            .getJSONObject("url")
                                            .getJSONObject("clickThrough")
                                            .getString("value");
                                    String[] splitValue3 = value3.split("/");

                                    /********************************/
                                    if (title3Normalized.equals(level3) || findSimilarity(title3Normalized, level3) > 0.9) {
                                        String urlCat = template;
                                        urlCat = urlCat.replace("(CATID)", splitValue3[splitValue3.length - 1]);
                                        return urlCat;
                                    }
                                }
                                throw new Exception("Sin categoria relacionada");
                            } else {
                                System.out.println("    Level 2: " + title2Normalized + " id: " + subCatId + " sin resultado");
                            }
                            /* Fin del proceso segundo POST */
                        }
                    }
                }
            }
            /* Fin del proceso primer POST */
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public static String getResponse(String url, String body, Map<String, String> headers) {
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