import de.undercouch.gradle.tasks.download.Download

plugins {
  kotlin("jvm") version "2.1.20"
  id("de.undercouch.download") version "5.5.0"
}

//see https://www.jetbrains.com/help/ide-services/docker-installation.html#binary_installation
@Suppress("PropertyName")
val IDE_SERVICES_VERSION = "2025.2.0.33953"

val downloadDir = layout.buildDirectory.dir("downloads")
val tbeDemoDir = layout.buildDirectory.dir("tbe-demo")
val archiveFile = downloadDir.map { it.file("tbe-launcher-2025.2.0.33953.tar") }

val prepareIDEServices by tasks.registering(Sync::class) {
  group = "build"
  dependsOn(downloadIDEServices)
  from(tarTree(archiveFile))
  into(tbeDemoDir)
}

val downloadIDEServices by tasks.registering(Download::class) {
  group = "download"
  description = "Download and unpack JetBrains IDE Services launcher"
  src("http://download.jetbrains.com/ide-services/files/tbe-launcher-${IDE_SERVICES_VERSION}.tar")
  dest(archiveFile)
  overwrite(false)

  doFirst {
    downloadDir.get().asFile.mkdirs()
    tbeDemoDir.get().asFile.mkdirs()
  }
}

