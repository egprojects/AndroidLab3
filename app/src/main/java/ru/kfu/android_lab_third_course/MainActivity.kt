package ru.kfu.android_lab_third_course

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val listeners = arrayOfNulls<TextWatcher>(3)
    var store = Store(
        listOf(
            CalculationSideEffect(SmartCalculationService(), PublishRelay.create())
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        store.state
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::render)

        for (i in 0..2) {
            listeners[i] = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    store.actionRelay.onNext(Action.SetValue(s.toString().toInt(), i))
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            }
        }
        registerListeners()
    }

    private fun render(state: State) {
        val isCalculating = state.isCalculating
        val contentVisibility = if (isCalculating) View.GONE else View.VISIBLE
        if (!isCalculating) {
            val values = state.values
            unregisterListeners()
            if (values[0] != null) et_value1.setText(values[0].toString())
            if (values[1] != null) et_value2.setText(values[1].toString())
            if (values[2] != null) et_value3.setText(values[2].toString())
            registerListeners()
        }
        arrayOf(et_value1, tv_plus, et_value2, tv_equals, et_value3).forEach {
            it.visibility = contentVisibility
        }
        pb.visibility = if (isCalculating) View.VISIBLE else View.GONE
    }

    private fun registerListeners() {
        arrayOf(et_value1, et_value2, et_value3).forEachIndexed { index, et ->
            et.addTextChangedListener(listeners[index])
        }
    }

    private fun unregisterListeners() {
        arrayOf(et_value1, et_value2, et_value3).forEachIndexed { index, et ->
            et.removeTextChangedListener(listeners[index])
        }
    }

}