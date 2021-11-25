package ru.xokken.calculator.util

import android.text.Editable
import android.widget.EditText

fun Editable.getInt(): Int? {
    if (this.isNotEmpty()) {
        return this.toString().toInt()
    }
    return null
}

fun EditText.setValue(value: Int?) {
    if (this.editableText.getInt() != value) this.setText(value.toString())
}
