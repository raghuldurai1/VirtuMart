package com.example.pkart.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.pkart.databinding.FragmentCheckoutBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import android.os.Handler
import android.os.Looper

import java.util.UUID

class CheckoutFragment : Fragment() {

    private lateinit var binding: FragmentCheckoutBinding
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private var totalAmount = 0
    private var userName = ""
    private var userPhone = ""
    private var userAddress = ""
    private val orderedProducts = mutableListOf<String>()

    // Google Pay related variables
    private lateinit var paymentsClient: PaymentsClient
    private val loadPaymentDataRequestCode = 991

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false)

        // Initialize Google Pay API
        paymentsClient = Wallet.getPaymentsClient(
            requireActivity(),
            Wallet.WalletOptions.Builder().setEnvironment(WalletConstants.ENVIRONMENT_TEST).build()
        )

        // Get total amount and product names from arguments
        totalAmount = arguments?.getInt("totalAmount", 0) ?: 0
        orderedProducts.addAll(arguments?.getStringArrayList("productNames") ?: arrayListOf())

        // Display total amount
        binding.tvTotalAmount.text = "Total Amount to be paid: ₹$totalAmount"

        getUserDetails()

        // Cash on Delivery button
        binding.btnCod.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Confirm Order")
                .setMessage("Are you sure you want to proceed with Cash on Delivery?")
                .setPositiveButton("Yes") { _, _ -> completePayment("Cash on Delivery") }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // Google Pay button
        binding.btnGooglePay.setOnClickListener {
            payWithGooglePay()
        }

        return binding.root
    }

    private fun getUserDetails() {
        if (userId == null) return

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    userName = document.getString("name") ?: "Unknown"
                    userPhone = document.getString("phone") ?: "N/A"
                    userAddress = document.getString("address") ?: "No Address Found"

                    binding.tvUserInfo.text = "Name: $userName\nMobile: $userPhone\nAddress: $userAddress"
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load user details", Toast.LENGTH_SHORT).show()
            }
    }

    private fun completePayment(paymentMethod: String = "Cash on Delivery") {
        if (userId == null) return

        // Show Lottie animation and disable buttons
        binding.lottieLoading.visibility = View.VISIBLE
        binding.lottieLoading.playAnimation()
        binding.btnCod.isEnabled = false
        binding.btnGooglePay.isEnabled = false

        // Delay the payment process for 4 seconds to show the animation
        Handler(Looper.getMainLooper()).postDelayed({
            val paymentId = UUID.randomUUID().toString()
            val paymentData = hashMapOf(
                "name" to userName,
                "phone" to userPhone,
                "address" to userAddress,
                "amountToPay" to totalAmount,
                "orderedProducts" to orderedProducts,
                "paymentMethod" to paymentMethod,
                "adminMessage" to "Order placed, waiting for admin approval",
                "timestamp" to System.currentTimeMillis()
            )

            db.collection("users").document(userId)
                .collection("payment").document(paymentId).set(paymentData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Payment Successful!", Toast.LENGTH_SHORT).show()
                    clearCart()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Payment Failed!", Toast.LENGTH_SHORT).show()
                }
                .addOnCompleteListener {
                    // Hide Lottie animation and re-enable buttons after 4 seconds
                    binding.lottieLoading.visibility = View.GONE
                    binding.btnCod.isEnabled = true
                    binding.btnGooglePay.isEnabled = true
                }
        }, 1600) // 4-second delay before processing payment
    }



    private fun clearCart() {
        if (userId == null) return

        db.collection("users").document(userId).collection("cart")
            .get()
            .addOnSuccessListener { result ->
                val batch = db.batch()
                for (document in result) {
                    batch.delete(document.reference)
                }
                batch.commit()
                    .addOnSuccessListener {
                        // Navigate back or to order confirmation
                        requireActivity().onBackPressed()
                    }
            }
    }

    // Google Pay Implementation
    private fun payWithGooglePay() {
        val request = createPaymentDataRequest()
        try {
            AutoResolveHelper.resolveTask(
                paymentsClient.loadPaymentData(request),
                requireActivity(),
                loadPaymentDataRequestCode
            )
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Google Pay error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createPaymentDataRequest(): PaymentDataRequest {
        val paymentRequestJson = """
            {
              "apiVersion": 2,
              "apiVersionMinor": 0,
              "allowedPaymentMethods": [{
                "type": "CARD",
                "parameters": {
                  "allowedAuthMethods": ["PAN_ONLY", "CRYPTOGRAM_3DS"],
                  "allowedCardNetworks": ["VISA", "MASTERCARD"]
                },
                "tokenizationSpecification": {
                  "type": "PAYMENT_GATEWAY",
                  "parameters": {
                    "gateway": "example",
                    "gatewayMerchantId": "exampleGatewayMerchantId"
                  }
                }
              }],
              "merchantInfo": {
                "merchantName": "Virtumart"
              },
              "transactionInfo": {
                "totalPriceStatus": "FINAL",
                "totalPrice": "$totalAmount.00",
                "currencyCode": "INR"
              }
            }
        """.trimIndent()

        return PaymentDataRequest.fromJson(paymentRequestJson)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == loadPaymentDataRequestCode) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let { intent ->
                        PaymentData.getFromIntent(intent)?.let { paymentData ->
                            // Process the payment data
                            val json = paymentData.toJson()
                            try {
                                val paymentMethodJson = JSONObject(json)
                                    .optJSONObject("paymentMethodData")
                                    ?.optJSONObject("info")
                                val paymentNetwork = paymentMethodJson?.optString("cardNetwork") ?: "Google Pay"

                                // Complete the payment with Google Pay method
                                completePayment(paymentNetwork)
                            } catch (e: Exception) {
                                Toast.makeText(requireContext(), "Payment processing error", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                Activity.RESULT_CANCELED -> {
                    Toast.makeText(requireContext(), "Payment cancelled", Toast.LENGTH_SHORT).show()
                }
                AutoResolveHelper.RESULT_ERROR -> {
                    AutoResolveHelper.getStatusFromIntent(data)?.let { status ->
                        Toast.makeText(requireContext(), "Payment failed: ${status.statusMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}