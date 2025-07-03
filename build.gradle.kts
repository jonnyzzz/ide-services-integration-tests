import de.undercouch.gradle.tasks.download.Download

plugins {
  kotlin("jvm") version "2.1.20"
  id("de.undercouch.download") version "5.5.0"
}

//see https://www.jetbrains.com/help/ide-services/try-demo.html#start_demo
@Suppress("PropertyName")
val IDE_SERVICES_VERSION = "2025.2.0.33953"

repositories {
  mavenCentral()
}

dependencies {
  testImplementation("org.slf4j:slf4j-simple:2.0.9")

  // JUnit 5
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")

  testImplementation(platform("org.testcontainers:testcontainers-bom:1.21.3"))
  testImplementation("org.testcontainers:testcontainers")
  testImplementation("org.testcontainers:junit-jupiter")

  // Kotlin test
  testImplementation(kotlin("test"))
}

val downloadDir = layout.buildDirectory.dir("downloads")
val tbeDemoDir = layout.buildDirectory.dir("tbe-demo")
val archiveFile = downloadDir.map { it.file("tbe-launcher-2025.2.0.33953.tar") }

val prepareIDEServices by tasks.registering(Sync::class) {
  group = "build"
  dependsOn(downloadIDEServices)
  from(zipTree(archiveFile)) {
    eachFile { path = path.trimStart('/').substringAfter("/") }
  }
  into(tbeDemoDir)
  inputs.property("version", IDE_SERVICES_VERSION)
  inputs.property("file", buildFile)
  outputs.dir(tbeDemoDir)
}

val downloadIDEServices by tasks.registering(Download::class) {
  group = "download"
  description = "Download and unpack JetBrains IDE Services launcher"
  src("https://download.jetbrains.com/ide-services/demo/tbe-demo-${IDE_SERVICES_VERSION}.zip")
  dest(archiveFile)
  overwrite(true)

  inputs.property("version", IDE_SERVICES_VERSION)
  inputs.property("file", buildFile)
  outputs.file(archiveFile)

  doFirst {
    archiveFile.get().asFile.parentFile.mkdirs()
  }
}

tasks.withType<Test> {
  dependsOn(prepareIDEServices)
  useJUnitPlatform()

  doFirst {
    systemProperty("tbe.demo.dir", tbeDemoDir.get().asFile.absolutePath)
    systemProperty("tbe.demo.version", IDE_SERVICES_VERSION)
  }
}
