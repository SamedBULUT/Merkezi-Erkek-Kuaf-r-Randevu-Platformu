package com.berberbul.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class RandevularimFragment : Fragment() {

    private lateinit var rvRandevularim: RecyclerView
    private lateinit var randevuAdapter: RandevuAdapter
    private val randevuListesi = mutableListOf<Randevu>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_randevularim, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvRandevularim = view.findViewById(R.id.rvRandevularim)
        rvRandevularim.layoutManager = LinearLayoutManager(requireContext())

        randevuAdapter = RandevuAdapter(randevuListesi)
        rvRandevularim.adapter = randevuAdapter

        randevulariGetir()
    }

    private fun randevulariGetir() {
        db.collection("randevular")
            .whereEqualTo("customerName", "Müşteri Can")
            .get()
            .addOnSuccessListener { documents ->
                randevuListesi.clear()
                for (document in documents) {
                    val randevu = document.toObject(Randevu::class.java)
                    randevuListesi.add(randevu)
                }
                randevuAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("BackendTest", "Randevular çekilemedi", exception)
                Toast.makeText(requireContext(), "Randevular yüklenemedi.", Toast.LENGTH_SHORT).show()
            }
    }
}