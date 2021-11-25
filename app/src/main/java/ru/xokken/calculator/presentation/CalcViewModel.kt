package ru.xokken.calculator.presentation

import androidx.lifecycle.ViewModel
import com.freeletics.rxredux.reduxStore
import io.reactivex.Observable
import ru.xokken.calculator.presentation.model.*
import java.util.concurrent.TimeUnit

class CalcViewModel : ViewModel() {

    private val initState = CalcState()
    private var previousAction: InputAction? = null
    private var lastAction: InputAction? = null

    fun observeState(upstreamActions: Observable<Action>): Observable<CalcState> =
        upstreamActions
            .switchMap { action ->
                if (action is DiffResInput) {
                    replaceLastAction(action)
                    return@switchMap Observable
                        .concat(
                            Observable.just(StartLoading(action.info)),
                            Observable.just(action).delay(55, TimeUnit.SECONDS)
                        )
                }
                Observable.just(action)
            }
            .reduxStore(
                initState, listOf()
            ) { state, action ->
                when (action) {
                    is StartLoading -> {
                        getLoadingState(state, action.info)
                    }
                    is InputAction -> {
                        replaceLastAction(action)
                        previousAction ?: run {
                            return@reduxStore state.submitInputAction(action)
                        }
                        calcResultState()
                    }
                    else -> state
                }
            }.distinctUntilChanged()

    private fun replaceLastAction(
        action: InputAction
    ) {
        when (action) {
            is FirstInput -> if (lastAction !is FirstInput) {
                previousAction = lastAction
            }
            is SecondInput -> if (lastAction !is SecondInput) {
                previousAction = lastAction
            }
            is ThirdInput -> if (lastAction !is ThirdInput) {
                previousAction = lastAction
            }
        }
        lastAction = action
    }

    private fun getLoadingState(
        currentState: CalcState,
        inputInfo: InputInfo
    ): CalcState =
        with(inputInfo) {
            currentState.copy(
                firstInput = firstValue,
                secondInput = secondValue,
                thirdInput = thirdValue,
                isLoading = true
            )
        }

    private fun calcResultState(): CalcState {
        val actionList = listOf(lastAction, previousAction)
        var firstValue = actionList.find { action ->
            action is FirstInput
        }?.value
        var secondValue = actionList.find { action ->
            action is SecondInput
        }?.value
        var thirdValue = actionList.find { action ->
            action is ThirdInput
        }?.value
        firstValue ?: run {
            firstValue = thirdValue!! - secondValue!!
        }
        secondValue ?: run {
            secondValue = thirdValue!! - firstValue!!
        }
        thirdValue ?: run {
            thirdValue = firstValue!! + secondValue!!
        }
        return CalcState(
            firstValue,
            secondValue,
            thirdValue
        )
    }
}
