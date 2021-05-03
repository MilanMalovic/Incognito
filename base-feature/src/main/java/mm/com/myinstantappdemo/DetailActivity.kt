package mm.com.myinstantappdemo

import android.animation.ValueAnimator
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.Global
import android.provider.Settings.System
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.skydoves.balloon.BalloonAnimation.OVERSHOOT
import com.skydoves.balloon.createBalloon
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_detail.infoBtnAir
import kotlinx.android.synthetic.main.activity_detail.infoBtnBT
import kotlinx.android.synthetic.main.activity_detail.infoBtnGPS
import kotlinx.android.synthetic.main.activity_detail.infoBtnMD
import kotlinx.android.synthetic.main.activity_detail.infoBtnWiFi
import kotlinx.android.synthetic.main.activity_detail.ivDonate
import kotlinx.android.synthetic.main.activity_detail.rlBlueTooth
import kotlinx.android.synthetic.main.activity_detail.rlGps
import kotlinx.android.synthetic.main.activity_detail.rlGsmGprs
import kotlinx.android.synthetic.main.activity_detail.rlMobileData
import kotlinx.android.synthetic.main.activity_detail.tvAnonymous
import kotlinx.android.synthetic.main.activity_detail.tvBtStatus
import kotlinx.android.synthetic.main.activity_detail.tvGpsStatus
import kotlinx.android.synthetic.main.activity_detail.tvMDStatus
import kotlinx.android.synthetic.main.activity_detail.tvMobileDataStatus
import kotlinx.android.synthetic.main.activity_detail.tvWiFiStatus
import kotlinx.android.synthetic.main.activity_detail.wiFi
import mm.com.myinstantappdemo.tools.gone
import mm.com.myinstantappdemo.tools.show
import mm.com.myinstantappdemo.tools.showExitAlertDialog
import mm.com.myinstantappdemo.wifi.lib.ReactiveWifi

class DetailActivity : AppCompatActivity() {

    private var batteryDisposable: Disposable? = null
    private var wifiStateSubscription: Disposable? = null
    private var animator = ValueAnimator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        wiFi.setOnClickListener {
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
            startActivity(intent)
        }

        ivDonate.setOnClickListener {
            val intent = Intent(this, DonateActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
        }

        rlGps.setOnClickListener {
            startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0)
        }

