package org.cs250.nan.backend.shell;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class ShellCommands {

    // Basic command for testing
    @ShellMethod(value = "Print a greeting message", key = "hello")
    public String hello(@ShellOption(defaultValue = "User") String name) {
        return String.format("Hello, %s!", name);
    }
}
