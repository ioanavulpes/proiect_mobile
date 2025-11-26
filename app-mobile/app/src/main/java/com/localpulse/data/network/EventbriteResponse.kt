package com.localpulse.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response models for Ticketmaster Discovery API v2
 * These match the structure of the JSON response from Ticketmaster
 */

@Serializable
data class TicketmasterResponse(
    @SerialName("_embedded")
    val embedded: EmbeddedEvents? = null,
    @SerialName("page")
    val page: PageInfo? = null
)

@Serializable
data class EmbeddedEvents(
    @SerialName("events")
    val events: List<TicketmasterEvent>? = null
)

@Serializable
data class TicketmasterEvent(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("url")
    val url: String,
    @SerialName("images")
    val images: List<EventImage>? = null,
    @SerialName("dates")
    val dates: EventDates? = null,
    @SerialName("_embedded")
    val embedded: EventEmbedded? = null,
    @SerialName("info")
    val info: String? = null,
    @SerialName("pleaseNote")
    val pleaseNote: String? = null
)

@Serializable
data class EventImage(
    @SerialName("url")
    val url: String? = null,
    @SerialName("ratio")
    val ratio: String? = null,
    @SerialName("width")
    val width: Int? = null,
    @SerialName("height")
    val height: Int? = null
)

@Serializable
data class EventDates(
    @SerialName("start")
    val start: DateStart? = null,
    @SerialName("timezone")
    val timezone: String? = null
)

@Serializable
data class DateStart(
    @SerialName("localDate")
    val localDate: String? = null,
    @SerialName("localTime")
    val localTime: String? = null,
    @SerialName("dateTime")
    val dateTime: String? = null
)

@Serializable
data class EventEmbedded(
    @SerialName("venues")
    val venues: List<TicketmasterVenue>? = null
)

@Serializable
data class TicketmasterVenue(
    @SerialName("name")
    val name: String? = null,
    @SerialName("address")
    val address: VenueAddress? = null,
    @SerialName("city")
    val city: VenueCity? = null,
    @SerialName("state")
    val state: VenueState? = null,
    @SerialName("location")
    val location: VenueLocation? = null
)

@Serializable
data class VenueAddress(
    @SerialName("line1")
    val line1: String? = null,
    @SerialName("line2")
    val line2: String? = null
)

@Serializable
data class VenueCity(
    @SerialName("name")
    val name: String? = null
)

@Serializable
data class VenueState(
    @SerialName("name")
    val name: String? = null,
    @SerialName("stateCode")
    val stateCode: String? = null
)

@Serializable
data class VenueLocation(
    @SerialName("latitude")
    val latitude: String? = null,
    @SerialName("longitude")
    val longitude: String? = null
)

@Serializable
data class PageInfo(
    @SerialName("size")
    val size: Int? = null,
    @SerialName("totalElements")
    val totalElements: Int? = null,
    @SerialName("totalPages")
    val totalPages: Int? = null,
    @SerialName("number")
    val number: Int? = null
)

