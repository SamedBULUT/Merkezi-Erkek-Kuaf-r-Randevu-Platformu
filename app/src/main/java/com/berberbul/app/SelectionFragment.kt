package com.berberbul.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class SelectionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // fragment_selection.xml tasarımını ekrana bağlar
        return inflater.inflate(R.layout.fragment_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // XML tasarımındaki kartları buluyoruz
        val cardBerber = view.findViewById<View>(R.id.cardBerber)
        val cardMusteri = view.findViewById<View>(R.id.cardMusteri)

        // Berbere tıklandığında
        cardBerber.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("KULLANICI_ROLU", "berber")
            // Not: R.id.action_selectionFragment_to_loginFragment kısmı nav_graph'ta çizdiğin okun adıdır.
            findNavController().navigate(R.id.action_selectionFragment_to_loginFragment, bundle)
        }

        // Müşteriye tıklandığında
        cardMusteri.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("KULLANICI_ROLU", "musteri")
            // Not: R.id.action_selectionFragment_to_loginFragment kısmı nav_graph'ta çizdiğin okun adıdır.
            findNavController().navigate(R.id.action_selectionFragment_to_loginFragment, bundle)
        }
    }
}