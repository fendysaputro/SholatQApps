package id.phephen.sholatqapps.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.widget.TextView
import id.phephen.sholatqapps.R
import id.phephen.sholatqapps.adapter.SurahAdapter
import id.phephen.sholatqapps.fragment.FragmentJadwalSholat.Companion.newInstance
import id.phephen.sholatqapps.model.ModelSurah
import java.util.*
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.google.android.gms.location.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import id.phephen.sholatqapps.networking.Api
import id.phephen.sholatqapps.utils.GetAddressIntentService
import org.json.JSONArray
import org.json.JSONException
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class ListSurahActivity : AppCompatActivity(), SurahAdapter.OnSelectedData {

    private var surahAdapter: SurahAdapter? = null
    var progressDialog: ProgressDialog? = null
    val modelSurah: MutableList<ModelSurah> = ArrayList()
    private var hariIni: String? = null
    private var tanggal: String? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var addressResultReceiver: LocationAddressResultReceiver? = null
    private var currentLocation: Location? = null
    private var locationCallback: LocationCallback? = null
    var rvSurah: RecyclerView? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_surah)

        supportActionBar?.hide()

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Mohon Tunggu")
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage("Sedang menampilkan data...")

        addressResultReceiver = LocationAddressResultReceiver(Handler())

        val tvToday = findViewById<TextView>(R.id.tvToday)
        val tvDate = findViewById<TextView>(R.id.tvDate)

        val dateNow = Calendar.getInstance().time
        hariIni = DateFormat.format("EEEE", dateNow) as String
        tanggal = DateFormat.format("d MMMM yyyy", dateNow) as String

        tvToday.text = "$hariIni,"
        tvDate.text = tanggal

        val sendDetail = newInstance("detail")
        val lLTime = findViewById<LinearLayout>(R.id.llTime)
        val llMosque = findViewById<LinearLayout>(R.id.llMosque)
        val llIqro = findViewById<LinearLayout>(R.id.llIqro)
        val fabDoa = findViewById<FloatingActionButton>(R.id.fab_doa)
        lLTime.setOnClickListener {
            sendDetail.show(supportFragmentManager, sendDetail.tag)
        }

        llMosque.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@ListSurahActivity, MasjidActivity::class.java)) })

        llIqro.setOnClickListener {
            startActivity(Intent(this, IqroActivity::class.java))
        }

        fabDoa.setOnClickListener {
            startActivity(Intent(this@ListSurahActivity, DoaHarianActivity::class.java))
        }

        rvSurah = findViewById<RecyclerView>(R.id.rvSurah)
        rvSurah?.layoutManager = LinearLayoutManager(this)
        rvSurah?.setHasFixedSize(true)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                currentLocation = locationResult.locations[0]
                address
            }
        }
        startLocationUpdates()
        listSurah()

    }

    private fun listSurah() {
        progressDialog!!.show()
        AndroidNetworking.get(Api.URL_LIST_SURAH)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONArray(object : JSONArrayRequestListener {
                override fun onResponse(response: JSONArray?) {
                    for (i in 0 until response!!.length()) {
                        try {
                            progressDialog!!.dismiss()
                            val dataApi = ModelSurah()
                            val jsonObject = response.getJSONObject(i)
                            dataApi.nomor = jsonObject.getString("nomor")
                            dataApi.nama = jsonObject.getString("nama")
                            dataApi.type = jsonObject.getString("type")
                            dataApi.ayat = jsonObject.getString("ayat")
                            dataApi.asma = jsonObject.getString("asma")
                            dataApi.arti = jsonObject.getString("arti")
                            dataApi.audio = jsonObject.getString("audio")
                            dataApi.keterangan = jsonObject.getString("keterangan")
                            modelSurah.add(dataApi)
                            showListSurah()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            Toast.makeText(this@ListSurahActivity, "Gagal menampilkan data!", Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onError(anError: ANError?) {
                    progressDialog!!.dismiss()
                    Toast.makeText(
                        this@ListSurahActivity, "Tidak ada jaringan internet!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
    }

    private fun showListSurah () {
        surahAdapter = SurahAdapter(modelSurah, this)
        rvSurah!!.adapter = surahAdapter
    }

    override fun onSelected(modelSurah: ModelSurah?) {
        val intent = Intent(this, DetailSurahActivity::class.java)
        intent.putExtra("detailSurah", modelSurah)
        startActivity(intent)
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            val locationRequest = LocationRequest()
            locationRequest.interval = 1000
            locationRequest.fastestInterval = 1000
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            fusedLocationClient!!.requestLocationUpdates(locationRequest, locationCallback!!, null)
        }
    }

    private val address: Unit
    get() {
        if (!Geocoder.isPresent()) {
            Toast.makeText(this, "Can't find current address, ", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(this, GetAddressIntentService::class.java)
        intent.putExtra("add_receiver", addressResultReceiver)
        intent.putExtra("add_location", currentLocation)
        startService(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            } else {
                Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private inner class LocationAddressResultReceiver internal constructor(handler: Handler?) :
            ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
            super.onReceiveResult(resultCode, resultData)
            if (resultCode == 0) {
                address
            }

            if (resultCode == 1) {
                Toast.makeText(this@ListSurahActivity, "Address not found", Toast.LENGTH_SHORT).show()
            }
            val currentAdd = resultData!!.getString("address_result")
            showResult(currentAdd)
        }

            }

    private fun showResult(currentAdd: String?) {
        val newAddress = findViewById<TextView>(R.id.txtLocation)
        newAddress!!.text = currentAdd
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient!!.removeLocationUpdates(locationCallback!!)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 2
    }
}