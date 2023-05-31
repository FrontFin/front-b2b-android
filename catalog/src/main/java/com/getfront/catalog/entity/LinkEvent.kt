package com.getfront.catalog.entity

internal sealed interface LinkEvent {
    object Done : LinkEvent
    object Close : LinkEvent
    object ShowClose : LinkEvent
    object Undefined : LinkEvent
    data class Connected(val accounts: List<FrontAccount>) : LinkEvent
    data class Payload(val payload: FrontPayload) : LinkEvent
}
