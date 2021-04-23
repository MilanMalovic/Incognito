package mm.com.myinstantappdemo.wifi

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_wifi.access_points
import kotlinx.android.synthetic.main.dialog_wifi.wifi_signal_level
import kotlinx.android.synthetic.main.dialog_wifi.wifi_state_change
import mm.com.myinstantappdemo.R
import mm.com.myinstantappdemo.wifi.lib.AccessRequester
import mm.com.myinstantappdemo.wifi.lib.ReactiveWifi

class WiFiActivity : AppCompatActivity() {

    private var wifiSubscription: Disposable? = null
    private var signalLevelSubscription: Disposable? = null
    private var supplicantSubscription: Disposable? = null
    private var wifiInfoSubscription: Disposable? = null
    private var wifiStateSubscription: Disposable? = null

    companion object {

        private val PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1000
        private val TAG = "ReactiveWifi"
        private val WIFI_SIGNAL_LEVEL_MESSAGE = "WiFi signal level: "
        private val WIFI_STATE_CHANGE_MESSAGE = "WiFi State: "
        val IS_PRE_M_ANDROID = Build.VERSION.SDK_INT < Build.VERSION_CODES.M
    }

    override fun onResume() {
        super.onResume()

        if (!isFineOrCoarseLocationPermissionGranted()) {
            requestCoarseLocationPermission()
        } else if (isFineOrCoarseLocationPermissionGranted() || IS_PRE_M_ANDROID) {
            startWifiAccessPointsSubscription()
        }

        startWifiSignalLevelSubscription()
        startSupplicantSubscription()
        startWifiInfoSubscription()
        startWifiStateSubscription()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_wifi)
    }

    private fun startWifiSignalLevelSubscription() {
        signalLevelSubscription = ReactiveWifi
            .observeWifiSignalLevel(this)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { level ->
                val description = level.description
                wifi_signal_level.text = WIFI_SIGNAL_LEVEL_MESSAGE + description
            }
    }

    @SuppressLint("MissingPermission")
    private fun startWifiAccessPointsSubscription() {
        if (!AccessRequester.isLocationEnabled(this)) {
            AccessRequester.requestLocationAccess(this)
            return
        }

        wifiSubscription = ReactiveWifi
            .observeWifiAccessPoints(this)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { scanResults ->
                displayAccessPoints(scanResults)
            }
    }

    private fun startSupplicantSubscription() {
        supplicantSubscription = ReactiveWifi
            .observeSupplicantState(this)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { supplicantState ->
                Log.d("ReactiveWifi", "New supplicant state: $supplicantState")
            }
    }

    private fun startWifiInfoSubscription() {
        wifiInfoSubscription = ReactiveWifi
            .observeWifiAccessPointChanges(this)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { wifiInfo ->
                Log.d("ReactiveWifi", "New BSSID: " + wifiInfo.bssid)
            }
    }

    private fun startWifiStateSubscription() {
        wifiStateSubscription = ReactiveWifi
            .observeWifiStateChange(this)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { wifiState ->
                Log.d("ReactiveWifi", wifiState.description)
                wifi_state_change.text = WIFI_STATE_CHANGE_MESSAGE + wifiState.description
            }
    }

    private fun displayAccessPoints(scanResults: List<ScanResult>) {
        val ssids = scanResults.map { it.SSID }

        val itemLayoutId = android.R.layout.simple_list_item_1
        access_points.adapter = ArrayAdapter(this, itemLayoutId, ssids)
    }

    override fun onPause() {
        super.onPause()
        safelyUnsubscribe(wifiSubscription, signalLevelSubscription, supplicantSubscription,
            wifiInfoSubscription, wifiStateSubscription)
    }

    private fun safelyUnsubscribe(vararg subscriptions: Disposable?) {
        subscriptions
            .filterNotNull()
            .filterNot { it.isDisposed }
            .forEach { it.dispose() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val isCoarseLocation = requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
        val permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED

        if (isCoarseLocation && permissionGranted && wifiSubscription == null) {
            startWifiAccessPointsSubscription()
        }
    }

    private fun requestCoarseLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(permission.ACCESS_COARSE_LOCATION),
                PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION)
        }
    }

    private fun isFineOrCoarseLocationPermissionGranted(): Boolean {
        val isAndroidMOrHigher = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        val isFineLocationPermissionGranted = isGranted(permission.ACCESS_FINE_LOCATION)
        val isCoarseLocationPermissionGranted = isGranted(permission.ACCESS_COARSE_LOCATION)

        return isAndroidMOrHigher && (isFineLocationPermissionGranted || isCoarseLocationPermissionGranted)
    }

    private fun isGranted(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
    }
}