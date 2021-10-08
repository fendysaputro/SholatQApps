package id.phephen.sholatqapps.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.OkHttpResponseListener
import com.github.barteksc.pdfviewer.PDFView
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import id.phephen.sholatqapps.BuildConfig
import id.phephen.sholatqapps.R
import id.phephen.sholatqapps.adapter.HaditsAdapter
import id.phephen.sholatqapps.model.Hadits
import id.phephen.sholatqapps.networking.Api
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.File


@SuppressLint("RestrictedApi", "SetTextI18")
@Suppress("DEPRECATION")
class IqroActivity : AppCompatActivity(), HaditsAdapter.OnItemSelectedData {

    var progressDialog: ProgressDialog? = null
    var spinnerValue: String? = null
    var btnShowData: Button? = null
    var rvHadits: RecyclerView? = null
    var modelHadits: MutableList<Hadits> = ArrayList()
    private var haditsAdapter: HaditsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iqro)

        var toolbarHaditsTitle: Toolbar? = null
        toolbarHaditsTitle = Toolbar(this)
        toolbarHaditsTitle.title = null
        setSupportActionBar(toolbarHaditsTitle)
        if (BuildConfig.DEBUG && supportActionBar == null) {
            error("Assertion failed")
        }

        toolbarHaditsTitle = findViewById(R.id.toolbar_iqro)
        toolbarHaditsTitle.setNavigationOnClickListener {
            finish()
        }

        setupView()
    }

    private fun setupView () {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Mohon Tunggu")
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage("Sedang menampilkan data...")


        val spinner: Spinner = findViewById(R.id.iqro_spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.riwayat_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    spinnerValue = adapter.getItem(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }
        }

        btnShowData = findViewById(R.id.btn_show_data)
        btnShowData!!.setOnClickListener {
            getData()
        }

        rvHadits = findViewById(R.id.rv_list_hadits)
        rvHadits?.layoutManager = LinearLayoutManager(this)
        rvHadits?.setHasFixedSize(true)
    }

    private fun getData () {
        progressDialog!!.show()
        AndroidNetworking.get(Api.URL_LIST_HADIST_RIWAYAT + spinnerValue + "?range=1-300")
            .setTag(this)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    try {
                        if (response != null) {
                            progressDialog!!.dismiss()
                            val responseObject = response.getJSONObject("data")
                            val resArray = responseObject.getJSONArray("hadiths")
                            for (i in 0 until resArray.length()) {
                                val data = Hadits()
                                val resObj = resArray.getJSONObject(i)
                                data.arab = resObj.getString("arab")
                                data.number = resObj.getInt("number")
                                data.id = resObj.getString("id")
                                modelHadits.add(data)
                                showListHadits()
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(this@IqroActivity, "Gagal menampilkan data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(anError: ANError?) {
                    progressDialog!!.dismiss()
                    Toast.makeText(this@IqroActivity, "Tidak ada jaringan internet", Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun showListHadits() {
        haditsAdapter = HaditsAdapter(modelHadits, this)
        rvHadits!!.adapter = haditsAdapter
    }

    override fun onItemSelected(data: Hadits) {
        val intent = Intent(this, DetailHaditsActivity::class.java)
        intent.putExtra("detailHadits", data)
        intent.putExtra("perawi", spinnerValue)
        startActivity(intent)
    }
}