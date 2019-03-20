package com.listener.dynamicimages

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner




class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener{
    var logoView: ImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logoView = findViewById(R.id.current_logo)

        loadLogo(ImageContract.Logo.CONTENT_URI, logoView)

        setupSpinner()
    }

    private fun setupSpinner() {

        // Spinner element
        val spinner = findViewById<Spinner>(R.id.spinner)

        // Spinner Drop down elements
        val months = ArrayList<String>()
        months.add("January")
        months.add("February")
        months.add("March")
        months.add("April")
        months.add("May")
        months.add("June")
        months.add("July")
        months.add("August")
        months.add("September")
        months.add("October")
        months.add("November")
        months.add("December")

        // Creating adapter for spinner
        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // attaching data adapter to spinner
        spinner.adapter = dataAdapter
        spinner.prompt = "Pick a month"
        spinner.setSelection(2)
        // Spinner click listener
        spinner.onItemSelectedListener = this
    }


    private fun loadLogo(imageUri: Uri, imageView: ImageView?) {
        logoView?.setImageURI(imageUri)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val month = parent?.getItemAtPosition(position).toString()

        val monthOrdinal: Int = when {
            month.compareTo("January") == 0 -> 0
            month.compareTo("February") == 0 -> 1
            month.compareTo("March") == 0 -> 2
            month.compareTo("April") == 0 -> 3
            month.compareTo("May") == 0 -> 4
            month.compareTo("June") == 0 -> 5
            month.compareTo("July") == 0 -> 6
            month.compareTo("August") == 0 -> 7
            month.compareTo("September") == 0 -> 8
            month.compareTo("October") == 0 -> 9
            month.compareTo("November") == 0 -> 10
            month.compareTo("December") == 0 -> 11
            else -> 0
        }

        loadLogo(ImageContract.Logo.buildUriByMonth(monthOrdinal), logoView)
    }
}
