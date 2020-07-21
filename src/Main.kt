import Model.MassFunction
import Model.PersentaseKemungkinan
import java.lang.Exception

fun main(args: Array<String>) {
    val gejala1 = MassFunction(listOf("A", "N", "D"), 0.6)
    val gejala2 = MassFunction(listOf("N", "D", "P"), 0.7)
    val gejala3 = MassFunction(listOf("A"), 0.9)
    val mKombinasi = hitungMassFunctionKombinasi(listOf(gejala1, gejala2, gejala3))

    for (m in mKombinasi) {
        println("daftar penyakit: ${m.daftarPenyakit}, nilai: ${m.nilai}")
    }

    try {
        val persentaseKemungkinan = hitungPersentase(mKombinasi)
        print("jadi persentase penyakit ${persentaseKemungkinan.penyakit} adalah sebesar ${persentaseKemungkinan.persentase}%")
    } catch (e: Exception) {
        print("tidak dapat mencari")
    }
}

private fun hitungMassFunctionKombinasi(
    semuaGejala: List<MassFunction>
): List<MassFunction> {
    var massFunctionGejalaSebelumnya: MutableList<MassFunction> = mutableListOf(semuaGejala[0])
    for ((index, gejalaSelanjutnya) in semuaGejala.withIndex()) {
        if (index != 0) {
//            massFunctionGejalaSebelumnya.add()
            // aturan kombinasi
            massFunctionGejalaSebelumnya =
                himpunanSama(massFunctionGejalaSebelumnya, gejalaSelanjutnya)
        }
    }

    return massFunctionGejalaSebelumnya
}

private fun hitungPersentase(mKombinasi: List<MassFunction>): PersentaseKemungkinan {
    // filter MassFunction yang daftar penyakit 1 list saja
    val mYangDaftarPenyakitSatuList = mKombinasi.filter { it.daftarPenyakit.size == 1 }

    // cek berisi atau tidak
    if (mYangDaftarPenyakitSatuList.size == 0) {
        throw Exception("Tidak di temukan")
    } else {
        // cari nilai paling tinggi yang bukan θ
        val mKombinasiTampaθ = mYangDaftarPenyakitSatuList.filter { it.daftarPenyakit.containsAll(listOf(Constant.TETHA)) }
        val dapat = mYangDaftarPenyakitSatuList.maxBy { it.nilai }
        return PersentaseKemungkinan(dapat!!.daftarPenyakit[0],dapat.nilai)
    }
}

