# ide-services-integration-tests

Description
-----------

This is a demo project that helps to write integration tests with
IDE Services. 

This test is based on the [TBE Demo](https://www.jetbrains.com/help/ide-services/try-demo.html#start_demo)
docker compose package which starts the server with mock authentication.

Contribution
------------

Please share your suggestions or pull requests

License
-------

Apache 2.0

Authentication
--------------

The Mock-Auth module is deployed as a dedicated service, you can use that to
issue fake authentication tokens, which you can use for the REST API calls 
in your tests. For that, do the following call 

```kotlin

  private fun withUserAuthentication(
    user: String, //"toolbox.admin" or "toolbox.user.z"
    consumer: WithUserAuthenticationScope.() -> Unit
  ) {
    authClient
      .post()
      .uri("/realms/toolbox/protocol/openid-connect/token")
      .contentType(MediaType.APPLICATION_FORM_URLENCODED)
      .bodyValue("client_id=tbe-server&username=${user}&password=toolboxpwd&grant_type=password&client_secret=bacd3019-c3b9-4b31-98d5-d3c410a1098e")
      .exchange()
      .expectStatus().is2xxSuccessful
      .expectBody()
      .jsonPath("$.['access_token', 'refresh_token']").value<Map<String, String>> {
        consumer.invoke(WithUserAuthenticationScope(it["access_token"]!!, it["refresh_token"]!!))
      }
  }

```
