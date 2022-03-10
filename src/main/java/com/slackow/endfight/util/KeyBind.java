package com.slackow.endfight.util;

public class KeyBind implements Renameable {
    public String name;
    public int code;
    public String message;

    public KeyBind(String name, int code, String message) {
        this.name = name;
        this.code = code;
        this.message = message;
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
