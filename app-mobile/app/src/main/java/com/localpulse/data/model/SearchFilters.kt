package com.localpulse.data.model

/**
 * Data class for event search filters
 */
data class SearchFilters(
    val city: String = "",
    val keyword: String = "",
    val category: String = "",
    val startDate: String = "",
    val endDate: String = ""
)

