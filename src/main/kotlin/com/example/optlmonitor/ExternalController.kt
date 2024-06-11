package com.example.optlmonitor

import com.example.optlmonitor.exception.FakeInternalException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.util.*


@RestController
@RequestMapping("/api/v1/external")
class ExternalController {
    private val random: Random = Random(0)
    @GetMapping
    fun chain(): String {
        if (random.nextInt(3) > 1) {
            throw FakeInternalException("Failed to fetch active coupons")
        }
        val restTemplate = RestTemplate()
        val response = restTemplate.getForEntity("http://web:8080/api/v1/coupons", String::class.java).body
        return response.orEmpty()
    }
}