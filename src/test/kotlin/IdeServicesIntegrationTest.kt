package com.jonnyzzz.tbe.demo.integration

import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.Test

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DockerComposeIntegrationTest {
  @Container
  val ideServicesDemoComposeContainer = IdeServicesDemoCompose()

  @BeforeAll
  fun setup() {
    ideServicesDemoComposeContainer.start()
    println("IDE Services Docker Compose environment is started started")
    println("Open IDE Services demo at ${ideServicesDemoComposeContainer.ideServiceUrl}")
  }

  @Test
  fun testWithPlaywright() {
    val playwright = Playwright.create()

    val browser = playwright
      .webkit()
      .launch(
        BrowserType.LaunchOptions().setHeadless(false)
      )

    try {
      val page = browser.newPage()
      page.navigate(ideServicesDemoComposeContainer.ideServiceUrl)
      println("Navigated to IDE Services URL: ${ideServicesDemoComposeContainer.ideServiceUrl}")
    } finally {
      browser.close()
      playwright.close()
    }
  }
}
