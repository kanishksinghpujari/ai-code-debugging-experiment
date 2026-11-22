package dev.experiment.linkforge.service;

public class LinkNotFoundException extends RuntimeException {
    public LinkNotFoundException(String code) {
        super("No active link found for code '" + code + "'");
    }
}
