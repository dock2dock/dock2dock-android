package io.dock2dock.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.dock2dock.activities.databinding.ActivityPickItemBinding
import io.dock2dock.adapters.*
import io.dock2dock.crossdock.fragments.*
import io.dock2dock.fragments.*
import io.dock2dock.application.configuration.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPickItemBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Dock2DockConfiguration.init(this, "d2d_8f69aeb7ac874ae1964fbbd8f0758_fe3577")
        binding = ActivityPickItemBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupFragment()
        populateDetails()
        title = "Track Order"
    }

    private lateinit var fragAdapter: PagerAdapter

    var formatter = SimpleDateFormat("E dd MMMM")

    private fun setupFragment() {
        fragAdapter = PagerAdapter(supportFragmentManager)
        val fragments = arrayListOf(
            FragmentModel(PickItemFragment(), "Lines"),
            FragmentModel(StagingItemFragment(), "Staging"),
            FragmentModel(CrossdockLabelsFragment("1234706"), "Crossdock")
        )

        fragAdapter.fragments = fragments
        binding.viewPager.adapter = fragAdapter

        binding.tabs.setupWithViewPager(binding.viewPager)
    }

    private fun populateDetails() {
        binding.orderId.text = "123456"
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
