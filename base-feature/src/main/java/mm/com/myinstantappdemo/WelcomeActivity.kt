package mm.com.myinstantappdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.btnGoNext

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btnGoNext.setOnClickListener {
            val i = Intent(this, DetailActivity::class.java)
            startActivity(i)
        }
    }
}