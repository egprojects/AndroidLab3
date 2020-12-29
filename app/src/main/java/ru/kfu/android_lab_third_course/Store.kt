package ru.kfu.android_lab_third_course

import com.freeletics.rxredux.reduxStore
import io.reactivex.subjects.PublishSubject

class Store(
    sideEffects: List<SideEffect>
) {
    val actionRelay = PublishSubject.create<Action>()

    val state = actionRelay.reduxStore(
        State(),
        sideEffects,
        Reducer()::reduce
    )
}