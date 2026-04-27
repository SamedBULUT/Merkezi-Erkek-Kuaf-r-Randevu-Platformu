package com.berberbul.app

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.util.GeoPoint

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfilFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    // Harita değişkenimiz
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 1. Adım: Harita ayarını yükle
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))

        // 2. Adım: Kendi tasarımını (layout) kullan
        val view = inflater.inflate(R.layout.fragment_berber_yonetim, container, false)

        // 3. Adım: Haritayı başlat
        mapView = view.findViewById(R.id.mapContainer)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(15.0)
        // Trabzon koordinatları
        mapView.controller.setCenter(GeoPoint(41.0015, 39.7568))

        return view
    }

    // Haritanın uygulama içinde düzgün çalışması için eklenmeli
    override fun onResume() {
        super.onResume()
        if (::mapView.isInitialized) {
            mapView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::mapView.isInitialized) {
            mapView.onPause()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfilFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}