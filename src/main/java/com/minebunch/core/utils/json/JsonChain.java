package com.minebunch.core.utils.json;

import com.google.gson.JsonElement;

public class JsonChain {
    private final com.google.gson.JsonObject json = new com.google.gson.JsonObject();

    public JsonChain addProperty(String property, String value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonChain addProperty(String property, Number value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonChain addProperty(String property, Boolean value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonChain addProperty(String property, Character value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonChain add(String property, JsonElement element) {
        this.json.add(property, element);
        return this;
    }

    public com.google.gson.JsonObject get() {
        return this.json;
    }
}
