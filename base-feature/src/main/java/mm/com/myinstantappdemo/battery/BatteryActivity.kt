package mm.com.myinstantappdemo.battery

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_battery.batteryMeter
import kotlinx.android.synthetic.main.activity_battery.tvBatteryLevel
import mm.com.myinstantappdemo.R

class BatteryActivity : AppCompatActivity() {

    private var batteryDisposable: Disposable? = null
    private var animator = ValueAnimator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battery)
        checkBatteryStatus()
    }

    private fun checkBatteryStatus() {
        batteryDisposable = RxBattery.observe(this)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                batteryMeter.chargeLevel = it.level
                showMatrixStyleText("attery level: " + it.level + "%" +"\n\n"+"Battery health: " + it.health().toString() +"\n\n"+ "Battery plug type: " + it.plugged().toString() + "\n\n" +"Charging status: " + it.status().toString() + "\n\n"+ "Battery temperature: " + it.temperature.toString().substring(0,2) + "C" + "\n\n" + "Battery voltage: " + it.voltage + "mV", tvBatteryLevel)
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
        safelyUnsubscribe(batteryDisposable)
    }

    private fun safelyUnsubscribe(vararg subscriptions: Disposable?) {
        subscriptions
            .filterNotNull()
            .filterNot { it.isDisposed }
            .forEach { it.dispose() }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
    }
}