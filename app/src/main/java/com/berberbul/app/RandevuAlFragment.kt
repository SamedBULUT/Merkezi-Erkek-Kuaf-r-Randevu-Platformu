package com.berberbul.app

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.berberbul.app.data.Berber
import com.berberbul.app.data.BerberAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore

class RandevuAlFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var rvBerberler: RecyclerView
    private lateinit var berberAdapter: BerberAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var berberListesi: ArrayList<Berber> = ArrayList()

    private var anlikLat: Double = 0.0
    private var anlikLon: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_randevu_al, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        rvBerberler = view.findViewById(R.id.rvBerberList)
        rvBerberler.layoutManager = LinearLayoutManager(requireContext())

        berberAdapter = BerberAdapter(berberListesi) { secilenBerber ->
            val bundle = Bundle()
            bundle.putString("SECILEN_BERBER_UID", secilenBerber.berberUid)
            bundle.putString("SECILEN_BERBER_AD", secilenBerber.dukkanAdi)
            findNavController().navigate(R.id.action_RandevuAlFragment_to_BerberProfilFragment, bundle)
        }

        rvBerberler.adapter = berberAdapter

        konumIzniniKontrolEt()
    }

    private fun konumIzniniKontrolEt() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
        } else {
            anlikKonumuGetir()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            anlikKonumuGetir()
        } else {
            Toast.makeText(requireContext(), "Konum izni verilmedi, sıralama yapılamıyor.", Toast.LENGTH_SHORT).show()
            berberleriGetir() // İzin yoksa mesafesiz getir
        }
    }

    private fun anlikKonumuGetir() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    anlikLat = location.latitude
                    anlikLon = location.longitude
                }
                berberleriGetir()
            }
        } catch (e: SecurityException) {
            berberleriGetir()
        }
    }

    private fun berberleriGetir() {
        db.collection("berberler").get()
            .addOnSuccessListener { documents ->
                val geciciListe = ArrayList<Berber>()
                for (document in documents) {
                    val berber = document.toObject(Berber::class.java)

                    if (berber.dukkanAdi.isNotEmpty()) {
                        // Eğer konum alınabilmişse mesafeyi hesapla
                        if (anlikLat != 0.0 && anlikLon != 0.0) {
                            berber.musteriyeUzaklik = mesafeHesapla(
                                anlikLat, anlikLon,
                                berber.enlem, berber.boylam
                            )
                        }
                        geciciListe.add(berber)
                    }
                }

                val siraliListe = ArrayList(geciciListe.sortedBy { it.musteriyeUzaklik })
                berberAdapter.listeyiGuncelle(siraliListe)
            }
    }

    private fun mesafeHesapla(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }
}