package ru.kfu.android_lab_third_course

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var expression: String
    private lateinit var numbers: MutableList<Int>
    private lateinit var operations: MutableList<Operation>

    companion object {
        private const val APP_PREFERENCES = "preferences"
        private const val APP_PREFERENCES_THEME = "theme"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val defaultNightMode = AppCompatDelegate.getDefaultNightMode()
        val preferencedDefaultNightMode =
            getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE).getInt(
                APP_PREFERENCES_THEME, defaultNightMode
            )
        if (defaultNightMode != preferencedDefaultNightMode) {
            changeTheme(preferencedDefaultNightMode)
        }

        setContentView(R.layout.activity_main)

        expression = savedInstanceState?.getString("expression") ?: ""
        numbers = savedInstanceState?.getIntArray("numbers")
            ?.toMutableList() ?: mutableListOf()
        operations = savedInstanceState?.getIntArray("operations")
            ?.map { Operation.values()[it] }
            ?.toMutableList() ?: mutableListOf()
        if (numbers.size != 0) {
            tv_main_expression.text = expression
            if (numbers.size == operations.size) setActionsEnabled(false)
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putString("expression", expression)
        savedInstanceState.putIntArray("numbers", numbers.toIntArray())
        savedInstanceState.putIntArray("operations", operations.map { it.ordinal }.toIntArray())
    }

    fun onNumClicked(view: View) {
        if (operations.contains(Operation.EQUALITY)) clear()
        val num = (view as Button).text.toString().toInt()
        expression += num
        if (numbers.size == operations.size) {
            numbers.add(num)
        } else {
            val oldNum = numbers.removeAt(numbers.size - 1)
            numbers.add(oldNum * 10 + num)
        }
        onExpressionChanged()
        setActionsEnabled(true)
    }

    private fun onExpressionChanged() {
        tv_main_expression.text = expression
    }

    private fun setActionsEnabled(areEnabled: Boolean) {
        if (btn_main_plus.isEnabled != areEnabled) {
            btn_main_plus.isEnabled = areEnabled
            btn_main_minus.isEnabled = areEnabled
            btn_main_equals.isEnabled = areEnabled
        }
    }

    fun onPlusClicked(view: View) {
        expression += " + "
        operations.add(Operation.PLUS)
        onExpressionChanged()
        setActionsEnabled(false)
    }

    fun onMinusClicked(view: View) {
        expression += " - "
        operations.add(Operation.MINUS)
        onExpressionChanged()
        setActionsEnabled(false)
    }

    fun onEqualsClicked(view: View) {
        val result = calculateResult()
        expression += " = $result"
        operations.add(Operation.EQUALITY)
        onExpressionChanged()
        setActionsEnabled(false)
    }

    private fun calculateResult(): Int {
        var result = numbers.first()
        for (i in operations.indices) {
            val num = numbers[i + 1]
            result += num * if (operations[i] == Operation.PLUS) 1 else -1
        }

        return result
    }

    private fun clear() {
        expression = ""
        numbers.clear()
        operations.clear()
    }

    fun onChangeThemeClicked(view: View) {
        changeTheme(
            if (AppCompatDelegate.getDefaultNightMode() == MODE_NIGHT_YES) {
                MODE_NIGHT_NO
            } else {
                MODE_NIGHT_YES
            }
        )
    }

    private fun changeTheme(defaultNightMode: Int) {
        AppCompatDelegate.setDefaultNightMode(defaultNightMode)
        getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
            .edit()
            .putInt(APP_PREFERENCES_THEME, defaultNightMode)
            .apply()
    }
}