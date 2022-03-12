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

    @Override
    public String toString() {
        return name + ":" + code + ":" + message;
    }

    public static KeyBind valueOf(String repr) {
        int colon1 = repr.indexOf(':');
        int colon2 = repr.indexOf(':', colon1 + 1);
        return new KeyBind(repr.substring(0, colon1), Integer.parseInt(repr.substring(colon1 + 1, colon2)), repr.substring(colon2 + 1));
    }
}
