import Model.MassFunction

fun main(args: Array<String>) {
    val gejala1 = MassFunction(listOf("A", "N","D"), 0.6)
    val gejala2 = MassFunction(listOf("N","D", "P"), 0.7)
//    val gejala3 = MassFunction(listOf("A"), 0.9)
    val mKombinasi = kombinasiDuaGejala(mutableListOf(gejala1), gejala2)

    for (m in mKombinasi) {
        println("daftar penyakit: ${m.daftarPenyakit}, nilai: ${m.nilai}")
    }
}

private fun kombinasiDuaGejala(
    massFunctionGejalaSebelumnya: MutableList<MassFunction>,
    massFunctionGejalaBerikutnya: MassFunction
): List<MassFunction> {

    // aturan kombinasi
    val massFunctionHasilAturanKombinasi = himpunanSama(massFunctionGejalaSebelumnya, massFunctionGejalaBerikutnya)



    return massFunctionHasilAturanKombinasi
}

private fun himpunanSama(
    semuaMassFunctionGejalaSebelumnya: List<MassFunction>,
    massFunctionGejalaBerikutnya: MassFunction
): List<MassFunction> {
    var semuaMassFunctionBaru: MutableList<MassFunction> = mutableListOf()
//    val totalNilai = 0.0
    for (massFunction in semuaMassFunctionGejalaSebelumnya) {
        // kombinasi dilakukan pada m{daftar penyakit} berikutnya dan m{0} barikutnya tapi terbatas tidak adanya m{0} sebelumnya

        // kombinasi untuk yang m{daftar penyakit} berikutnya
        var massFunctionSementaraUntukMSebelumnya = MassFunction()
        var irisanDaftarPenyakit: List<String>? =
            massFunction.daftarPenyakit.filter { massFunctionGejalaBerikutnya.daftarPenyakit.contains(it) }
        if (irisanDaftarPenyakit == null) {
            irisanDaftarPenyakit = listOf(Constant.HIMPUNAN_KOSONG)
        }

        massFunctionSementaraUntukMSebelumnya.daftarPenyakit = irisanDaftarPenyakit
        massFunctionSementaraUntukMSebelumnya.nilai = massFunction.nilai * massFunctionGejalaBerikutnya.nilai
        semuaMassFunctionBaru.add(massFunctionSementaraUntukMSebelumnya)

        // kombinasi untuk yang m{0} berikutnya
        var massFunctionSementaraUntukM0Sebelumnya = MassFunction()
        massFunctionSementaraUntukM0Sebelumnya.daftarPenyakit = massFunction.daftarPenyakit
        massFunctionSementaraUntukM0Sebelumnya.nilai = massFunction.nilaiFof * massFunction.nilai
        semuaMassFunctionBaru.add(massFunctionSementaraUntukM0Sebelumnya)

    }

    // untuk kombinasi m{0} dengan m{daftar penyakit} berikutnya dan m{0} berikutnya

    // untuk m{daftar penyakit} berikutnya
    val nilaiM0: Double = 1 - semuaMassFunctionGejalaSebelumnya.map { it.nilai }.sum()
//    val nilaim0 = 1 - totalNilai
    val hasilPerkalianNilaiM0DenganMassFunctionBaru = nilaiM0 * massFunctionGejalaBerikutnya.nilai
    semuaMassFunctionBaru.add(
        MassFunction(
            massFunctionGejalaBerikutnya.daftarPenyakit,
            hasilPerkalianNilaiM0DenganMassFunctionBaru
        )
    )

    // untuk m{0} berikutnya
    semuaMassFunctionBaru.add(MassFunction(listOf("0"), nilaiM0 * massFunctionGejalaBerikutnya.nilaiFof))




    // perhitungan fungsi kombinasi
    semuaMassFunctionBaru = perhitunganFungsiKombinasi(semuaMassFunctionBaru)

    return semuaMassFunctionBaru
}

fun perhitunganFungsiKombinasi(semuaMassFunctionBaru: MutableList<MassFunction>): MutableList<MassFunction> {
    var hasilPerhitunganFungsiKombinasi: MutableList<MassFunction> = mutableListOf()

    // fungsi ini mengkelompokkan yang sama atau sendiri
    val kelompokDaftarMassFunction: MutableList<List<MassFunction>> = mutableListOf()
    var saringan: List<MassFunction> = listOf()
    for (m in semuaMassFunctionBaru) {
        saringan = semuaMassFunctionBaru.filter { it.daftarPenyakit.equals(m.daftarPenyakit) }
        if (saringan.size != 0) {
            kelompokDaftarMassFunction.add(
                saringan
            )
        }
    }

    // cari himpunan kosong
    var totalNilaiHimpunanKosong: Double = 0.0
    val daftarHimpunanKosong: List<MassFunction> =
        semuaMassFunctionBaru.filter { it.daftarPenyakit.contains(Constant.HIMPUNAN_KOSONG) }
    if (daftarHimpunanKosong.size >= 0) {
        for (m in daftarHimpunanKosong) {
            totalNilaiHimpunanKosong = totalNilaiHimpunanKosong + m.nilai
        }
    }

    // for in kelompok daftarMass function, masukkan kedalam rumus
    for (daftarMassFunction in kelompokDaftarMassFunction) {
        if (daftarMassFunction.size == 1) {
            hasilPerhitunganFungsiKombinasi.add(daftarMassFunction[0])
        } else {

            hasilPerhitunganFungsiKombinasi.add(rumusAturanKombinasi(daftarMassFunction, totalNilaiHimpunanKosong))
        }
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
