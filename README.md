### Overview

The gradle `unity` plugin includes a task that allows users to run a Unity command from gradle.

##### Installation

You must install the plugin to a local or remote artifact repo. To install locally, simple run `gradle install`.

##### Usage

Include the `unity` plugin as a dependency.

```groovy
buildscript {
    repositories {
        mavenLocal()
    }

    dependencies {
        classpath 'com.createar.plugins:unity:0.1.0'
    }
}
```

Next, create a `UnityMethodTask`.

```groovy
/**
 * Builds project.
 */
task buildWebGl(type:com.createar.plugins.unity.UnityMethodTask) {
    projectPath '.'
    method 'Com.MyCompany.BuildTools.BuildWebGlPlayer'
    target 'webgl'
    logPath 'unity.webgl.log'
    username UNITY_USERNAME
    password UNITY_PASSWORD
    serial UNITY_SERIAL
}
```
