package tsugumi.seii.bankai.jennao

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rand = Random()

        spinMe.setOnClickListener {
            roulette.spin((rand.nextInt(360 - 0 + 1) + 0).toFloat())
        }
    }
}
