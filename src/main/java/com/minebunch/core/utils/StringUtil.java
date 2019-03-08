package com.minebunch.core.utils;

import java.util.Arrays;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtil {
    public final Pattern URL_REGEX = Pattern.compile("^(http://www\\.|https://www\\.|http://|https://)?[a-z0-9]+([\\-.][a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(/.*)?$");
    public final Pattern IP_REGEX = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])([.,])){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");

    public String buildString(String[] args, int start) {
        return String.join(" ", Arrays.copyOfRange(args, start, args.length));
    }

    public String getCorrectArticle(String input) {
        char c = input.toLowerCase().charAt(0);
        boolean aOrAn = c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u';
        return aOrAn ? "an" : "a";
    }
}
