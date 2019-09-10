package com.hlq.hlq_snackbar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hlq.snackbar.NiceSnackBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NiceSnackBar.make(tv, "HLQ", NiceSnackBar.LENGTH_SHORT).show()
    }
}
