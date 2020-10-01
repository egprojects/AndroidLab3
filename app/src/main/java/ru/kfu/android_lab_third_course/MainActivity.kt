package ru.kfu.android_lab_third_course

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentsClient
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.buy_with_googlepay_button.*

class MainActivity : AppCompatActivity() {
    private lateinit var paymentsClient: PaymentsClient

    companion object {
        private const val LOAD_PAYMENT_DATA_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        paymentsClient = GooglePayHelper.createPaymentsClient(this)
        GooglePayHelper.isReadyToPay(paymentsClient).addOnSuccessListener {
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
        val request = GooglePayHelper.createPaymentDataRequest("0,0")
        paymentsClient.let {
            showLoading()
            AutoResolveHelper.resolveTask(
                it.loadPaymentData(request),
                this,
                LOAD_PAYMENT_DATA_REQUEST_CODE
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
    }
}