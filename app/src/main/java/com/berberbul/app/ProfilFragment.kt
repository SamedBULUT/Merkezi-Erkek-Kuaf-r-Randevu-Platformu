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
 * Bu sayfada müşterinin kişisel bilgileri ve harita yer alır.
 */
class ProfilFragment : Fragment() {

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
        // Osmdroid yapılandırmasını yükle
        val context = requireContext()
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))

        // Tasarımı (Layout) bağla
        val view = inflater.inflate(R.layout.fragment_berber_yonetim, container, false)

        // Haritayı başlat
        mapView = view.findViewById(R.id.mapContainer)
        setupBerberHaritasi()

        return view
    }

    private fun setupBerberHaritasi() {
        mapView.setMultiTouchControls(true)
        val mapController = mapView.controller
        mapController.setZoom(15.0)

        // Başlangıç noktası (Trabzon koordinatları)
        val startPoint = GeoPoint(41.0015, 39.7568)
        mapController.setCenter(startPoint)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Profil bilgilerini burada doldurabilirsin
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
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