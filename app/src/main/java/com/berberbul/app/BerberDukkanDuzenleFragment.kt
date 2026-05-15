package com.berberbul.app

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

class BerberDukkanDuzenleFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mapView: MapView

    private var secilenLat: Double = 0.0
    private var secilenLon: Double = 0.0
    private var currentMarker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_berber_dukkan_duzenle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Configuration.getInstance().load(requireContext(), androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext()))

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        mapView = view.findViewById(R.id.map)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        val mapController = mapView.controller
        mapController.setZoom(15.0)

        val etDukkanAdi = view.findViewById<TextInputEditText>(R.id.etDukkanAdi)
        val etTelefon = view.findViewById<TextInputEditText>(R.id.etTelefon)
        val etAdres = view.findViewById<TextInputEditText>(R.id.etAdres)
        val btnKonumAl = view.findViewById<Button>(R.id.btnKonumAl)
        val tvKonumDurumu = view.findViewById<TextView>(R.id.tvKonumDurumu)
        val btnKaydet = view.findViewById<Button>(R.id.btnKaydet)

        val uid = auth.currentUser?.uid

        if (uid != null) {
            db.collection("berberler").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        etDukkanAdi.setText(document.getString("dukkanAdi"))
                        etTelefon.setText(document.getString("telefon"))
                        etAdres.setText(document.getString("adres"))
                        secilenLat = document.getDouble("enlem") ?: 0.0
                        secilenLon = document.getDouble("boylam") ?: 0.0

                        if (secilenLat != 0.0) {
                            tvKonumDurumu.text = "Konum: Kayıtlı"
                            val savedPoint = GeoPoint(secilenLat, secilenLon)
                            mapController.setCenter(savedPoint)
                            updateMarker(savedPoint)
                        }
                    }
                }
        }

        val receive = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                p?.let {
                    secilenLat = it.latitude
                    secilenLon = it.longitude
                    updateMarker(it)
                    tvKonumDurumu.text = "Konum: Seçildi"
                }
                return true
            }
            override fun longPressHelper(p: GeoPoint?): Boolean = false
        }
        mapView.overlays.add(MapEventsOverlay(receive))

        btnKonumAl.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2002)
            } else {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        secilenLat = location.latitude
                        secilenLon = location.longitude
                        val currentPoint = GeoPoint(secilenLat, secilenLon)
                        mapController.animateTo(currentPoint)
                        updateMarker(currentPoint)
                        tvKonumDurumu.text = "Konum: Alındı"
                    }
                }
            }
        }

        btnKaydet.setOnClickListener {
            val dukkanAdi = etDukkanAdi.text.toString().trim()
            val telefon = etTelefon.text.toString().trim()
            val adres = etAdres.text.toString().trim()

            if (dukkanAdi.isNotEmpty() && telefon.isNotEmpty()) {
                val berberMap = hashMapOf(
                    "dukkanAdi" to dukkanAdi,
                    "telefon" to telefon,
                    "adres" to adres,
                    "berberUid" to uid,
                    "enlem" to secilenLat,
                    "boylam" to secilenLon
                )

                if (uid != null) {
                    db.collection("berberler").document(uid).set(berberMap)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Kaydedildi!", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                }
            }
        }
    }

    private fun updateMarker(point: GeoPoint) {
        if (currentMarker == null) {
            currentMarker = Marker(mapView)
            mapView.overlays.add(currentMarker)
        }
        currentMarker?.position = point
        currentMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.invalidate()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 2002 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "İzin verildi, tekrar basın.", Toast.LENGTH_SHORT).show()
        }
    }
}