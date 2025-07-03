package com.jonnyzzz.tbe.demo.integration

import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.lang.Thread.sleep
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
      .chromium()
      .launch(
        BrowserType.LaunchOptions().setHeadless(false)
      )

    try {
      browser.newPage().apply {
        navigate(ideServicesDemoComposeContainer.ideServiceUrl)
        println("Navigated to IDE Services URL: ${ideServicesDemoComposeContainer.ideServiceUrl}")

        // Click on the sign-in button
        locator("text=Sign in").click()
        println("Clicked on Sign in button")

        // Verify that the login form is visible
        val loginForm = locator("#login-form")
        kotlin.test.assertTrue(loginForm.isVisible(), "Login form should be visible")
        println("Login form is visible")
      }.close()
    } finally {
      browser.close()
      playwright.close()
    }
  }
}
