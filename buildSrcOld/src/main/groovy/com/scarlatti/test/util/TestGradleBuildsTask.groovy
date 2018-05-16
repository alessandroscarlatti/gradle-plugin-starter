package com.scarlatti.test.util

import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.GradleBuild

import java.nio.file.Paths

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Thursday, 5/3/2018
 *
 * Right now, tests built are executed backwards
 * since they are added to the dependency chain
 * as soon as they are created.  This could be amended
 * with a bit more effort.
 */
class TestGradleBuildsTask extends DefaultTask {

    private int createProjectTaskCount = -1

    /**
     * Sandbox dir is absolute.
     */
    private String sandboxDir = Paths.get(project.projectDir.absolutePath, 'build/sandbox').toString()

    private String pluginRepoDir = Paths.get(project.projectDir.absolutePath, 'build/libs').toString()

    private String gradleTestFileName = 'test.gradle'

    String getSandboxDir() {
        return sandboxDir
    }

    /**
     * Only support relative dirs for now
     * @param sandboxDir
     */
    void setSandboxDir(String sandboxDir) {
        this.sandboxDir = Paths.get(project.projectDir.absolutePath, sandboxDir).toString()
    }

    String getPluginRepoDir() {
        return pluginRepoDir
    }

    /**
     * Only support relative dirs for now
     * @param pluginRepoDir
     */
    void setPluginRepoDir(String pluginRepoDir) {
        this.pluginRepoDir = Paths.get(project.projectDir.absolutePath, pluginRepoDir).toString()
    }

    String getGradleTestFileName() {
        return gradleTestFileName
    }

    void setGradleTestFileName(String gradleTestFileName) {
        this.gradleTestFileName = gradleTestFileName
    }

    private Task createTestTask(String name, String dir) {
        String absoluteDir = getAbsolutePath(dir)
        println "creating task for dir ${absoluteDir}"
        return createTestGradleBuildTask(name, absoluteDir)
    }

    /**
     * For right now, support "template" and "gradle"
     */
    Task test(Map<String, String> props) {
        if (props.get("name") == null) {
            throw new IllegalStateException("Must provide 'name' property for test.")
        }

        if (props.get("template") == null) {
            return createTestTask(props.get("name"), props.get("projectDir"))
        }

        return createTemplateTestGradleBuildTask(props.get("name"), props.get("template"), props.get("gradle"))
    }

    void test(Map<String, String> props, @DelegatesTo(GradleBuild) Closure closure) {
        Task task = test(props)
        closure.setDelegate(task)
        closure()
    }

    /**
     * If gradleFile is null, don't attempt to overwrite in the template
     * @param templateDir right now expected to be relative to the project
     * @param gradleFile right now expected to be relative to the project
     */
    private Task createTemplateTestGradleBuildTask(String name, String templateDir, String gradleFile) {

        String absoluteEventualProjectDir = Paths.get(sandboxDir, templateDir).toString()
        println "eventualProjectDir ${absoluteEventualProjectDir}"

        Task testProjectTask = createTestGradleBuildTask(name, absoluteEventualProjectDir)

        // now we create a dependency for this task that will create the test project from template.
        String absoluteTemplateDir = Paths.get(project.projectDir.absolutePath, templateDir).toString()
        String absoluteGradleFile = Paths.get(project.projectDir.absolutePath, gradleFile).toString()

        Task createProjectTask = createBuildTemplateProjectTask(absoluteTemplateDir, absoluteEventualProjectDir, absoluteGradleFile)
        testProjectTask.dependsOn(createProjectTask)

        return testProjectTask
    }

    /**
     * Create a task that will create a project from a template dir (and optionally a gradle file)
     * @param templateDir absolute path to template directory
     * @param eventualProjectDir absolute path to destination project directory
     * @param gradleFile absolute path to gradle file
     * @return the task created
     */
    private Task createBuildTemplateProjectTask(String templateDir, String eventualProjectDir, String gradleFile) {
        createProjectTaskCount++
        String taskName = "createProject_" + dirToTaskName(templateDir) + "_" + createProjectTaskCount
        return project.tasks.create(taskName, CreateTestProjectTask.class) { task ->
            task.group = 'build'
            task.description = "Build project from template dir ${templateDir}."
            task.templateDir = templateDir
            task.eventualProjectDir = eventualProjectDir
            task.gradleFile = gradleFile
        }
    }

    private Task createTestGradleBuildTask(String name, String rawDir) {
        String pluginName = project.properties.pluginName
        Objects.requireNonNull(pluginName, "pluginName gradle project property may not be null")
        println "Creating task named ${name}"
        String repoDir = Paths.get(pluginRepoDir)
        println "Using plugin repo dir ${repoDir}"
        println "Injecting plugin name '${pluginName}'"

        project.tasks.create(name, GradleBuild.class) { build ->
            build.mustRunAfter(project.tasks.getByName('build'))
            build.group = 'verification'
            build.description = 'Run the test project.'
            build.dir = rawDir
            build.tasks = ['testPluginProject']
            build.startParameter.projectProperties.putAll([
                    group:  project.group,
                    version: project.version,
                    pluginName: pluginName,
                    pluginRepoDir: repoDir,
                    pluginArtifactName: project.properties.pluginArtifactName,
                    gradleTestFileName: gradleTestFileName
            ])
        }

        project.tasks.test.finalizedBy(project.tasks.getByName(name))

        return project.tasks.getByName(name)
    }

    private String dirToTaskName(String rawDir) {
        return Paths.get(rawDir).toString().replace(File.separatorChar, '_' as char).replace(':', '_')
    }

    private String getAbsolutePath(String relativePath) {
        return Paths.get(project.projectDir.absolutePath, relativePath).toString()
    }
}
