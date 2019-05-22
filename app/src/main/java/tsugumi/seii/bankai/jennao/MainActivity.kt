package tsugumi.seii.bankai.jennao

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

// use R8 to replace proguard TODO
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rand = Random()

        spinMe.setOnClickListener {
            val min = 120
            val max = 380

            spinWheel((rand.nextInt(max - min + 1) + min).toLong())
        }
    }

    private fun spinWheel(travelAngle: Long){

        roulette.spin(travelAngle)
        Log.i("Spin Roulette","Angle travelled: $travelAngle")

    }
}
