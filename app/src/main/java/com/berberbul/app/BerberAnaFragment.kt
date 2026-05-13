package com.berberbul.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class BerberAnaFragment : Fragment() {

    private lateinit var rvBerberRandevular: RecyclerView
    private lateinit var tvBekleyenSayisi: TextView
    private lateinit var tvOnaylananSayisi: TextView
    private lateinit var adapter: BerberRandevuAdapter

    private val randevuListesi = mutableListOf<Randevu>()
    private val db = FirebaseFirestore.getInstance()

    // Sisteme giriş yapan aktif berberin adı (Giriş sistemine göre dinamikleştirilebilir)
    private val aktifBerberAdi = "Kuaför Selim"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Hazırladığımız XML tasarımını bağlıyoruz
        return inflater.inflate(R.layout.fragment_berber_ana, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvBerberRandevular = view.findViewById(R.id.rvBerberRandevular)
        tvBekleyenSayisi = view.findViewById(R.id.tvBekleyenSayisi)
        tvOnaylananSayisi = view.findViewById(R.id.tvOnaylananSayisi)

        rvBerberRandevular.layoutManager = LinearLayoutManager(requireContext())

        // Adapter'ı başlatırken buton tıklamalarını dinliyoruz
        adapter = BerberRandevuAdapter(
            randevuListesi = randevuListesi,
            onOnaylaClick = { secilenRandevu ->
                randevuDurumunuGuncelle(secilenRandevu.id, "Onaylandı")
            },
            onReddetClick = { secilenRandevu ->
                randevuDurumunuGuncelle(secilenRandevu.id, "Reddedildi")
            }
        )
        rvBerberRandevular.adapter = adapter

        randevulariGetir()
    }

    private fun randevulariGetir() {
        // Berberin adına ait ve henüz onaylanmamış/bekleyen randevuları getiriyoruz
        db.collection("randevular")
            .whereEqualTo("barberName", aktifBerberAdi)
            .whereEqualTo("status", "Bekliyor")
            .get()
            .addOnSuccessListener { documents ->
                randevuListesi.clear()
                for (document in documents) {
                    val randevu = document.toObject(Randevu::class.java)
                    // Güncelleme yapabilmek için belgenin Firebase ID'sini alıyoruz
                    randevu.id = document.id
                    randevuListesi.add(randevu)
                }
                tvBekleyenSayisi.text = randevuListesi.size.toString()
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("BerberPanel", "Randevular yüklenemedi", e)
            }
    }

    private fun randevuDurumunuGuncelle(documentId: String, yeniDurum: String) {
        if (documentId.isEmpty()) return

        db.collection("randevular").document(documentId)
            .update("status", yeniDurum)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Randevu $yeniDurum", Toast.LENGTH_SHORT).show()
                // Durum güncellenince listeyi anında yeniliyoruz
                randevulariGetir()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Hata oluştu!", Toast.LENGTH_SHORT).show()
                Log.e("BerberPanel", "Güncelleme hatası", e)
            }
    }
}