private fun himpunanSama(
    semuaMassFunctionGejalaSebelumnya: List<MassFunction>,
    massFunctionGejalaBerikutnya: MassFunction
): MutableList<MassFunction> {
    var semuaMassFunctionBaru: MutableList<MassFunction> = mutableListOf()
//    val totalNilai = 0.0
    for (massFunction in semuaMassFunctionGejalaSebelumnya) {
        // kombinasi dilakukan pada m{daftar penyakit} berikutnya dan m{0} barikutnya tapi terbatas tidak adanya m{0} sebelumnya

        // kombinasi untuk yang m{daftar penyakit} berikutnya
        var massFunctionSementaraUntukMSebelumnya = MassFunction()
        var irisanDaftarPenyakit: List<String>? =
            massFunction.daftarPenyakit.filter { massFunctionGejalaBerikutnya.daftarPenyakit.contains(it) }
        if (irisanDaftarPenyakit!!.isEmpty() == true && !massFunction.daftarPenyakit.containsAll(listOf(Constant.TETHA))) {
            irisanDaftarPenyakit = listOf(Constant.HIMPUNAN_KOSONG)
        }
        if (massFunction.daftarPenyakit.containsAll(listOf(Constant.TETHA))) {
            irisanDaftarPenyakit = massFunctionGejalaBerikutnya.daftarPenyakit
        }

        massFunctionSementaraUntukMSebelumnya.daftarPenyakit = irisanDaftarPenyakit
        massFunctionSementaraUntukMSebelumnya.nilai = massFunction.nilai * massFunctionGejalaBerikutnya.nilai
        semuaMassFunctionBaru.add(massFunctionSementaraUntukMSebelumnya)

        // kombinasi untuk yang m{0} berikutnya
        var massFunctionSementaraUntukMθberikutnya =
            MassFunction(massFunction.daftarPenyakit, massFunction.nilai * massFunctionGejalaBerikutnya.nilaiFof)
//        massFunctionSementaraUntukM0Sebelumnya.daftarPenyakit = massFunction.daftarPenyakit
//        massFunctionSementaraUntukM0Sebelumnya.nilai = massFunction.nilaiFof * massFunctionGejalaBerikutnya.nilai
        semuaMassFunctionBaru.add(massFunctionSementaraUntukMθberikutnya)

    }

    // untuk kombinasi m{0} dengan m{daftar penyakit} berikutnya dan m{0} berikutnya


    val tetha = semuaMassFunctionBaru.filter { it.daftarPenyakit.contains(Constant.TETHA) } as MutableList<MassFunction>
    var nilaiMØ: Double = 0.0
    if (tetha.size == 0) {
        // untuk m{daftar penyakit} berikutnya
        nilaiMØ = semuaMassFunctionGejalaSebelumnya[0].nilaiFof

        val hasilPerkalianNilaiM0DenganMassFunctionBaru = nilaiMØ * massFunctionGejalaBerikutnya.nilai
        semuaMassFunctionBaru.add(
            MassFunction(
                massFunctionGejalaBerikutnya.daftarPenyakit,
                hasilPerkalianNilaiM0DenganMassFunctionBaru
            )
        )

        // untuk m{0} berikutnya
        semuaMassFunctionBaru.add(MassFunction(listOf(Constant.TETHA), nilaiMØ * massFunctionGejalaBerikutnya.nilaiFof))
    }


    // perhitungan fungsi kombinasi
    semuaMassFunctionBaru = perhitunganFungsiKombinasi(semuaMassFunctionBaru)
    // hapus himpunan kosong
    semuaMassFunctionBaru =
        semuaMassFunctionBaru.filter { !it.daftarPenyakit.contains(Constant.HIMPUNAN_KOSONG) } as MutableList<MassFunction>


    return semuaMassFunctionBaru
}

fun perhitunganFungsiKombinasi(semuaMassFunctionBaru: MutableList<MassFunction>): MutableList<MassFunction> {
    var hasilPerhitunganFungsiKombinasi: MutableList<MassFunction> = mutableListOf()
    var daftarYangSudah: MutableList<List<String>> = mutableListOf()

    // fungsi ini mengkelompokkan yang sama atau sendiri
    val kelompokDaftarMassFunction: MutableList<List<MassFunction>> = mutableListOf()
    var saringan: List<MassFunction> = listOf()
    for (m in semuaMassFunctionBaru) {
        saringan = semuaMassFunctionBaru.filter { it.daftarPenyakit.equals(m.daftarPenyakit) }
        val cekSudahAda = daftarYangSudah.filter { it.containsAll(m.daftarPenyakit) }

        if (saringan.size != 0 && cekSudahAda.size == 0) {
            daftarYangSudah.add(m.daftarPenyakit)
            kelompokDaftarMassFunction.add(
                saringan
            )
        }
    }

    // cari himpunan kosong
    var totalNilaiHimpunanKosong: Double = 0.0
    val daftarHimpunanKosong: List<MassFunction> =
        semuaMassFunctionBaru.filter { it.daftarPenyakit.contains(Constant.HIMPUNAN_KOSONG) }
    if (daftarHimpunanKosong.size > 0) {
        for (m in daftarHimpunanKosong) {
            totalNilaiHimpunanKosong = totalNilaiHimpunanKosong + m.nilai
        }
    }

    // for in kelompok daftarMass function, masukkan kedalam rumus
    for (daftarMassFunction in kelompokDaftarMassFunction) {
        hasilPerhitunganFungsiKombinasi.add(rumusAturanKombinasi(daftarMassFunction, totalNilaiHimpunanKosong))
    }

    return hasilPerhitunganFungsiKombinasi
}

fun rumusAturanKombinasi(daftarMassFunction: List<MassFunction>, k: Double = 0.0): MassFunction {

    var jumlahNilai = 0.0
    for (m in daftarMassFunction) {
        jumlahNilai = jumlahNilai + m.nilai
    }
    var satuKurangK = 1 - k
    return MassFunction(daftarMassFunction[0].daftarPenyakit, (jumlahNilai / satuKurangK))
}
