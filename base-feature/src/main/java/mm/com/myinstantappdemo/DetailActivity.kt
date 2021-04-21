package mm.com.myinstantappdemo

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.location.LocationManagerCompat
import kotlinx.android.synthetic.main.activity_detail.tvAirplaneMode
import kotlinx.android.synthetic.main.activity_detail.tvBlueTooth
import kotlinx.android.synthetic.main.activity_detail.tvGPS
import kotlinx.android.synthetic.main.activity_detail.tvMobileData
import kotlinx.android.synthetic.main.activity_detail.tvWifi

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        checkBlueToothStatus()
        isMobileDataEnabled2()
        tvWifi.text = (tvWifi.text.toString() + isOnline().toString())
        tvGPS.text = (tvGPS.text.toString() + isLocationEnabled2(this).toString())
        tvAirplaneMode.text = (tvAirplaneMode.text.toString() + isAirplaneModeOn().toString())
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

    private fun isLocationEnabled2(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    private fun isOnline(): Boolean {
        val connMgr = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun checkBlueToothStatus() {
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
            tvBlueTooth.text = (tvBlueTooth.text.toString() + "Device not support bluetooth")
        } else if (!mBluetoothAdapter.isEnabled) {
            tvBlueTooth.text = (tvBlueTooth.text.toString() + "Disabled")
        } else {
            // Bluetooth is enabled
            tvBlueTooth.text = (tvBlueTooth.text.toString() + "Enabled")
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
    }
}