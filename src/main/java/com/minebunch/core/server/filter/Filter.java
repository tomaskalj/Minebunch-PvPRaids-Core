package com.minebunch.core.server.filter;

import com.google.common.collect.ImmutableList;
import java.util.List;

// TODO: load messages from db? improve detection
public class Filter {
    private static final List<String> FILTERED_WORDS = ImmutableList.of(
            "ddos", "dox", "swat", "nigger"
    );

    public boolean isFiltered(String msg) {
        msg = msg.trim();

        for (String part : msg.split(" ")) {
            if (FILTERED_WORDS.contains(part.toLowerCase())) {
                return true;
            }
        }

        return false;
    }
}
