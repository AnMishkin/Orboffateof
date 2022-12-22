package download.mishkindeveloper.orboffate


//импорт с сайта//
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import download.mishkindeveloper.orboffate.databinding.ActivityMainBinding
import java.lang.Math.sqrt
import java.util.*


//конец импорта с сайта//

class MainActivity : AppCompatActivity() {
    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f
var numberr : Int = 0


    lateinit var binding: ActivityMainBinding

    lateinit var mAdView: AdView
    private var mInterstitialAd: InterstitialAd? = null


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//постоянно горит экран
hasWindowFocus()

//включение банерной рекламы
        initAdMob()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        Objects.requireNonNull(sensorManager)!!.registerListener(
            sensorListener, sensorManager!!
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
        )
        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH


        binding.policy.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://mishkindeveloper.download/pages-Privacy-Policy-orb.html"))
            startActivity(browserIntent)
        }
        binding.imageInfo.setOnClickListener {
            var intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

//межстраничная реклама
        zagruzkaReclamy()

    }

//слушайтель встряхивания
    private val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration
            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.85f + delta

//значение 8 норм
            if (acceleration > 12) {

                binding.tvResult.text = getPrediction()

//межстраничная реклама каждые 3 предсказания
                when (numberr <= 3) {
                    true->numberr++
                    false->reklama()
                }
                //numberr++
//конец - межстраничная реклама каждые 3 предсказания

                randomMusic()

//убрать политику конфиденциальности и инфо при встряхивании
                binding.policy.isVisible = false
                binding.imageInfo.isVisible = false
                //Toast.makeText(applicationContext, "Shake event detected", Toast.LENGTH_SHORT).show()
            }
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

//банерная реклама
    private fun initAdMob(){
        MobileAds.initialize(this){}
        mAdView = binding.adView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(sensorListener, sensorManager!!.getDefaultSensor(
            Sensor .TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
        )
        binding.adView.resume()
    }

    override fun onStop() {
       sensorManager!!.unregisterListener(sensorListener)
        super.onStop()
    }

    override fun onPause() {
        sensorManager!!.unregisterListener(sensorListener)
        super.onPause()
        binding.adView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.adView.destroy()
    }

    private fun reklama() {
        zagruzkaReclamy()
        mInterstitialAd?.show(this)
        numberr = 0
    }

//загрузка межстраничная реклама
    private fun zagruzkaReclamy(){

    var adRequest = AdRequest.Builder().build()
    InterstitialAd.load(this, "ca-app-pub-3971991853344828/3267089682", adRequest, object :
        InterstitialAdLoadCallback() {
        override fun onAdFailedToLoad(adError: LoadAdError) {
            mInterstitialAd = null
        }
        override fun onAdLoaded(interstitialAd: InterstitialAd) {
            mInterstitialAd = interstitialAd
        }
    })
    mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() {
        }

        override fun onAdShowedFullScreenContent() {
            mInterstitialAd = null
        }
    }
}

////предсказание
    private fun getPrediction() : String {
        return resources.getStringArray(R.array.orb)[randomNumber()]
    }

//рандом номер
    private fun randomNumber() : Int {
        val size = resources.getStringArray(R.array.orb).size-1
        return (0..size).random()
    }

//рандом музыка
     private fun randomMusic() {
        var list = listOf(R.raw.first,R.raw.two,R.raw.tree,R.raw.four,R.raw.five).random()
        val mediaPlayer by lazy { MediaPlayer.create(this, list)}
        mediaPlayer.start()
    }
}





