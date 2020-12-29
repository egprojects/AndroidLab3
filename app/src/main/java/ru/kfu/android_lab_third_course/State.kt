package ru.kfu.android_lab_third_course

data class State(
    val values: Array<Int?> = arrayOfNulls(3),
    val activeValuesIndexes: MutableList<Int> = mutableListOf(),
    var isCalculating: Boolean = false
)