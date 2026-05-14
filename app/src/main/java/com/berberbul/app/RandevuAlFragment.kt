package com.berberbul.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.berberbul.app.data.Berber
import com.berberbul.app.data.BerberAdapter
import com.google.firebase.firestore.FirebaseFirestore

class RandevuAlFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var rvBerberler: RecyclerView
    private lateinit var berberAdapter: BerberAdapter
    private var berberListesi: ArrayList<Berber> = ArrayList()

    private val anlikKullaniciEnlem = 41.0027
    private val anlikKullaniciBoylam = 39.7168

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_randevu_al, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        rvBerberler = view.findViewById(R.id.rvBerberList)
        rvBerberler.layoutManager = LinearLayoutManager(requireContext())

        berberAdapter = BerberAdapter(berberListesi) { secilenBerber ->
            val bundle = Bundle()
            bundle.putString("SECILEN_BERBER_UID", secilenBerber.berberUid)
            bundle.putString("SECILEN_BERBER_AD", secilenBerber.dukkanAdi)
            findNavController().navigate(R.id.action_RandevuAlFragment_to_BerberProfilFragment, bundle)
        }

        rvBerberler.adapter = berberAdapter

        berberleriGetir()
    }

    private fun berberleriGetir() {
        db.collection("berberler").get()
            .addOnSuccessListener { documents ->
                val geciciListe = ArrayList<Berber>()
                for (document in documents) {
                    val berber = document.toObject(Berber::class.java)

                    if (berber.dukkanAdi.isNotEmpty()) {
                        berber.musteriyeUzaklik = mesafeHesapla(
                            anlikKullaniciEnlem,
                            anlikKullaniciBoylam,
                            berber.enlem,
                            berber.boylam
                        )
                        geciciListe.add(berber)
                    }
                }

                val siraliListe = ArrayList(geciciListe.sortedBy { it.musteriyeUzaklik })
                berberAdapter.listeyiGuncelle(siraliListe)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Berberler yüklenemedi", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mesafeHesapla(kullaniciEnlem: Double, kullaniciBoylam: Double, berberEnlem: Double, berberBoylam: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(berberEnlem - kullaniciEnlem)
        val dLon = Math.toRadians(berberBoylam - kullaniciBoylam)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(kullaniciEnlem)) * Math.cos(Math.toRadians(berberEnlem)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }
}