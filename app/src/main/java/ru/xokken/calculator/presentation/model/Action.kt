package ru.xokken.calculator.presentation.model

sealed class Action

data class StartLoading(
    val info: InputInfo
) : Action()

sealed class InputAction(
    open val value: Int
) : Action()

data class FirstInput(
    override val value: Int
) : InputAction(value)

data class SecondInput(
    override val value: Int
) : InputAction(value)

open class ThirdInput(
    override val value: Int
) : InputAction(value)

data class DiffResInput(
    override val value: Int,
    val info: InputInfo
) : ThirdInput(value)
