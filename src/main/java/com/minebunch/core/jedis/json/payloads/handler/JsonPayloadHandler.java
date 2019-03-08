package com.minebunch.core.jedis.json.payloads.handler;

import com.google.gson.JsonObject;
import com.minebunch.core.jedis.json.payloads.PayloadType;

public interface JsonPayloadHandler {
    void handlePayload(JsonObject object);

    PayloadType getType();
}
