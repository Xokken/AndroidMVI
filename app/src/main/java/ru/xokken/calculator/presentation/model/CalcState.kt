package ru.xokken.calculator.presentation.model

data class CalcState(
    val firstInput: Int? = null,
    val secondInput: Int? = null,
    val thirdInput: Int? = null,
    val isLoading: Boolean = false
) {
    fun submitInputAction(action: InputAction): CalcState =
        when (action) {
            is FirstInput -> this.copy(
                firstInput = action.value
            )
            is SecondInput -> this.copy(
                secondInput = action.value
            )
            is ThirdInput -> this.copy(
                thirdInput = action.value
            )
            is DiffResInput -> this.copy(
                thirdInput = action.value
            )
        }
}
