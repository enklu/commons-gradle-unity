package com.createar.plugins.unity;

import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.GradleException

import com.createar.plugins.unity.LogWatcher
import com.createar.plugins.unity.StdoutLogWatcherDelegate
import com.createar.plugins.unity.RegexMatchLogWatcherDelegate

/**
 * Task for building Unity projects.
 */
public class UnityMethodTask extends Exec {

    /**
     * Optional path to unity binary.
     */
    @Input
    @Optional
    String unityBin

    /**
     * Username to sign in with.
     */
    @Input
    @Optional
    String username

    /**
     * Password to sign in with.
     */
    @Input
    @Optional
    String password

    /**
     * Serial for this user.
     */
    @Input
    @Optional
    String serial

    /**
     * Relative or absolute path to the project.
     */
    @Input
    @Optional
    String projectPath

    /**
     * If set to noquit, will not use the -quit parameter.
     */
    @Input
    @Optional
    String quitMode

    /**
     * Path to log file.
     */
    @Input
    String logPath

    /**
     * The build target to use.
     */
    @Input
    String target

    /**
     * The method to call.
     */
    @Input
    String method

    private LogWatcher _logWatcher;

    /**
     * Constructor.
     */
    public UnityMethodTask() {
        super()
    }

    /**
     * Executes the method.
     */
    @TaskAction
    protected void exec() {
        // set the executable
        if (unityBin) {
            executable unityBin

            println "Using custom unity install."
        } else {
            executable getUnityPath()

            println "Using auto detected unity install."
        }

        // prepare our custom arguments
        def customArgs = new ArrayList<String>()

        addDefaultArgs(customArgs)

        customArgs.add('-executeMethod')
        customArgs.add(method)

        customArgs.add('-projectPath')
        customArgs.add(getProjectPath())

        // set args on Exec task
        setArgs(customArgs)

        // watch logs
        def watcher = new LogWatcher(new File(logPath))
            // forward to stdout
            .addDelegate(new StdoutLogWatcherDelegate())
            // look for regexes
            .addDelegate(new RegexMatchLogWatcherDelegate("Crash\\!\\!\\!"));
        (new Thread(watcher)).start()

        // pass to super
        super.exec()
    }

    /**
     * Adds default arguments to run Unity in batchmdoe.
     *
     * @param      customArgs  The custom arguments list to add to.
     */
    private void addDefaultArgs(List<String> customArgs) {
        if (quitMode != 'noquit') {
            println "Using quit."

            customArgs.add('-quit')
        } else {
            println "No quit."
        }
        
        customArgs.add('-batchmode')
        customArgs.add('-nographics')

        if (null != username) {
            customArgs.add('-username')
            customArgs.add(username)
        }

        if (null != password) {
            customArgs.add('-password')
            customArgs.add(password)
        }

        if (null != serial) {
            customArgs.add('-serial')
            customArgs.add(serial)
        }

        if (null != target) {
            // constants taken from https://docs.unity3d.com/Manual/CommandLineArguments.html
            if (!["win32", "win64", "osx", "linux", "linux64", "ios", "android", "web", "webstreamed", "webgl", "xboxone", "ps4", "psp2", "wsaplayer", "tizen", "samsungtv"].contains(target)) {
               throw new GradleException("Invalid build target.");  
            }

            customArgs.add('-buildTarget')
            customArgs.add(target)
        }

        if (null != logPath) {
            customArgs.add('-logFile')
            customArgs.add(logPath)
        }
    }

    /**
     * Tries to figure out where the Unity executable is.
     *
     * @return     The unity path.
     */
    private String getUnityPath() {
        if (System.getenv('UNITY_BIN')) {
            return System.getenv('UNITY_BIN')
        }

        def defaultLocations = [
            'C:/Program Files/Unity/Editor/Unity.exe',
            'C:/Program Files (x86)/Unity/Editor/Unity.exe',
            '/Applications/Unity'
        ]

        for (def location in defaultLocations) {
            if (new File(location).exists()) {
                return location
            }
        }

        throw new GradleException('Could not find Unity.')
    }

    /**
     * Retrieves the project director.
     *
     * @return     The project path.
     */
    public String getProjectPath() {
        return new File(projectPath ?: System.getProperty('user.dir')).absolutePath
    }
}