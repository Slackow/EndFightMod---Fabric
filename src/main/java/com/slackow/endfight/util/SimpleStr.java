package com.slackow.endfight.util;

public class SimpleStr implements Renameable {
    String str;
    @Override
    public void setName(String name) {
        str = name;
    }

    @Override
    public String getName() {
        return str;
    }
    public SimpleStr(String str) {
        this.str = str;
    }
}