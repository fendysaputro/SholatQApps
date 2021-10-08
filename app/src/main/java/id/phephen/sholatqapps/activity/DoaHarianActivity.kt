package id.phephen.sholatqapps.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import id.phephen.sholatqapps.R
import id.phephen.sholatqapps.adapter.DoaHarianAdapter
import id.phephen.sholatqapps.model.ModelDoaHarian
import id.phephen.sholatqapps.networking.Api
import org.json.JSONException
import org.json.JSONObject


@SuppressLint("RestrictedApi", "SetTextI18")
@Suppress("DEPRECATION")
class DoaHarianActivity : AppCompatActivity(), DoaHarianAdapter.OnSelectedData {

    private var doaHarianAdapter: DoaHarianAdapter? = null
    var progressDialog: ProgressDialog? = null
    val modelDoa: MutableList<ModelDoaHarian> = ArrayList()
    var rvDoaHarian: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doa_harian)

        var toolbarDetailDoa: Toolbar? = null

        toolbarDetailDoa = findViewById(R.id.toolbar_detail_doa)

        toolbarDetailDoa.title = "Doa Harian"

        setSupportActionBar(toolbarDetailDoa)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        toolbarDetailDoa.setNavigationOnClickListener {
            finish()
        }

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Mohon Tunggu")
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage("Sedang menampilkan data...")


        rvDoaHarian = findViewById<RecyclerView>(R.id.rv_doa)
        rvDoaHarian?.layoutManager = LinearLayoutManager(this)
        rvDoaHarian?.setHasFixedSize(true)

        listDoaHarian()

    }

    private fun listDoaHarian() {
        progressDialog!!.show()
        AndroidNetworking.get(Api.URL_LIST_DOA_HARIAN)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener{
                override fun onResponse(response: JSONObject?) {
                    try {
                        if (response != null) {
                            progressDialog!!.dismiss()
                            val resArray = response.getJSONArray("data")
                            for (i in 0 until resArray.length()) {
                                val data = ModelDoaHarian()
                                val resObj = resArray.getJSONObject(i)
                                data.title = resObj.getString("title")
                                data.arabic = resObj.getString("arabic")
                                data.latin = resObj.getString("latin")
                                data.translation = resObj.getString("translation")
                                modelDoa.add(data)
                                showListDoa()
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(this@DoaHarianActivity, "Gagal Menampilkan data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(anError: ANError?) {
                    progressDialog!!.dismiss()
                    Toast.makeText(this@DoaHarianActivity, "Tidak ada jaringan internet", Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun showListDoa() {
        doaHarianAdapter = DoaHarianAdapter(modelDoa, this)
        rvDoaHarian!!.adapter = doaHarianAdapter
    }

    override fun onSelected(data: ModelDoaHarian) {
        val intent = Intent(this, DetailDoaHarianActivity::class.java)
        intent.putExtra("detailDoa", data)
        startActivity(intent)
    }


}