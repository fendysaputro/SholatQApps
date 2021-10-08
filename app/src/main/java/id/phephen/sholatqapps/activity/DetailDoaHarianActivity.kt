package id.phephen.sholatqapps.activity

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import id.phephen.sholatqapps.BuildConfig
import id.phephen.sholatqapps.R
import id.phephen.sholatqapps.model.ModelDoaHarian
import java.util.*


class DetailDoaHarianActivity : AppCompatActivity() {

    private var titleDoa: String? = null
    private var latinDoa: String? = null
    private var arabicDoa: String? = null
    private var translateDoa: String? = null
    private var modelDoaHarian: ModelDoaHarian? = null
    private var textToSpeech: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_doa_harian)

        var toolbarDoaTitle: androidx.appcompat.widget.Toolbar? = null

        toolbarDoaTitle = androidx.appcompat.widget.Toolbar(this)

        toolbarDoaTitle.title = null
        setSupportActionBar(toolbarDoaTitle)
        if (BuildConfig.DEBUG && supportActionBar == null) {
            error("Assertion failed")
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        toolbarDoaTitle = findViewById(R.id.toolbar_detail_doa)
        toolbarDoaTitle.setNavigationOnClickListener {
            finish()
        }

        modelDoaHarian = intent.getSerializableExtra("detailDoa") as ModelDoaHarian
        if (modelDoaHarian != null) {
            titleDoa = modelDoaHarian!!.title
            latinDoa = modelDoaHarian!!.latin
            arabicDoa = modelDoaHarian!!.arabic
            translateDoa = modelDoaHarian!!.translation
        }

        val tvTitleDoa = findViewById<TextView>(R.id.txt_title_doa)
        val tvHeader = findViewById<TextView>(R.id.tvHeader_title_doa)
        val tvArabic = findViewById<TextView>(R.id.txt_arabic)
        val tvArti = findViewById<TextView>(R.id.txt_arti)

        tvTitleDoa.text = titleDoa
        tvHeader.text = titleDoa
        tvArabic.text = arabicDoa
        tvArti.text = translateDoa

        val fabPlay = findViewById<FloatingActionButton>(R.id.fab_play_doa)
        val fabStop = findViewById<FloatingActionButton>(R.id.fab_stop_doa)

        fabStop!!.visibility = View.GONE
        fabPlay!!.visibility = View.VISIBLE

        textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener {
            if (it == TextToSpeech.SUCCESS) {
                val loc = Locale("ar")
                val result = textToSpeech?.setLanguage(Locale(loc.toLanguageTag()))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Failed to initialize Text to Speech", Toast.LENGTH_SHORT).show()
                }
                textToSpeech?.setSpeechRate(0.6f)
                val voiceName: String = loc.toLanguageTag()
                val voice =
                    Voice(voiceName, loc, Voice.QUALITY_HIGH, Voice.LATENCY_HIGH, false, null)
                textToSpeech?.voice = voice
            }
        })

        fabPlay.setOnClickListener {
            fabPlay.visibility = View.GONE
            fabStop.visibility = View.VISIBLE
            textToSpeech?.speak(arabicDoa, TextToSpeech.QUEUE_FLUSH, null, "")
        }

    }

    override fun onPause() {
        if (textToSpeech != null) {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
        super.onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}