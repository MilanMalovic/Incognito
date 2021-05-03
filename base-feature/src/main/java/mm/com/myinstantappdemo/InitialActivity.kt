package mm.com.myinstantappdemo

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_init.logo

class InitialActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)
        setStartingAnimation()
    }

    private fun setStartingAnimation() {
        val animRotate = AnimationUtils.loadAnimation(this, R.anim.anim_pulse)
        logo.startAnimation(animRotate)
        animRotate.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(arg0: Animation) {
            }
            override fun onAnimationRepeat(arg0: Animation) {}
            override fun onAnimationEnd(arg0: Animation) {
                val i = Intent(this@InitialActivity, DetailActivity::class.java)
                startActivity(i)
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
            }
        })
    }

}