        rlGsmGprs.setOnClickListener {
            val intent = Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS)
            startActivity(intent)
        }

        rlMobileData.setOnClickListener {
            val intent = Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS)
            startActivity(intent)
        }
        rlBlueTooth.setOnClickListener {
            val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
            startActivity(intent)
        }
        if (tvWiFiStatus.text.toString() == "Invisible" && tvMobileDataStatus.text.toString() == "Invisible" && tvGpsStatus.text.toString() == "Invisible" && tvBtStatus.text.toString() == "Invisible" && tvMDStatus.text.toString() == "Visible") {
            tvAnonymous.show()
        } else {
            tvAnonymous.gone()
        }
        onClickInfoBtn()
    }

    private fun onClickInfoBtn() {
        infoBtnMD.setOnClickListener {
            val balloon = createBalloon(this@DetailActivity) {
                setArrowVisible(false)
                setWidthRatio(1.0f)
                setTextSize(15f)
                setCornerRadius(10f)
                setWidthRatio(1.0f)
                setMarginLeft(10)
                setMarginRight(40)
                setPadding(10)
                setText("You should disable Mobile Data to make this item invisible")
                setTextColor(ContextCompat.getColor(this@DetailActivity, R.color.blackOrWhite))
                setBackgroundColor(ContextCompat.getColor(this@DetailActivity, R.color.background))
                setBalloonAnimation(OVERSHOOT)
                setLifecycleOwner(this@DetailActivity)
            }
            balloon.showAlignTop(rlMobileData, 0, 15)
            balloon.setOnBalloonClickListener { balloon.dismiss() }
        }

        infoBtnAir.setOnClickListener {
            val balloon = createBalloon(this@DetailActivity) {
                setWidthRatio(1.0f)
                setArrowVisible(false)
                setTextSize(15f)
                setCornerRadius(10f)
                setWidthRatio(1.0f)
                setMarginLeft(10)
                setMarginRight(40)
                setPadding(10)
                setText("You should enable Air Plane mode to make this item invisible")
                setTextColor(ContextCompat.getColor(this@DetailActivity, R.color.blackOrWhite))
                setBackgroundColor(ContextCompat.getColor(this@DetailActivity, R.color.background))
                setBalloonAnimation(OVERSHOOT)
                setLifecycleOwner(this@DetailActivity)
            }
            balloon.showAlignTop(rlGsmGprs, 0, 15)
            balloon.setOnBalloonClickListener { balloon.dismiss() }
        }

        infoBtnWiFi.setOnClickListener {
            val balloon = createBalloon(this@DetailActivity) {
                setWidthRatio(1.0f)
                setTextSize(15f)
                setArrowVisible(false)
                setCornerRadius(10f)
                setWidthRatio(1.0f)
                setMarginLeft(10)
                setMarginRight(40)
                setPadding(10)
                setText("You should disable Wi-fi to make this item invisible")
                setTextColor(ContextCompat.getColor(this@DetailActivity, R.color.blackOrWhite))
                setBackgroundColor(ContextCompat.getColor(this@DetailActivity, R.color.background))
                setBalloonAnimation(OVERSHOOT)
                setLifecycleOwner(this@DetailActivity)
            }
            balloon.showAlignTop(wiFi, 0, 15)
            balloon.setOnBalloonClickListener { balloon.dismiss() }
        }

        infoBtnBT.setOnClickListener {
            val balloon = createBalloon(this@DetailActivity) {
                setWidthRatio(1.0f)
                setArrowVisible(false)
                setTextSize(15f)
                setCornerRadius(10f)
                setWidthRatio(1.0f)
                setMarginLeft(10)
                setMarginRight(40)
                setPadding(10)
                setText("You should disable Bluetooth to make this item invisible")
                setTextColor(ContextCompat.getColor(this@DetailActivity, R.color.blackOrWhite))
                setBackgroundColor(ContextCompat.getColor(this@DetailActivity, R.color.background))
                setBalloonAnimation(OVERSHOOT)
                setLifecycleOwner(this@DetailActivity)
            }
            balloon.showAlignTop(rlBlueTooth, 0, 15)
            balloon.setOnBalloonClickListener { balloon.dismiss() }
        }


        infoBtnGPS.setOnClickListener {
            val balloon = createBalloon(this@DetailActivity) {
                setWidthRatio(1.0f)
                setTextSize(15f)
                setArrowVisible(false)
                setCornerRadius(10f)
                setWidthRatio(1.0f)
                setMarginLeft(10)
                setMarginRight(40)
                setPadding(10)
                setText("You should disable GPS Location to make this item invisible")
                setTextColor(ContextCompat.getColor(this@DetailActivity, R.color.blackOrWhite))
                setBackgroundColor(ContextCompat.getColor(this@DetailActivity, R.color.background))
                setBalloonAnimation(OVERSHOOT)
                setLifecycleOwner(this@DetailActivity)
            }
            balloon.showAlignTop(infoBtnGPS, 0, 15)
            balloon.setOnBalloonClickListener { balloon.dismiss() }
        }
    }

    /**
     * @return null if unconfirmed
     */
    fun isMobileDataEnabled(): Boolean? {
        val connectivityService = getSystemService(CONNECTIVITY_SERVICE)
        val cm = connectivityService as ConnectivityManager
        return try {
            val c = Class.forName(cm.javaClass.name)
            val m = c.getDeclaredMethod("getMobileDataEnabled")
            m.isAccessible = true
            return m.invoke(cm) as Boolean
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onResume() {
        super.onResume()
        checkBlueToothStatus()
        startWifiStateSubscription()


        if (isMobileDataEnabled()!!) {
            tvMDStatus.text = "Visible"
            tvMDStatus.setTextColor(ContextCompat.getColor(this, R.color.red))
            infoBtnMD.show()
        } else if (!isMobileDataEnabled()!!) {
            tvMDStatus.text = "Invisible"
            infoBtnMD.gone()
            tvMDStatus.setTextColor(ContextCompat.getColor(this, R.color.green))
        }


        if (isLocationEnabled(this)) {
            tvGpsStatus.setTextColor(ContextCompat.getColor(this, R.color.red))
            tvGpsStatus.text = "Visible"
            infoBtnGPS.show()
        } else {
            infoBtnGPS.gone()
            tvGpsStatus.setTextColor(ContextCompat.getColor(this, R.color.green))
            tvGpsStatus.text = "Invisible"
        }


        if (isAirplaneModeOn()) {
            tvMobileDataStatus.text = "Invisible"
            infoBtnAir.gone()
            tvMobileDataStatus.setTextColor(ContextCompat.getColor(this, R.color.green))
        } else {
            infoBtnAir.show()
            tvMobileDataStatus.setTextColor(ContextCompat.getColor(this, R.color.red))
            tvMobileDataStatus.text = "Visible"
        }


        if (tvWiFiStatus.text.toString() == "Invisible" && tvMobileDataStatus.text.toString() == "Invisible" && tvGpsStatus.text.toString() == "Invisible" && tvBtStatus.text.toString() == "Invisible" && tvMDStatus.text.toString()
            == "Invisible"
        ) {
            tvAnonymous.show()
        } else {
            tvAnonymous.gone()
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
                        infoBtnWiFi.show()
                        tvAnonymous.gone()
                    }
                    "enabling" -> {
                        tvWiFiStatus.text = "Visible"
                        infoBtnWiFi.show()
                        tvWiFiStatus.setTextColor(ContextCompat.getColor(this, R.color.red))
                        tvAnonymous.gone()
                    }
                    "disabled" -> {
                        infoBtnWiFi.gone()
                        tvWiFiStatus.text = "Invisible"
                        tvWiFiStatus.setTextColor(ContextCompat.getColor(this, R.color.green))
                    }
                }
            }
    }

    private fun showMatrixStyleText(text: String, textView: TextView) {
        val textLength = text.length - 1
        animator = ValueAnimator.ofInt(0, textLength).apply {

            var index = -1
            interpolator = LinearInterpolator()
            duration = 4000
            addUpdateListener { valueAnimator ->
                val currentCharIndex = valueAnimator.animatedValue as Int
                if (index != currentCharIndex) {
                    val currentChar = text[currentCharIndex]
                    textView.text =
                        textView.text.toString().plus(currentChar.toString())
                }
                index = currentCharIndex
            }

        }
        animator.start()
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
        return System.getInt(contentResolver, Global.AIRPLANE_MODE_ON, 0) != 0
    }

    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    private fun checkBlueToothStatus() {
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        tvBtStatus.text = "Invisible"
        if (mBluetoothAdapter == null) {
            tvBtStatus.text = "Invisible"
            tvBtStatus.setTextColor(ContextCompat.getColor(this, R.color.green))
        } else if (!mBluetoothAdapter.isEnabled) {
            tvBtStatus.text = "Invisible"
            infoBtnBT.gone()
            tvBtStatus.setTextColor(ContextCompat.getColor(this, R.color.green))
        } else {
            // Bluetooth is enabled
            tvBtStatus.text = "Visible"
            infoBtnBT.show()
            tvBtStatus.setTextColor(ContextCompat.getColor(this, R.color.red))
        }
    }

    override fun onBackPressed() {
        this.showExitAlertDialog { finishAffinity() }
    }
}