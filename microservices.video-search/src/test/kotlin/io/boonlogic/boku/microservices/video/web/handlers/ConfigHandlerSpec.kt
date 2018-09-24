package io.boonlogic.boku.microservices.video.web.handlers

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
object ConfigHandlerSpec: Spek({
    describe("config service") {
        describe("should do") {
            it("something") {
                assertEquals(8, 8)
            }
            it("something 2") {
                // assertions
            }
            it("something 3") {
            }
        }
    }
})
