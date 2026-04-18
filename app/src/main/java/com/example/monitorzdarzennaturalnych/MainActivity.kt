package com.example.monitorzdarzennaturalnych

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Wczytujemy prosty podstawowy ekran zdefiniowany w activity_main.xml
        setContentView(R.layout.activity_main)
    }
}
