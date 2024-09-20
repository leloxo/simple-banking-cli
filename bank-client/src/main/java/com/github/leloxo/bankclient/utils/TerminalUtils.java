package com.github.leloxo.bankclient.utils;

public class TerminalUtils {
    public static String colorize(String message, TerminalColors color) {
        return color.getCode() + message + TerminalColors.RESET.getCode();
    }

    public static String colorizeError(String message) {
        return colorize(message, TerminalColors.RED);
    }

    public static String colorizeSuccess(String message) {
        return colorize(message, TerminalColors.GREEN);
    }

    public static String colorizeTitle(String message) {
        return colorize(message, TerminalColors.BLUE);
    }
}
