package io.dock2dock.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.dock2dock.activities.databinding.ActivityPickItemBinding
import io.dock2dock.adapters.FragmentModel
import io.dock2dock.adapters.PagerAdapter
import io.dock2dock.android.configuration.Dock2DockConfiguration
import io.dock2dock.android.fragments.CrossdockLabelsFragment
import io.dock2dock.fragments.PickItemFragment
import io.dock2dock.fragments.StagingItemFragment
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPickItemBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Dock2DockConfiguration.init(this, BuildConfig.Dock2Dock_ApiKey, "https://api.nonprod.dock2dock.io/")

        binding = ActivityPickItemBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupFragment()
        populateDetails()
        title = "Track Order"
    }

    private lateinit var fragAdapter: PagerAdapter

    var formatter = SimpleDateFormat("E dd MMMM")
    private val salesOrderNo = "42202"

    private fun setupFragment() {
        fragAdapter = PagerAdapter(supportFragmentManager)
        val fragments = arrayListOf(
            FragmentModel(PickItemFragment(), "Lines"),
            FragmentModel(StagingItemFragment(), "Staging"),
            FragmentModel(CrossdockLabelsFragment(salesOrderNo), "Crossdock")
        )

        fragAdapter.fragments = fragments
        binding.viewPager.adapter = fragAdapter
        binding.viewPager.offscreenPageLimit = 3

        binding.tabs.setupWithViewPager(binding.viewPager)
    }

    private fun populateDetails() {
        binding.orderId.text = salesOrderNo
        binding.sellToCustomerNameTxt.text = "New World Durham Street"
        binding.customerNameId.text = "New World Durham Street"
        binding.address1Id.text = "175 Durham Street South"
        binding.address2Id.text = "Christchurch Central City"
        binding.address3Id.text = "Christchurch"

        val formatter = SimpleDateFormat("dd MMM yyyy")
        binding.shipmentDateId.text = formatter.format(Date())
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
