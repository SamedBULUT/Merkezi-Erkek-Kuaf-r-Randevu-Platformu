package com.berberbul.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BerberAnaFragment : Fragment() {

    private lateinit var rvBerberRandevular: RecyclerView
    private lateinit var tvBekleyenSayisi: TextView
    private lateinit var tvOnaylananSayisi: TextView
    private lateinit var cardOnaylananlar: View
    private lateinit var adapter: BerberRandevuAdapter

    private val randevuListesi = mutableListOf<Randevu>()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_berber_ana, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvBerberRandevular = view.findViewById(R.id.rvBerberRandevular)
        tvBekleyenSayisi = view.findViewById(R.id.tvBekleyenSayisi)
        tvOnaylananSayisi = view.findViewById(R.id.tvOnaylananSayisi)
        cardOnaylananlar = view.findViewById(R.id.cardOnaylananlar)

        rvBerberRandevular.layoutManager = LinearLayoutManager(requireContext())

        adapter = BerberRandevuAdapter(
            randevuListesi = randevuListesi,
            onOnaylaClick = { secilenRandevu ->
                randevuDurumunuGuncelle(secilenRandevu.id, "Onaylandı")
            },
            onReddetClick = { secilenRandevu ->
                randevuDurumunuGuncelle(secilenRandevu.id, "Onaylanmadı")
            }
        )
        rvBerberRandevular.adapter = adapter

        cardOnaylananlar.setOnClickListener {
            findNavController().navigate(R.id.action_BerberAnaFragment_to_BerberOnaylananlarFragment)
        }

        randevulariAnlikTakipEt()
    }

    private fun randevulariAnlikTakipEt() {
        val aktifBerberUid = auth.currentUser?.uid ?: return

        db.collection("randevular")
            .whereEqualTo("berberId", aktifBerberUid)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("BerberPanel", "Dinleme hatası", e)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    randevuListesi.clear()
                    var bekleyenCount = 0
                    var onaylananCount = 0

                    for (document in snapshots) {
                        val randevu = document.toObject(Randevu::class.java)
                        randevu.id = document.id

                        if (randevu.status == "Bekliyor") {
                            randevuListesi.add(randevu)
                            bekleyenCount++
                        } else if (randevu.status == "Onaylandı") {
                            onaylananCount++
                        }
                    }

                    tvBekleyenSayisi.text = bekleyenCount.toString()
                    tvOnaylananSayisi.text = onaylananCount.toString()
                    adapter.notifyDataSetChanged()
                }
            }
    }

    private fun randevuDurumunuGuncelle(documentId: String, yeniDurum: String) {
        if (documentId.isEmpty()) return

        db.collection("randevular").document(documentId)
            .update("status", yeniDurum)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "İşlem başarılı: $yeniDurum", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("BerberPanel", "Güncelleme hatası", e)
            }
    }
}