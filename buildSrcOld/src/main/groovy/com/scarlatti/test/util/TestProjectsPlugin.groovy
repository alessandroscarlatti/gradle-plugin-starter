package com.scarlatti.test.util

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.GradleBuild

import java.nio.file.Paths

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Thursday, 5/3/2018
 */
class TestProjectsPlugin implements Plugin<Project> {

    private Project project

    @Override
    void apply(Project project) {
        // read a list of test project dirs from the
        // gradle project property
        this.project = project

        createTestPluginProjectTask()
    }

    private void createTestPluginProjectTask() {
        project.tasks.create("testProjects", TestGradleBuildsTask.class) { task ->
            task.group = 'verification'
            task.description = 'Tests this plugin in test projects.'
            task.mustRunAfter(project.tasks.getByName("build"))
            task.outputs.upToDateWhen {false}
        }

        project.tasks.getByName("test").finalizedBy(project.tasks.getByName("testProjects"))
    }
}
