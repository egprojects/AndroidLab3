package ru.kfu.android_lab_third_course

import android.app.Activity
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.Wallet.WalletOptions
import com.google.android.gms.wallet.WalletConstants
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class GooglePayHelper {
    companion object {
        private val environment: Int
            get() = if (BuildConfig.DEBUG) {
                WalletConstants.ENVIRONMENT_TEST
            } else {
                WalletConstants.ENVIRONMENT_PRODUCTION
            }

        private fun getAllowedCardNetworks(): JSONArray {
            return JSONArray()
                .put("AMEX")
                .put("DISCOVER")
                .put("INTERAC")
                .put("JCB")
                .put("MASTERCARD")
                .put("VISA")
        }

        @Throws(JSONException::class)
        private fun getBaseRequest(): JSONObject? {
            return JSONObject().put("apiVersion", 2).put("apiVersionMinor", 0)
        }

        @Throws(JSONException::class)
        private fun getGatewayTokenizationSpecification(): JSONObject? {
            return object : JSONObject() {
                init {
                    put("type", "PAYMENT_GATEWAY")
                    put("parameters", object : JSONObject() {
                        init {
                            put("gateway", "example")
                            put("gatewayMerchantId", "exampleGatewayMerchantId")
                        }
                    })
                }
            }
        }

        private fun getAllowedCardAuthMethods(): JSONArray? {
            return JSONArray()
                .put("PAN_ONLY")
                .put("CRYPTOGRAM_3DS")
        }

        @Throws(JSONException::class)
        private fun getBaseCardPaymentMethod(): JSONObject? {
            val cardPaymentMethod = JSONObject()
            cardPaymentMethod.put("type", "CARD")
            val parameters = JSONObject()
            parameters.put("allowedAuthMethods", getAllowedCardAuthMethods())
            parameters.put("allowedCardNetworks", getAllowedCardNetworks())
            // Optionally, you can add billing address/phone number associated with a CARD payment method.
            parameters.put("billingAddressRequired", true)
            val billingAddressParameters = JSONObject()
            billingAddressParameters.put("format", "FULL")
            parameters.put("billingAddressParameters", billingAddressParameters)
            cardPaymentMethod.put("parameters", parameters)
            return cardPaymentMethod
        }

        @Throws(JSONException::class)
        private fun getCardPaymentMethod(): JSONObject? {
            val cardPaymentMethod = getBaseCardPaymentMethod()
            cardPaymentMethod!!.put(
                "tokenizationSpecification",
                getGatewayTokenizationSpecification()
            )
            return cardPaymentMethod
        }

        fun createPaymentsClient(activity: Activity?): PaymentsClient? {
            val walletOptions =
                WalletOptions.Builder().setEnvironment(environment).build()
            return Wallet.getPaymentsClient(activity!!, walletOptions)
        }

        fun getIsReadyToPayRequest(): JSONObject? {
            return try {
                val isReadyToPayRequest = getBaseRequest()
                isReadyToPayRequest!!.put(
                    "allowedPaymentMethods", JSONArray().put(getBaseCardPaymentMethod())
                )
                isReadyToPayRequest
            } catch (e: JSONException) {
                null
            }
        }

        @Throws(JSONException::class)
        private fun getTransactionInfo(price: String): JSONObject? {
            val transactionInfo = JSONObject()
            transactionInfo.put("totalPrice", price)
            transactionInfo.put("totalPriceStatus", "FINAL")
            transactionInfo.put("countryCode", "RU")
            transactionInfo.put("currencyCode", "USD")
            return transactionInfo
        }

        @Throws(JSONException::class)
        private fun getMerchantInfo(): JSONObject? {
            return JSONObject().put("merchantName", "Example Merchant")
        }

        fun getPaymentDataRequest(price: String): JSONObject? {
            return try {
                val paymentDataRequest: JSONObject = getBaseRequest()!!
                paymentDataRequest.put(
                    "allowedPaymentMethods", JSONArray().put(getCardPaymentMethod())
                )
                paymentDataRequest.put("transactionInfo", getTransactionInfo(price))
                paymentDataRequest.put("merchantInfo", getMerchantInfo())
                paymentDataRequest
            } catch (e: JSONException) {
                null
            }
        }
    }
}