package com.example.optlmonitor

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime



interface CouponRepository:CrudRepository<Coupon,Long> {
    @Query("SELECT * FROM coupon WHERE start_date <= :startDate AND end_date >= :endDate")
    fun findActiveCoupons(startDate: LocalDateTime?, endDate: LocalDateTime?): List<Coupon>
}