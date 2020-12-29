package ru.kfu.android_lab_third_course

import io.reactivex.Single
import java.util.concurrent.TimeUnit

class SmartCalculationService {
    fun calculate(term1: Int?, term2: Int?, result: Int?): Single<Int> {
        return Single.just(
            if (result == null) {
                term1!! + term2!!
            } else {
                result - (term1 ?: term2!!)
            }
        )
            .delay(5, TimeUnit.SECONDS)
    }
}