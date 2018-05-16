package com.scarlatti.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class GreetingTask extends DefaultTask implements Runnable {
    private String message;
    private String recipient;

    @TaskAction
    @Override
    public void run() {
        System.out.printf("%s, %s!\n", getMessage(), getRecipient());
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }

}