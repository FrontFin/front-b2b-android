package com.getfront.catalog.entity

internal sealed interface CatalogResponse {
    object Done : CatalogResponse
    object Close : CatalogResponse
    object Undefined : CatalogResponse
    data class Connected(val accounts: List<FrontAccount>) : CatalogResponse
}
