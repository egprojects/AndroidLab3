package ru.kfu.android_lab_third_course

sealed class News {
    class ShowCalculationResult(val values: Array<Int?>) : News()
}