package com.slackow.endfight.util;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Kit implements Renameable{
    public int[] contents;
    public String name;

    public Kit(String name, int[] contents) {
        this.contents = contents;
        this.name = name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + ";" + Arrays.stream(contents).mapToObj(Integer::toString).collect(Collectors.joining(","));
    }

    public static Kit valueOf(String repr) {
        int index = repr.indexOf(';');
        return new Kit(repr.substring(0, index),
                Arrays.stream(repr.substring(index + 1).split(","))
                        .mapToInt(Integer::parseUnsignedInt).toArray());
    }
}
