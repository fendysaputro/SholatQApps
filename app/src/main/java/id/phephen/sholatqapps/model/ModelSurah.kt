package id.phephen.sholatqapps.model

import java.io.Serializable

/**
 * Created by Phephen on 30/08/2021.
 */
class ModelSurah : Serializable {

    var arti: String? = null

    @JvmField
    var asma: String? = null

    @JvmField
    var ayat: String? = null

    @JvmField
    var nama: String? = null

    @JvmField
    var type: String? = null
    var audio: String? = null

    @JvmField
    var nomor: String? = null
    var keterangan: String? = null

}