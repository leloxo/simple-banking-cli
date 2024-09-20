package com.github.leloxo.bankclient.utils;

public enum TerminalColors {
    RESET("\033[0m"),
    RED("\033[31m"),
    GREEN("\033[32m"),
    YELLOW("\033[33m"),
    BLUE("\033[34m"),
    MAGENTA("\033[35m"),
    CYAN("\033[36m"),
    WHITE("\033[37m");

    private final String code;

    TerminalColors(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

