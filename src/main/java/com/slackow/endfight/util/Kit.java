package com.slackow.endfight.util;

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
}
