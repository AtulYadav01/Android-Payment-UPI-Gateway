package com.e.androidpaymentupigateway

import android.app.Activity
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
import java.util.*

class MainActivity : AppCompatActivity() {

  /*  Made by Atul Yadav
    https://github.com/AtulYadav01*/

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

    val r = Random()
    private val output =
            r.nextInt(2000000 - 200000 + 1).toString() + "20000"

    private fun upipayment(money: String, note: String, username: String, Upi_id: String) {
        val uri = Uri.parse("upi://pay").buildUpon()
                //Use Merchant Upi ID only, it will not work with normal UPI Id
                .appendQueryParameter("pa", Upi_id)
                .appendQueryParameter("pn", username)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("tr", output)
                .appendQueryParameter("am", money)
                .appendQueryParameter("cu", "INR")
                .build()

        val upipayintent = Intent(Intent.ACTION_VIEW)
        upipayintent.setData(uri)

       /* if you want to open a particular app then

        val upipayintent = Intent(Intent.ACTION_VIEW)
        when {
            upigpay == "1" -> {
                upipayintent.setPackage("com.google.android.apps.nbu.paisa.user")
                upipayintent.setData(uri)
            }
            upiphonepe == "1" -> {
                upipayintent.setPackage("com.phonepe.app")
                upipayintent.setData(uri)
            }
            else -> {
                upipayintent.setData(uri)
            }
        }

        Here upigpay and upiphonepe is the variable on which we get value and open app accordingly
        */

        val chooser = Intent.createChooser(upipayintent, "Pay With")

        if (null != chooser.resolveActivity(packageManager)) {
            startActivityForResult(chooser, UPI_PAYMENT)
        } else {
            Toast.makeText(this, "No Upi app found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            UPI_PAYMENT -> if (Activity.RESULT_OK == resultCode || resultCode == 11) {
                if (data != null) {
                    val trxt = data.getStringExtra("response")
                    Log.d("UPI", "onActivityResult: $trxt")
                    val dataList = ArrayList<String>()
                    if (trxt != null) {
                        dataList.add(trxt)
                    }
                    upiPaymentDataOperation(dataList)
                } else {
                    Log.d("UPI", "onActivityResult: " + "Return data is null")
                    val dataList = ArrayList<String>()
                    dataList.add("nothing")
                    upiPaymentDataOperation(dataList)
                }
            } else {
                Log.d("UPI", "onActivityResult: " + "Return data is null") //when user simply back without payment
                val dataList = ArrayList<String>()
                dataList.add("nothing")
                upiPaymentDataOperation(dataList)
            }
        }
    }

    private fun upiPaymentDataOperation(data: ArrayList<String>) {
        if (isConnectionAvailable(this@MainActivity)) {
            var str: String? = data[0]
            Log.d("UPIPAY", "upiPaymentDataOperation: " + str!!)
            var paymentCancel = ""
            if (str == null) str = "discard"
            var status = ""
            var approvalRefNo = ""
            val response = str.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (i in response.indices) {
                val equalStr = response[i].split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (equalStr.size >= 2) {
                    if (equalStr[0].toLowerCase(Locale.ROOT) == "Status".toLowerCase(Locale.ROOT)) {
                        status = equalStr[1].toLowerCase(Locale.ROOT)
                    } else if (equalStr[0].equals("ApprovalRefNo", ignoreCase = true) || equalStr[0].toLowerCase(
                                    Locale.ROOT
                            ) == "txnRef".toLowerCase(Locale.ROOT)
                    ) {
                        approvalRefNo = equalStr[1]
                    }
                } else {
                    paymentCancel = "Payment cancelled by user."
                }
            }

            if (status == "success") {
                //Code to handle successful transaction here.
                Toast.makeText(this@MainActivity, "Transaction successful.", Toast.LENGTH_SHORT).show()
                Log.d("UPI", "responseStr: $approvalRefNo")
            } else if ("Payment cancelled by user." == paymentCancel) {
                Toast.makeText(this@MainActivity, "Payment cancelled by user.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this@MainActivity, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {

        fun isConnectionAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager != null) {
                val netInfo = connectivityManager.activeNetworkInfo
                if (netInfo != null && netInfo.isConnected
                        && netInfo.isConnectedOrConnecting
                        && netInfo.isAvailable) {
                    return true
                }
            }
            return false
        }
    }
}