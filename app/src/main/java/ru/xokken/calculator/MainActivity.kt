package ru.xokken.calculator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.xokken.calculator.presentation.CalcFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, CalcFragment.newInstance())
                .commitNow()
        }
    }
}