package com.dock2dock.android.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dock2dock.android.R
import com.dock2dock.android.adapters.FragmentModel
import com.dock2dock.android.adapters.PagerAdapter
import com.dock2dock.android.fragments.*
import kotlinx.android.synthetic.main.activity_pick_item.*
import java.text.SimpleDateFormat
import java.util.*

class PickItemActivity : AppCompatActivity() {

    private lateinit var fragAdapter: PagerAdapter

    var formatter = SimpleDateFormat("E dd MMMM")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_item)
        setupFragment()
        populateDetails()
    }

    private fun setupFragment() {
        fragAdapter = PagerAdapter(supportFragmentManager)
        var fragments = arrayListOf(
            FragmentModel(PickItemFragment(), "Lines"),
            FragmentModel(StagingItemFragment(), "Staging"),
            FragmentModel(CrossdockItemFragment(), "Crossdock")
        )

        fragAdapter.fragments = fragments
        viewPager.adapter = fragAdapter

        tabs.setupWithViewPager(viewPager)
    }

    private fun populateDetails() {
        orderId.text = "123456"
        customerNameId.text = "New World Durham"
        address1Id.text = "45 Main Street"
        address2Id.text = "Christchurch City"
        address3Id.text = "Christchurch"

        var formatter = SimpleDateFormat("dd MMM yyyy")
       shipmentDateId.text = formatter.format(Date())
    }
}
