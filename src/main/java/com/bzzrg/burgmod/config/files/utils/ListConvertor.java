package com.bzzrg.burgmod.config.files.utils;

import com.google.gson.JsonElement;

public interface ListConvertor<T> {
    JsonElement toJson(T obj);
    T fromJson(JsonElement json);
}