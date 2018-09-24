package io.boonlogic.boku.microservices.video.web.handlers

import io.boonlogic.boku.microservices.video.web.Routes
import org.junit.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient

@RunWith(SpringRunner::class)
@Nested
@DisplayName("a handler")
//@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@WebFluxTest(controllers = [Routes::class])
@ExtendWith(SpringExtension::class)
class ApplicationTests {

    @Autowired
    lateinit var client: WebTestClient

//    @Autowired private lateinit var handler: ConfigHandler
//    @Autowired private lateinit var restTemplate: TestRestTemplate



    @Test
    fun `run without test`() {

//        val req = ServerResponse.ok().toMono()
//        handler.foo(req)

        client.get().uri("demo").exchange().expectStatus().isOk

//        client.get()
//            .uri("/hello")
//            .exchange()
//            .expectStatus().isOk
//            .expectBody()
//            .consumeWith({ m ->
//                assertEquals("Hello World!", String(m.responseBodyContent ?: ByteArray(0), StandardCharsets.UTF_8))
//            })

//        val content = """[{"firstName":"Jack","lastName":"Bauer","id":1},{"firstName":"Chloe","lastName":"O'Brian","id":2},{"firstName":"Kim","lastName":"Bauer","id":3},{"firstName":"David","lastName":"Palmer","id":4},{"firstName":"Michelle","lastName":"Dessler","id":5}]"""
//        assertEquals(content, restTemplate.getForObject<String>("/customers", String::class.java))
    }

}
