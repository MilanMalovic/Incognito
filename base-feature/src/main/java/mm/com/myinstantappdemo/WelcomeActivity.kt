package mm.com.myinstantappdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.rlBtnNext
import mm.com.myinstantappdemo.tools.showExitAlertDialog

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        rlBtnNext.setOnClickListener {
            val i = Intent(this, DetailActivity::class.java)
            startActivity(i)
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
        }
    }

    override fun onBackPressed() {
        this.showExitAlertDialog { finishAffinity() }
    }
}