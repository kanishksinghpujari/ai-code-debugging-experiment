package dev.experiment.linkforge.service;

public class AliasAlreadyExistsException extends RuntimeException {
    public AliasAlreadyExistsException(String alias) {
        super("The alias '" + alias + "' is already in use");
    }
}
