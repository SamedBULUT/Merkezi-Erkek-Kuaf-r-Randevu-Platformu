package com.berberbul.app

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

/**
 * Müşteri Profil Sayfası.
 * Bu sayfada sadece müşterinin kişisel bilgileri yer alır.
 * Harita modülü buradan kaldırılmış ve Berber tarafına taşınmıştır.
 */
class ProfilFragment : Fragment() {

    // Bundle ile veri taşıyacaksanız bu parametreleri tutabilirsiniz
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var mapView: MapView

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
        berber-paneli-arayuz
        // DİKKAT: R.layout.fragment_profil dosyasını şişirdiğinizden emin olun.
        // Daha önce burada fragment_berber_yonetim yazıyordu, onu düzelttik.
        return inflater.inflate(R.layout.fragment_profil, container, false)

        val context = requireContext()
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))

        val view = inflater.inflate(R.layout.fragment_berber_yonetim, container, false)

        mapView = view.findViewById(R.id.mapContainer)

        setupBerberHaritasi()

        return view
    }

    private fun setupBerberHaritasi() {
        mapView.setMultiTouchControls(true)
        val mapController = mapView.controller
        mapController.setZoom(15.0)

        val startPoint = GeoPoint(41.0015, 39.7568)
        mapController.setCenter(startPoint)
        main
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