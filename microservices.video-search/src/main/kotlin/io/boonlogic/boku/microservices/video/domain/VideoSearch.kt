package io.boonlogic.boku.microservices.video.domain

import java.time.ZonedDateTime

data class VideoSearchRequest(
    val searchQuery: String,
    val category: String
)

data class VideoSearchCreationResponse(
    val id: String,
    val creationTime: ZonedDateTime = ZonedDateTime.now()
)
