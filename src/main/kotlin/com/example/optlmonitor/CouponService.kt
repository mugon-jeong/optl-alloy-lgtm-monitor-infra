package com.example.optlmonitor

import com.example.optlmonitor.exception.NotFoundException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.function.Supplier

val logger = KotlinLogging.logger { }

@Service
class CouponService(private val couponRepository: CouponRepository) {

    fun findAll(): Iterable<Coupon> {
        logger.debug("Fetching all coupons")
        val coupons = couponRepository.findAll()
        logger.debug("Coupons: {}", coupons)
        return coupons
    }

    fun findById(id: Long): Coupon {
        logger.debug("Fetching coupon by id: {}", id)
        return couponRepository.findById(id)
            .orElseThrow<RuntimeException>(Supplier<RuntimeException> { NotFoundException("Invalid id: %d".formatted(id)) })
    }

    fun saveCoupon(coupon: Coupon): Coupon {
        logger.debug("Saving coupon: {}", coupon)
        val newlyCoupon = couponRepository.save(coupon)
        logger.debug("New coupon on store: {}", newlyCoupon)
        return newlyCoupon
    }

    fun deleteCoupon(id: Long) {
        logger.debug("Deleting coupon by id: {}", id)
        val coupon = couponRepository.findById(id)
            .orElseThrow<RuntimeException>(Supplier<RuntimeException> { NotFoundException("Invalid id: %d".formatted(id)) })
        couponRepository.delete(coupon)
        logger.debug("Deleted coupon by id: {}", id)
    }

    fun findActiveCoupons(startDate: LocalDateTime?, endDate: LocalDateTime?): List<Coupon> {
        logger.debug("Fetching active coupons between {} and {}", startDate, endDate)
        val activeCoupons = couponRepository.findActiveCoupons(startDate, endDate)
        logger.debug("Active coupons: {}", activeCoupons)
        return activeCoupons
    }
}