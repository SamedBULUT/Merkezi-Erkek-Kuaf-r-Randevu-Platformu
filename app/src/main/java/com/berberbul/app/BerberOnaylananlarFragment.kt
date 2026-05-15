package com.berberbul.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BerberOnaylananlarFragment : Fragment() {

    private lateinit var rvOnaylananlar: RecyclerView
    private lateinit var adapter: BerberRandevuAdapter
    private val randevuListesi = mutableListOf<Randevu>()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_berber_onaylananlar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvOnaylananlar = view.findViewById(R.id.rvOnaylananlar)
        rvOnaylananlar.layoutManager = LinearLayoutManager(requireContext())

        adapter = BerberRandevuAdapter(
            randevuListesi = randevuListesi,
            onOnaylaClick = {},
            onReddetClick = {}
        )
        rvOnaylananlar.adapter = adapter

        randevuGecmisiniGetir()
    }

    private fun randevuGecmisiniGetir() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("randevular")
            .whereEqualTo("berberId", uid)
            .addSnapshotListener { snapshots, _ ->
                if (snapshots != null) {
                    randevuListesi.clear()
                    for (doc in snapshots) {
                        val randevu = doc.toObject(Randevu::class.java)
                        randevu.id = doc.id

                        if (randevu.status == "Onaylandı" || randevu.status == "İptal Edildi") {
                            randevuListesi.add(randevu)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }
}