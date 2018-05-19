# gradle-plugin-starter

This project is intended to be a template for gradle plugin projects.

The source code for the plugin itself is in the `/buildSrc` dir.
It is testable live in `/build.gradle`.
Tasks are available to it via the  `:pluginBuild` project, which uses `buildSrc` dirs as source and resource dirs.