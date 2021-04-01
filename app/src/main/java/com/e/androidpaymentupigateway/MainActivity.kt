package com.e.androidpaymentupigateway

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton

class MainActivity : AppCompatActivity() {

    private lateinit var amount: EditText
    private lateinit var upi_id: EditText
    private lateinit var name: EditText
    private lateinit var note: EditText
    private lateinit var send: AppCompatButton
    private val UPI_PAYMENT = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        amount = findViewById(R.id.amount)
        upi_id = findViewById(R.id.upi_id)
        name = findViewById(R.id.name)
        note = findViewById(R.id.note)
        send = findViewById(R.id.send)


        send.setOnClickListener {
            val money = amount.text.toString()
            val note = note.text.toString()
            val username = name.text.toString()
            val Upi_id = upi_id.text.toString()
            upipayment(money, note, username, Upi_id)
        }

    }

    private fun upipayment(money: String, note: String, username: String, Upi_id: String) {
        val uri = Uri.parse("upi://pay").buildUpon()
                //Use Merchant Upi ID only, it will not work with normal UPI Id
                .appendQueryParameter("pa", Upi_id)
                .appendQueryParameter("pn", username)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", money)
                .appendQueryParameter("cu", "INR")
                .build()

        val upipayintent = Intent(Intent.ACTION_VIEW)
        upipayintent.setData(uri)

        val chooser = Intent.createChooser(upipayintent, "Pay With")

        if (null != chooser.resolveActivity(packageManager)) {
            startActivityForResult(chooser, UPI_PAYMENT)
        } else {
            Toast.makeText(this, "No Upi app found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("code", resultCode.toString())
        when (requestCode) {
            UPI_PAYMENT ->
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null)
                    {
                        val trxdata = data.getStringExtra("response")
                        Log.d("UPI1", "onActivityResult: " + trxdata)
                    }
                    else
                    {
                        Log.d("UPI2", "onActivityResult: " + "Return data is null")

                    }
                }
                else
                {
                    Log.d("UPI3", "onActivityResult: " + "Return data is null") //when user simply back without payment
                }
        }
    }
}