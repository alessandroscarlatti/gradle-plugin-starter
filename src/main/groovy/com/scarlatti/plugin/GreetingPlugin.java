package com.scarlatti.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class GreetingPlugin implements Plugin<Project> {
    public void apply(Project project) {
        project.getTasks().create("ork", GreetingTask.class, (task) -> {
            task.setMessage("Hello");
            task.setRecipient("World...");
        });
    }
}
