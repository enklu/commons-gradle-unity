package com.createar.plugins.unity;

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

/**
 * Adds simple Unity functionality to Gradle.
 */
public class UnityPlugin implements Plugin<Project> {

	/**
	 * Applies the plugin to the project.
	 *
	 * @param      project  The project.
	 */
	void apply(Project target) {
		target.task('unity', type: UnityMethodTask)
	}
}