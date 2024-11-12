package com.example.uts_iot_backend

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.uts_iot_backend.R
import org.json.JSONException

class MainActivity : AppCompatActivity() {

    private lateinit var textViewSuhu: TextView
    private lateinit var textViewMax: TextView
    private lateinit var textViewMonthYear: TextView
    private lateinit var buttonLoadData: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        textViewSuhu = findViewById(R.id.textViewSuhu)
        textViewMax = findViewById(R.id.textViewMax)
        textViewMonthYear = findViewById(R.id.textViewMonthYear)
        buttonLoadData = findViewById(R.id.button)

        // Set button click listener
        buttonLoadData.setOnClickListener {
            loadWeatherData()
        }
    }

    private fun loadWeatherData() {
        val url = "http://10.0.2.2/Cuaca/get.php" // Change this to your API endpoint

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    // Parsing suhu_max, suhu_min, suhu_rata
                    val suhuMax = response.getString("suhumax")
                    val suhuMin = response.getString("suhumin")
                    val suhuRata = response.getString("suhurata")

                    // Set the text to TextViews
                    textViewSuhu.text = "Suhu Max: $suhuMax\nSuhu Min: $suhuMin\nSuhu Rata: $suhuRata"

                    // Parsing nilai_suhu_max_humid_max
                    val suhuMaxHumidArray = response.getJSONArray("nilai_suhu_max_humid_max")
                    val maxData = StringBuilder()
                    for (i in 0 until suhuMaxHumidArray.length()) {
                        val item = suhuMaxHumidArray.getJSONObject(i)
                        maxData.append("Suhu: ").append(item.getString("suhun")).append("\n")
                            .append("Humidity: ").append(item.getString("humid")).append("\n")
                            .append("Kecerahan: ").append(item.getString("kecerahan")).append("\n")
                            .append("Timestamp: ").append(item.getString("timestamp")).append("\n\n")
                    }
                    textViewMax.text = maxData.toString()

                    // Parsing month_year_max
                    val monthYearArray = response.getJSONArray("month_year_max")
                    val monthYearData = StringBuilder()
                    for (i in 0 until monthYearArray.length()) {
                        val item = monthYearArray.getJSONObject(i)
                        monthYearData.append("Bulan dan Tahun: ").append(item.getString("month_year"))
                            .append("\n")
                    }
                    textViewMonthYear.text = monthYearData.toString()

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                error.printStackTrace()
                Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        )

        // Adding request to request queue
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(jsonObjectRequest)
    }
}
