package com.jonnyzzz.tbe.demo.integration

import org.slf4j.LoggerFactory
import org.testcontainers.containers.ComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import java.time.Duration

class IdeServicesDemoCompose
  : ComposeContainer(IdeServicesPackage.ideServicesComposeFile.toFile()) {
  private val tbeContainerLog = LoggerFactory.getLogger("tbe-container-logs")

  private val tbeServer = ServiceNameAndPort("tbe-server", 8443)
  private val mockAuth = ServiceNameAndPort("mock-auth", 8085)

  /**
   * Base URL and name of IDE Services server. It is running with HTTPS and self-signed certificate,
   * which is included in the TBE Demo package.
   *
   * See https://www.jetbrains.com/help/ide-services/try-demo.html#start_demo for more details
   */
  val ideServiceUrl: String get() = resolveServiceUrl("https", tbeServer)

  /**
   * The demo package comes with the mock-auth, a tiny OAuth2 compatible service,
   * which implement JWT/JWKS tokens and allows one to generate new tokes easily.
   * We use that service to simplify the initial setup before the company-provided SSO is configured
   */
  val mockAuthUrl: String get() = resolveServiceUrl("http", mockAuth)


  init {
    withPull(true)
    withRemoveVolumes(true)
    withLocalCompose(true)
    withExposedService(tbeServer)
    withExposedService(mockAuth)
    withLogConsumer(tbeServer.serviceName) { tbeContainerLog.info("[TBE] " + it.utf8String) }
    waitingFor(
      tbeServer.serviceName,
      Wait
        .forHttps("/actuator/health")
        .allowInsecure()
        .forPort(tbeServer.servicePort)
        .forStatusCode(200)
        .withStartupTimeout(
          Duration.ofMinutes(5)
        )
    )
    waitingFor(
      mockAuth.serviceName,
      Wait
        .forHttp("/")
        .forPort(mockAuth.servicePort)
        .forStatusCode(200)
        .withStartupTimeout(
          Duration.ofMinutes(1)
        )
    )
    withStartupTimeout(Duration.ofMinutes(5))
  }

  private data class ServiceNameAndPort(val serviceName: String, val servicePort: Int)

  private fun withExposedService(service: ServiceNameAndPort) =
    withExposedService(service.serviceName, service.servicePort)

  private fun resolveServiceUrl(schema: String, service: ServiceNameAndPort): String =
    "$schema://${getServiceHost(service.serviceName, service.servicePort)}:${
      getServicePort(
        service.serviceName,
        service.servicePort
      )
    }"
}
