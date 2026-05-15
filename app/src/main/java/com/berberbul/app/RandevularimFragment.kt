package com.berberbul.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class RandevularimFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var rvRandevular: RecyclerView
    private lateinit var randevuAdapter: RandevuAdapter
    private var randevuListesi: ArrayList<Randevu> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_randevularim, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        rvRandevular = view.findViewById(R.id.rvRandevular)
        rvRandevular.layoutManager = LinearLayoutManager(requireContext())

        randevuAdapter = RandevuAdapter(randevuListesi) { secilenRandevu ->
            randevuIptalEt(secilenRandevu.id)
        }
        rvRandevular.adapter = randevuAdapter

        randevularıGetir()
    }

    private fun randevularıGetir() {
        val musteriUid = auth.currentUser?.uid
        if (musteriUid != null) {
            db.collection("randevular")
                .whereEqualTo("musteriId", musteriUid)
                .get()
                .addOnSuccessListener { documents ->
                    val geciciListe = ArrayList<Randevu>()
                    for (document in documents) {
                        val randevu = document.toObject(Randevu::class.java)
                        geciciListe.add(randevu)
                    }

                    val dateFormat = SimpleDateFormat("yyyy-M-d HH:mm", Locale.getDefault())

                    val siraliListe = ArrayList(geciciListe.sortedByDescending { randevu ->
                        try {
                            dateFormat.parse("${randevu.date} ${randevu.time}")?.time ?: 0L
                        } catch (e: Exception) {
                            0L
                        }
                    })

                    randevuAdapter.listeyiGuncelle(siraliListe)
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Randevular yüklenemedi", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun randevuIptalEt(randevuId: String) {
        if (randevuId.isEmpty()) return

        db.collection("randevular").document(randevuId)
            .update("status", "İptal Edildi")
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Randevu iptal edildi.", Toast.LENGTH_SHORT).show()
                randevularıGetir()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Hata oluştu!", Toast.LENGTH_SHORT).show()
            }
    }
}