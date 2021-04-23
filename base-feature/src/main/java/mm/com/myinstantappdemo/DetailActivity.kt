package mm.com.myinstantappdemo

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_detail.tvAirplaneMode
import kotlinx.android.synthetic.main.activity_detail.tvBatteryStatus
import kotlinx.android.synthetic.main.activity_detail.tvBtStatus
import kotlinx.android.synthetic.main.activity_detail.tvGpsStatus
import kotlinx.android.synthetic.main.activity_detail.tvMobileData
import kotlinx.android.synthetic.main.activity_detail.tvWiFiStatus
import kotlinx.android.synthetic.main.activity_detail.wiFi
import mm.com.myinstantappdemo.battery.RxBattery
import mm.com.myinstantappdemo.wifi.WiFiActivity
import mm.com.myinstantappdemo.wifi.lib.ReactiveWifi

class DetailActivity : AppCompatActivity() {

    private var batteryDisposable: Disposable? = null
    private var wifiStateSubscription: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        checkBlueToothStatus()
        isMobileDataEnabled2()
        checkBatteryStatus()
        startWifiStateSubscription()
        // tvWifi.text = (tvWifi.text.toString() + isOnline().toString())
        if (isLocationEnabled(this)) {
            tvGpsStatus.setTextColor(ContextCompat.getColor(this, R.color.red))
            tvGpsStatus.text = "Visible"
        } else {
            tvGpsStatus.setTextColor(ContextCompat.getColor(this, R.color.green))
            tvGpsStatus.text = "Invisible"
        }
        tvAirplaneMode.text = (tvAirplaneMode.text.toString() + isAirplaneModeOn().toString())


        wiFi.setOnClickListener {
            val i = Intent(this, WiFiActivity::class.java)
            startActivity(i)
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
        }
    }

    private fun startWifiStateSubscription() {
        tvWiFiStatus.text = "Invisible"
        tvWiFiStatus.setTextColor(ContextCompat.getColor(this, R.color.green))
        wifiStateSubscription = ReactiveWifi
            .observeWifiStateChange(this)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { wifiState ->
                Log.d("ReactiveWifi", wifiState.description)
                when (wifiState.description) {
                    "enabled" -> {
                        tvWiFiStatus.text = "Visible"
                        tvWiFiStatus.setTextColor(ContextCompat.getColor(this, R.color.red))
                    }
                    "disabled" -> {
                        tvWiFiStatus.text = "Invisible"
                        tvWiFiStatus.setTextColor(ContextCompat.getColor(this, R.color.green))
                    }
                }
            }
    }

    override fun onPause() {
        super.onPause()
        safelyUnsubscribe(wifiStateSubscription, batteryDisposable)
    }

    private fun safelyUnsubscribe(vararg subscriptions: Disposable?) {
        subscriptions
            .filterNotNull()
            .filterNot { it.isDisposed }
            .forEach { it.dispose() }
    }

    private fun isAirplaneModeOn(): Boolean {
        return Settings.System.getInt(contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) != 0
    }

    private fun isMobileDataEnabled2(): Boolean {
        var mobileDataEnabled = false // Assume disabled

        val cm = getSystemService(CONNECTIVITY_SERVICE)
        try {
            val cmClass = Class.forName(cm.javaClass.name)
            val method = cmClass.getDeclaredMethod("getMobileDataEnabled")
            method.isAccessible = true // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = method.invoke(cm) as Boolean
            tvMobileData.text = (tvMobileData.text.toString() + mobileDataEnabled)
        } catch (e: java.lang.Exception) {
            // Some problem accessible private API
            // TODO do whatever error handling you want here
        }
        return mobileDataEnabled
    }

    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    private fun checkBlueToothStatus() {
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
            tvBtStatus.text = "Not support"
            tvBtStatus.setTextColor(ContextCompat.getColor(this, R.color.green))
        } else if (!mBluetoothAdapter.isEnabled) {
            tvBtStatus.text = "Invisible"
            tvBtStatus.setTextColor(ContextCompat.getColor(this, R.color.green))
        } else {
            // Bluetooth is enabled
            tvBtStatus.text = "Visible"
            tvBtStatus.setTextColor(ContextCompat.getColor(this, R.color.red))
        }
    }

    private fun checkBatteryStatus() {
        batteryDisposable = RxBattery.observe(this)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                tvBatteryStatus.text =
                    (tvBatteryStatus.text.toString() + "\n" + "info level: ${it.level}" + "\n" + "Health: ${it.health()}" + "\n" + "Plugged: ${it.plugged()}" + "\n" + "Status: ${it.status()}")
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
    }
}