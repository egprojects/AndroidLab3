package ru.kfu.android_lab_third_course

import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import io.reactivex.rxkotlin.ofType

import ru.kfu.android_lab_third_course.Action.*

class CalculationSideEffect(
    private val calculationService: SmartCalculationService,
    private val relay: Relay<News>
) : SideEffect {
    override fun invoke(actions: Observable<Action>, state: () -> State): Observable<out Action> {
        return actions.ofType<SetValue>()
            .switchMap { action ->
                val state = state()
                if (state.activeValuesIndexes.size == 2) {
                    state.activeValuesIndexes.removeAt(0)
                }
                state.activeValuesIndexes.add(action.index)
                state.values[action.index] = action.value
                if (state.activeValuesIndexes.size == 2) {
                    var term1 = if (state.activeValuesIndexes.contains(0)) state.values[0] else null
                    var term2 = if (state.activeValuesIndexes.contains(1)) state.values[1] else null
                    var equationResult = if (state.activeValuesIndexes.contains(2)) {
                        state.values[2]
                    } else {
                        null
                    }
                    calculationService.calculate(
                        term1,
                        term2,
                        equationResult
                    ).doOnSuccess {
                        if (term1 == null) term1 = it
                        if (term2 == null) term2 = it
                        if (equationResult == null) equationResult = it
                        state.values[0] = term1
                        state.values[1] = term2
                        state.values[2] = equationResult
                        relay.accept(
                            News.ShowCalculationResult(arrayOf(term1, term2, equationResult))
                        )
                    }.map<Action> {
                        CalculationFinishedAction
                    }.toObservable()
                        .startWith(CalculationStarted)
                } else {
                    Observable.just(Wait)
                }
            }
    }
}