package com.example.optlmonitor

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Random
import kotlin.math.abs

private val log = KotlinLogging.logger {  }
@RestController
@RequestMapping("/")
class OptlController {
    private val random: Random = Random(0)

    @GetMapping("/rolldice")
    @Throws(InterruptedException::class)
    fun index(@RequestParam("player") player: String?): String {
        Thread.sleep(abs((random.nextGaussian() + 1.0) * 200.0).toLong())
        if (random.nextInt(10) < 3) {
            throw RuntimeException("simulating an error")
        }
        val result = this.getRandomNumber(1, 6)
        if (player != null) {
            log.info { "$player is rolling the dice $result" }
        } else {
            log.info { "Anonymous player is rolling the dice $result" }
        }
        return result.toString()
    }

    fun getRandomNumber(min: Int, max: Int): Int {
        return random.nextInt(min, max + 1)
    }
}