package com.berberbul.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Müşteri Profil Sayfası.
 * Bu sayfada sadece müşterinin kişisel bilgileri yer alır.
 * Harita modülü buradan kaldırılmış ve Berber tarafına taşınmıştır.
 */
class ProfilFragment : Fragment() {

    // Bundle ile veri taşıyacaksanız bu parametreleri tutabilirsiniz
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString("param1")
            param2 = it.getString("param2")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // DİKKAT: R.layout.fragment_profil dosyasını şişirdiğinizden emin olun.
        // Daha önce burada fragment_berber_yonetim yazıyordu, onu düzelttik.
        return inflater.inflate(R.layout.fragment_profil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Burada profil bilgilerini (ad, soyad, telefon vb.)
        // veritabanından çekip UI elemanlarına atayabilirsin.
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfilFragment().apply {
                arguments = Bundle().apply {
                    putString("param1", param1)
                    putString("param2", param2)
                }
            }
    }
}