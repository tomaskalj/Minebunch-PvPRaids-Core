package com.minebunch.core.jedis.json.payloads;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JsonPayload {
    private final PayloadType type;
    private final JsonObject data;
}
