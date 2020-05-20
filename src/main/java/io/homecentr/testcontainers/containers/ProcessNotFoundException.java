package io.homecentr.testcontainers.containers;

public class ProcessNotFoundException extends Exception {
    public ProcessNotFoundException(String processName) {
        super("The process '" + processName + "' could not be found in the container.");
    }
}
