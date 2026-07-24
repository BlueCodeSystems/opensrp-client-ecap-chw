package com.bluecodeltd.ecap.chw.configs;

import com.bluecodeltd.ecap.chw.BuildConfig;

public class Config {
    public static final String BASEURL = BuildConfig.DIRECTUS_BASE_URL.endsWith("/")
            ? BuildConfig.DIRECTUS_BASE_URL
            : BuildConfig.DIRECTUS_BASE_URL + "/";
    public static final String MOBILE_DEVICES_URL = BuildConfig.MOBILE_DEVICES_URL;
    public static final String ITEMURL = "/items/" + BuildConfig.DIRECTUS_FLAGS_COLLECTION;
    public static final String CASEWORKER_FIELD = BuildConfig.DIRECTUS_FLAGS_CASEWORKER_FIELD;
    public static final String AUTH_TOKEN = BuildConfig.DIRECTUS_FLAGS_AUTH_TOKEN;
}
