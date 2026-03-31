package com.bzzrg.burgmod.config.files.utils;

import com.google.gson.JsonElement;

public interface JsonConvertor {
    JsonElement getJson();
    void setFields(JsonElement json);
}