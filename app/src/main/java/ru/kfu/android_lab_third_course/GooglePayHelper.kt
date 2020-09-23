package ru.kfu.android_lab_third_course

import android.app.Activity
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.*

class GooglePayHelper {
    companion object {
        private val supportedPaymentMethods = listOf(
            WalletConstants.PAYMENT_METHOD_CARD,
            WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD
        )
        private val allowedCardNetworks = listOf(
            WalletConstants.CARD_NETWORK_AMEX,
            WalletConstants.CARD_NETWORK_JCB,
            WalletConstants.CARD_NETWORK_DISCOVER,
            WalletConstants.CARD_NETWORK_MASTERCARD,
            WalletConstants.CARD_NETWORK_VISA
        )
        private val environment: Int
            get() = if (BuildConfig.DEBUG) {
                WalletConstants.ENVIRONMENT_TEST
            } else {
                WalletConstants.ENVIRONMENT_PRODUCTION
            }

        fun createPaymentsClient(activity: Activity): PaymentsClient {
            val walletOptions = Wallet.WalletOptions.Builder()
                .setEnvironment(environment)
                .build()

            return Wallet.getPaymentsClient(activity, walletOptions)
        }

        fun isReadyToPay(client: PaymentsClient): Task<Boolean> {
            val request = IsReadyToPayRequest.newBuilder()
                .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_CARD)
                .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD)
                .build()

            return client.isReadyToPay(request)
        }

        fun createPaymentDataRequest(price: Double): PaymentDataRequest {
            val request = PaymentDataRequest.newBuilder()
                .setTransactionInfo(getTransactionInfo(price))
                .addAllowedPaymentMethods(supportedPaymentMethods)
                .setCardRequirements(
                    CardRequirements.newBuilder()
                        .addAllowedCardNetworks(allowedCardNetworks)
                        .build()
                )
            request.setPaymentMethodTokenizationParameters(createTokenizationParameters())

            return request.build()
        }

        private fun getTransactionInfo(price: Double) =
            TransactionInfo.newBuilder()
                .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                .setTotalPrice(price.toString())
                .setCurrencyCode("USD")
                .build()

        private fun createTokenizationParameters(): PaymentMethodTokenizationParameters =
            PaymentMethodTokenizationParameters.newBuilder()
                .setPaymentMethodTokenizationType(WalletConstants.PAYMENT_METHOD_TOKENIZATION_TYPE_PAYMENT_GATEWAY)
                .build()

    }
}