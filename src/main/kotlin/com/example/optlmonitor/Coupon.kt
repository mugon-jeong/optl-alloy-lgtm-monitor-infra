package com.example.optlmonitor

import org.springframework.data.annotation.Id
import java.time.LocalDateTime

data class Coupon(
    @Id
    val id: Long,
    val title: String,
    val description: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val operator: Operator,
    val value: Double
)
