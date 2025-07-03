package com.jonnyzzz.tbe.demo.integration

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists


object IdeServicesPackage {
  val ideServicesDemoDir: Path by lazy {
    val systemProperty = System.getProperty("tbe.demo.dir")
      ?: error("Failed to find tbe.demo.dir system property, are you using Gradle to start?")
    if (!File(systemProperty).exists()) error("tbe.demo.dir system property is not a valid directory: $systemProperty")

    Paths.get(systemProperty)
  }

  val ideServicesComposeFile by lazy {
    val compose = ideServicesDemoDir.resolve("docker-compose.yml")
    if (!compose.exists()) error("Failed to find docker-compose.yml in $ideServicesDemoDir")
    compose
  }
}
