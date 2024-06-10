package com.example.optlmonitor

import com.example.optlmonitor.exception.FakeInternalException
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit


@RestController
@RequestMapping("/api/v1/coupons")
class CouponController(private val couponService: CouponService) {
    private val random: Random = Random(0)

    @get:Throws(InterruptedException::class)
    @get:GetMapping
    val allCoupons: Iterable<Coupon>
        get() {
            // Simulate latency (Long running process)
            TimeUnit.of(ChronoUnit.SECONDS).sleep(random.nextLong(5))
            return couponService.findAll()
        }

    @GetMapping("/{id}")
    fun getCouponById(@PathVariable id: Long?): Coupon {
        return couponService.findById(id!!)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createCoupon(@RequestBody coupon: Coupon?): Coupon {
        return couponService.saveCoupon(coupon!!)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCoupon(@PathVariable id: Long?) {
        couponService.deleteCoupon(id!!)
    }

    @GetMapping("/active")
    fun getActiveCoupons(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime?,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime?
    ): List<Coupon> {
        if (random.nextInt(3) > 1) {
            throw FakeInternalException("Failed to fetch active coupons")
        }
        return couponService.findActiveCoupons(startDate, endDate)
    }
}