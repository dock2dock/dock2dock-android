package com.dock2dock.android.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dock2dock.android.R
import com.dock2dock.android.adapters.FragmentModel
import com.dock2dock.android.adapters.PagerAdapter
import com.dock2dock.android.fragments.CrossdockItemFragment
import com.dock2dock.android.fragments.PickItemFragment
import com.dock2dock.android.fragments.StagingItemFragment
import com.dock2dock.android.networking.configuration.Dock2DockConfiguration
import kotlinx.android.synthetic.main.activity_pick_item.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Dock2DockConfiguration.init(this, "d96d4b92-d746-45a7-8131-bf9a187c65ea", "Kz2~8!9H89KUZONX2a7qm7-e-61U8J5~kZ-6c7~~-0t~Y-4!zC")
        setContentView(R.layout.activity_pick_item)
        setupFragment()
        populateDetails()
    }

    private lateinit var fragAdapter: PagerAdapter

    var formatter = SimpleDateFormat("E dd MMMM")

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
//
//@Composable
//fun Greeting(name: String) {
//    Text(text = "Hello $name!")
//}
//
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    AndroidSdkTheme {
//        Greeting("Android")
//    }
//}
