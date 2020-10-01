package ru.kfu.android_lab_third_course

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.buy_with_googlepay_button.*
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private lateinit var paymentsClient: PaymentsClient

    companion object {
        private const val LOAD_PAYMENT_DATA_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        paymentsClient = GooglePayHelper.createPaymentsClient(this)!!

        val isReadyToPayJson: JSONObject? = GooglePayHelper.getIsReadyToPayRequest()
        if (isReadyToPayJson == null) {
            return
        }
        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString()) ?: return

        val task: Task<Boolean> = paymentsClient.isReadyToPay(request)
        task.addOnSuccessListener {
            buy_with_google_button.isClickable = true
            buy_with_google_button.setOnClickListener { payWithGooglePay() }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        hideLoading()
        when (requestCode) {
            LOAD_PAYMENT_DATA_REQUEST_CODE ->
                when (resultCode) {
                    Activity.RESULT_OK ->
                        data?.let {
                            val paymentData = PaymentData.getFromIntent(data)
                            val tokenJSON = paymentData?.paymentMethodToken?.token
                            showResult(tokenJSON.toString())
                        }
                    AutoResolveHelper.RESULT_ERROR -> showResult("Error")
                    else -> {
                    }
                }
        }
    }

    private fun payWithGooglePay() {
        showLoading()

        val paymentDataRequestJson: JSONObject? = GooglePayHelper.getPaymentDataRequest("0.0")
        if (paymentDataRequestJson == null) {
            return
        }
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())

        if (request != null) {
            AutoResolveHelper.resolveTask(
                paymentsClient.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE
            )
        }
    }

    private fun showLoading() {
        pb_main_payment_progress.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        pb_main_payment_progress.visibility = View.GONE
    }

    private fun showResult(result: String) {
        tv_main_result.text = result
        tv_main_result.visibility = View.VISIBLE
    }
}