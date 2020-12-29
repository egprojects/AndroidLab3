package ru.kfu.android_lab_third_course

import ru.kfu.android_lab_third_course.Action.*

class Reducer {
    fun reduce(state: State, action: Action): State {
        return when (action) {
            is Wait -> state
            is SetValue -> state.copy(
                values = state.values.apply { set(action.index, action.value) }
            )
            is CalculateAction -> state
            is CalculationStarted -> state.copy(isCalculating = true)
            is CalculationFinishedAction -> state.copy(isCalculating = false)
        }
    }

}