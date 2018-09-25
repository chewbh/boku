package io.boonlogic.boku.microservices.video.web.handlers

import org.assertj.core.api.Assertions
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.tools.jdbc.MockConnection
import org.jooq.tools.jdbc.MockFileDatabase
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@Nested
@DisplayName("a handler")
@SpringBootTest
@ExtendWith(SpringExtension::class)
class ApplicationTests {

//    private val create by lazy {
//        val provider = object : MockDataProvider {
//            override fun execute(ctx: MockExecuteContext?): Array<MockResult> {
//                val create = DSL.using(SQLDialect.POSTGRES)
//
//                println("helloworld")
//
//                val mockResults = arrayOf(
//                    MockResult(
//                        1,
//                        create.newResult(DSL.field("name"), DSL.field("description")).apply {
//                            add(create.newRecord(
//                                DSL.field("name"), DSL.field("description"))
//                                .values("Jack Reacher","Tim Cruise movie")
//                            )
//                            add(create.newRecord(
//                                DSL.field("name"), DSL.field("description"))
//                                .values("Priest","Vampire movie")
//                            )
//                            add(create.newRecord(
//                                DSL.field("name"), DSL.field("description"))
//                                .values("Police Story","Jackie Chan, who also directs, stars as Kevin, a Hong Kong cop")
//                            )
//                        }
//                    ))
//
//                ctx?.let {
//                    val sql = ctx.sql()
//                    if(sql.toUpperCase().startsWith("SELECT ")) {
//                        return mockResults
//                    } else {
//                        throw SQLException("Statement not supported: $sql")
//                    }
//                }
//                return emptyArray()
//            }
//        }
//        DSL.using(MockConnection(provider), SQLDialect.POSTGRES)
//    }

    private val create: DSLContext by lazy {
        val provider = MockFileDatabase(
            """
                select title, desc from video where title = 'hero1';
                > title            desc
                > ------------     ----------
                > Jack Reacher     Tim Cruise movie
                > Police Story     Jackie Chan, who also directs, stars as Kevin
                @ rows: 2

                select title, desc from video where title = 'hero2';
                > title           desc
                > ------------    ----------
                > Priest          Vampire movie
                > Jack Reacher    Tim Cruise movie
                @ rows: 2
            """.trimIndent())
        DSL.using(MockConnection(provider), SQLDialect.POSTGRES)
    }

    @Before
    fun setup() {

    }


    @Test
    fun `run without test`() {

        val result =
            create.select(DSL.field("title"),DSL.field("desc"))
                .from(DSL.table("video"))
                .where(DSL.field("title").eq("hero1"))
                .fetch {
                    "${it[0]}|_|${it[1]}"
                }
        println(result)
        Assertions.assertThat("").isEqualTo("")
    }

    @Test
    fun `run with test`() {

        val result =
            create.select(DSL.field("title"),DSL.field("desc"))
                .from(DSL.table("video"))
                .where(DSL.field("title").eq("hero2"))
                .fetch {
                    "${it[0]}|_|${it[1]}"
                }
        println(result)
        Assertions.assertThat("").isEqualTo("")
    }

}
