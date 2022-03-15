package com.slackow.endfight.util;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Kit {
    public int[] contents;

    public Kit(int[] contents) {
        this.contents = contents;
    }

    @Override
    public String toString() {
        return Arrays.stream(contents).mapToObj(Integer::toString).collect(Collectors.joining(","));
    }

    public static Kit valueOf(String repr) {
        return new Kit(Arrays.stream(repr.split(","))
                        .mapToInt(Integer::parseUnsignedInt).toArray());
    }
}
