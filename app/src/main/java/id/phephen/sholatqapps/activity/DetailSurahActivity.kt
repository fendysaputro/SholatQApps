@file:Suppress("DEPRECATION")

package id.phephen.sholatqapps.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import id.phephen.sholatqapps.BuildConfig
import id.phephen.sholatqapps.R
import id.phephen.sholatqapps.adapter.AyatAdapter
import id.phephen.sholatqapps.model.ModelAyat
import id.phephen.sholatqapps.model.ModelSurah
import id.phephen.sholatqapps.networking.Api
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException

class DetailSurahActivity : AppCompatActivity() {

    private var nomor: String? = null
    private var nama: String? = null
    private var arti: String? = null
    private var type: String? = null
    private var ayat: String? = null
    private var keterangan: String? = null
    private var audio: String? = null
    private var modelSurah: ModelSurah? = null
    private var ayatAdapter: AyatAdapter? = null
    var progressDialog: ProgressDialog? = null
    var modelAyat: MutableList<ModelAyat> = ArrayList()
    private var mHandler: Handler? = null
    private var rvAyat: RecyclerView? = null
    val mediaPlayer = MediaPlayer()

    @SuppressLint("RestrictedApi", "SetTextI18")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_surah)

        var toolbarDetail: androidx.appcompat.widget.Toolbar? = null

        toolbarDetail = androidx.appcompat.widget.Toolbar(this)

        toolbarDetail.title = null
        setSupportActionBar(toolbarDetail)
        if (BuildConfig.DEBUG && supportActionBar == null) {
            error("Assertion failed")
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        toolbarDetail = findViewById(R.id.toolbar_detail)
        toolbarDetail.setNavigationOnClickListener {
            finish()
        }

        mHandler = Handler()

        modelSurah = intent.getSerializableExtra("detailSurah") as ModelSurah
        if (modelSurah != null) {
            nomor = modelSurah!!.nomor
            nama = modelSurah!!.nama
            arti = modelSurah!!.arti
            type = modelSurah!!.type
            ayat = modelSurah!!.ayat
            audio = modelSurah!!.audio
            keterangan = modelSurah!!.keterangan

            val fabPlay = findViewById<FloatingActionButton>(R.id.fabPlay)
            val fabStop = findViewById<FloatingActionButton>(R.id.fabStop)

            fabStop.visibility = View.GONE
            fabPlay.visibility = View.VISIBLE

            val tvHeader = findViewById<TextView>(R.id.tvHeader)
            val tvTitle = findViewById<TextView>(R.id.tvTitle)
            val tvSubtitle = findViewById<TextView>(R.id.tvSubTitle)
            val tvInfo = findViewById<TextView>(R.id.tvInfo)
            val tvKet = findViewById<TextView>(R.id.tvKet)

            tvHeader.text = nama
            tvTitle.text = nama
            tvSubtitle.text = arti
            tvInfo.text = "$type - $ayat Ayat"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) tvKet.text = Html.fromHtml(
                keterangan,
                Html.FROM_HTML_MODE_COMPACT
            )
            else {
                tvKet.text = Html.fromHtml(keterangan)
            }

            fabPlay.setOnClickListener {
                try {
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
                    mediaPlayer.setDataSource(audio)
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                fabPlay.visibility = View.GONE
                fabStop.visibility = View.VISIBLE

            }

                fabStop.setOnClickListener {
                    mediaPlayer.stop()
                    mediaPlayer.reset()
                    fabPlay.visibility = View.VISIBLE
                    fabStop.visibility = View.GONE
                }

            }

            progressDialog = ProgressDialog(this)
            progressDialog!!.setTitle("Mohon Tunggu")
            progressDialog!!.setCancelable(false)
            progressDialog!!.setMessage("Sedang menampilkan data...")


            rvAyat = findViewById<RecyclerView>(R.id.rvAyat)

            rvAyat!!.layoutManager = LinearLayoutManager(this)
            rvAyat!!.setHasFixedSize(true)

            listAyat()
    }

    private fun listAyat() {
        progressDialog!!.show()
        AndroidNetworking.get(Api.URL_LIST_AYAT)
            .addPathParameter("nomor", nomor)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONArray(object : JSONArrayRequestListener {
                override fun onResponse(response: JSONArray?) {
                    for (i in 0 until response!!.length()) {
                        try {
                            progressDialog!!.dismiss()
                            val dataApi = ModelAyat()
                            val jsonObject = response.getJSONObject(i)
                            dataApi.nomor = jsonObject.getString("nomor")
                            dataApi.arab = jsonObject.getString("ar")
                            dataApi.indo = jsonObject.getString("id")
                            dataApi.terjemahan = jsonObject.getString("tr")
                            modelAyat.add(dataApi)
                            showListAyat()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            Toast.makeText(this@DetailSurahActivity, "Gagal menampilkan data", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onError(anError: ANError?) {
                    progressDialog!!.dismiss()
                    Toast.makeText(this@DetailSurahActivity, "Tidak ada jaringan internet", Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun showListAyat() {
        ayatAdapter = AyatAdapter(modelAyat)
        rvAyat!!.adapter = ayatAdapter
    }

    override fun onBackPressed() {
        mediaPlayer.stop()
        mediaPlayer.reset()
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

}