package com.scarlatti

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Saturday, 5/19/2018
 */
class OrkTaskTest extends Specification {

    @Rule TemporaryFolder tempDir = new TemporaryFolder()
    File buildFile

    def setup() {
        buildFile = tempDir.newFile('build.gradle')
    }

    def "ork task prints message"() {
        given:
            buildFile << """
            plugins {
                id 'greeting'
            }

            ork {
                message = 'what do you know2'
            }
        """

        when:
            def result = GradleRunner.create()
                    .withProjectDir(tempDir.root)
                    .withArguments('ork')
                    .withPluginClasspath()
                    .withDebug(true)
                    .build()

        then:
            println result.output
            result.output.contains('what do you know2')
            result.task(":ork").outcome == TaskOutcome.SUCCESS
    }
}
