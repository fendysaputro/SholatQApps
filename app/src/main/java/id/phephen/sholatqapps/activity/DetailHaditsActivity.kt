package id.phephen.sholatqapps.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import id.phephen.sholatqapps.BuildConfig
import id.phephen.sholatqapps.R
import id.phephen.sholatqapps.model.Hadits

class DetailHaditsActivity : AppCompatActivity() {

    private var titleHadits: String? = null
    private var latinHadits: String? = null
    private var arabicHadits: String? = null
    private var modelHadits: Hadits? = null
    private var perawi: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_hadits)

        var toolbarDetailHaditsTitle: Toolbar? = null
        toolbarDetailHaditsTitle = Toolbar(this)

        toolbarDetailHaditsTitle.title = null
        setSupportActionBar(toolbarDetailHaditsTitle)
        if (BuildConfig.DEBUG && supportActionBar == null) {
            error("Assertion failed")
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        toolbarDetailHaditsTitle = findViewById(R.id.toolbar_detail_hadits)
        toolbarDetailHaditsTitle.setOnClickListener {
            finish()
        }

        var newPerawi: String? = null
        perawi = intent.getStringExtra("perawi") as String
        if (!perawi.isNullOrEmpty()) {
            newPerawi = perawi
        }

        modelHadits = intent.getSerializableExtra("detailHadits") as Hadits
        if (modelHadits != null) {
            titleHadits = modelHadits!!.number.toString()
            latinHadits = modelHadits!!.id
            arabicHadits = modelHadits!!.arab
        }

        val tvHeaderTitleHadits = findViewById<TextView>(R.id.tvHeader_title_hadits)
        val tvLatin = findViewById<TextView>(R.id.txt_arti)
        val tvArabic = findViewById<TextView>(R.id.txt_arabic)
        val tvTitleHadits = findViewById<TextView>(R.id.txt_title_hadits)

        ("Hadits No : $titleHadits HR. $newPerawi").also { tvHeaderTitleHadits.text = it }
        tvArabic.text = arabicHadits
        tvLatin.text = latinHadits
        tvTitleHadits.text = "Hadits No : $titleHadits"
    }
}