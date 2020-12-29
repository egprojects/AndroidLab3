package ru.kfu.android_lab_third_course

sealed class Action {
    object Wait : Action()

    data class SetValue(val value: Int, val index: Int) : Action()

    data class CalculateAction(val values: Array<Int?>) : Action()

    object CalculationStarted : Action()

    object CalculationFinishedAction : Action()
}