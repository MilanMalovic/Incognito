package mm.com.myinstantappdemo

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.SkuType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.Purchase.PurchaseState
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import kotlinx.android.synthetic.main.activity_donate.rlBurger
import kotlinx.android.synthetic.main.activity_donate.rlCoffe
import kotlinx.android.synthetic.main.activity_donate.rlHuge
import kotlinx.android.synthetic.main.activity_donate.rlLunch
import java.util.ArrayList

class DonateActivity : AppCompatActivity(), PurchasesUpdatedListener {

    private var billingClient: BillingClient? = null
    private val skuList: ArrayList<String> = ArrayList()
    private var mSkuDetails: SkuDetails? = null
    private val skuCoffee = "coffee"
    private val skuBurger = "burger"
    private val skuLunch = "lunch"
    private val skuHuge = "huge_donation"

    override fun onStart() {
        super.onStart()
        skuList.add(skuCoffee)
        skuList.add(skuBurger)
        skuList.add(skuHuge)
        skuList.add(skuLunch)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donate)
        setupBillingClient()
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build()
        billingClient!!.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    // The BillingClient is setup successfully
                    loadAllSKUs()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, @Nullable purchases: List<Purchase>?) {
        val responseCode = billingResult.responseCode
        if (responseCode == BillingResponseCode.OK
            && purchases != null
        ) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (responseCode == BillingResponseCode.USER_CANCELED) {
            //         Toast.makeText(requireContext(), "user canceled purchase", Toast.LENGTH_SHORT).show()
            // Handle an error caused by a user cancelling the purchase flow.
            //Log.d(TAG, "User Canceled" + responseCode);
        } else if (responseCode == BillingResponseCode.ITEM_ALREADY_OWNED) {
            //       Toast.makeText(requireContext(), "already owned item", Toast.LENGTH_SHORT).show()
            //mSharedPreferences.edit().putBoolean(getResources().getString(R.string.pref_remove_ads_key), true).commit();
            ///setAdFree(true);
        } else if (responseCode == BillingResponseCode.ITEM_UNAVAILABLE) {
            //     Toast.makeText(requireContext(), "This item unavailable: $sku", Toast.LENGTH_SHORT).show()
        } else {

            Log.i(ContentValues.TAG, "Other code$responseCode")
            // Handle any other error codes.
            //   Toast.makeText(requireContext(), responseCode, Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadAllSKUs() {
        if (billingClient!!.isReady) {
            //    Toast.makeText(requireContext(), "billing client ready", Toast.LENGTH_SHORT).show()
            val params = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(SkuType.INAPP)
                .build()
            billingClient!!.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->

                if (billingResult.responseCode == BillingResponseCode.OK
                    && skuDetailsList!!.isNotEmpty()
                ) {
                    for (skuDetailsObject in skuDetailsList) {
                        val skuDetails = skuDetailsObject as SkuDetails
                        //    Toast.makeText(requireContext(), skuDetails.sku + "\n-this is the sku item-\n" + sku, Toast.LENGTH_SHORT).show()
                        when (skuDetails.sku) {
                            skuCoffee -> {
                                mSkuDetails = skuDetails
                                rlCoffe.setOnClickListener {
                                    val billingFlowParams = BillingFlowParams
                                        .newBuilder()
                                        .setSkuDetails(skuDetails)
                                        .build()
                                    billingClient!!.launchBillingFlow(this, billingFlowParams)
                                }
                            }
                            skuHuge -> {
                                mSkuDetails = skuDetails
                                rlHuge.setOnClickListener {
                                    val billingFlowParams = BillingFlowParams
                                        .newBuilder()
                                        .setSkuDetails(skuDetails)
                                        .build()
                                    billingClient!!.launchBillingFlow(this, billingFlowParams)
                                }
                            }
                            skuLunch -> {
                                mSkuDetails = skuDetails
                                rlLunch.setOnClickListener {
                                    val billingFlowParams = BillingFlowParams
                                        .newBuilder()
                                        .setSkuDetails(skuDetails)
                                        .build()
                                    billingClient!!.launchBillingFlow(this, billingFlowParams)
                                }
                            }
                            skuBurger -> {
                                mSkuDetails = skuDetails
                                rlBurger.setOnClickListener {

                                    val billingFlowParams = BillingFlowParams
                                        .newBuilder()
                                        .setSkuDetails(skuDetails)
                                        .build()
                                    billingClient!!.launchBillingFlow(this, billingFlowParams)

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.sku == skuCoffee && purchase.purchaseState == PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams =
                    ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
                billingClient!!.consumeAsync(acknowledgePurchaseParams) { billingResult, _ ->
                    if (billingResult.responseCode == BillingResponseCode.OK) {
                        Toast.makeText(this, "Purchase Acknowledged", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        if (purchase.sku == skuBurger && purchase.purchaseState == PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams =
                    ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
                billingClient!!.consumeAsync(acknowledgePurchaseParams) { billingResult, _ ->
                    if (billingResult.responseCode == BillingResponseCode.OK) {
                        Toast.makeText(this, "Purchase Acknowledged", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        if (purchase.sku == skuLunch && purchase.purchaseState == PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams =
                    ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
                billingClient!!.consumeAsync(acknowledgePurchaseParams) { billingResult, _ ->
                    if (billingResult.responseCode == BillingResponseCode.OK) {
                        Toast.makeText(this, "Purchase Acknowledged", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        if (purchase.sku == skuHuge && purchase.purchaseState == PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams =
                    ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
                billingClient!!.consumeAsync(acknowledgePurchaseParams) { billingResult, _ ->
                    if (billingResult.responseCode == BillingResponseCode.OK) {
                        Toast.makeText(this, "Purchase Acknowledged", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
    }
}