package io.dock2dock.example.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import io.dock2dock.android.application.configuration.Dock2DockConfiguration
import io.dock2dock.android.crossdock.components.LicensePlateScreen
import io.dock2dock.android.crossdock.fragments.CrossdockLabelsFragment
import io.dock2dock.android.crossdock.fragments.LicensePlatesFragment
import io.dock2dock.android.crossdock.viewModels.LicensePlateViewModel
import io.dock2dock.example.BuildConfig
import io.dock2dock.example.R
import io.dock2dock.example.adapters.FragmentModel
import io.dock2dock.example.adapters.PagerAdapter
import io.dock2dock.example.databinding.ActivityPickItemBinding
import io.dock2dock.example.fragments.PickItemFragment
import io.dock2dock.example.fragments.StagingItemFragment
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPickItemBinding
    private lateinit var licensePlateViewModel: LicensePlateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Dock2DockConfiguration.init(this, BuildConfig.Dock2Dock_ApiKey, "http://localhost:3000/")

        binding = ActivityPickItemBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        populateDetails()
        title = "Track Order"

        licensePlateViewModel = LicensePlateViewModel(salesOrderNo)
        licensePlateViewModel.onLicensePlateChanged = {
            //do something with license plate
        }

        findViewById<ComposeView>(R.id.license_plate_compose_view).setContent {
            LicensePlateScreen(viewModel = licensePlateViewModel)
        }

        setupFragment()
    }

    private lateinit var fragAdapter: PagerAdapter

    var formatter = SimpleDateFormat("E dd MMMM")
    private val salesOrderNo = "SO1002"

    private fun setupFragment() {
        fragAdapter = PagerAdapter(supportFragmentManager)

        val licensePlatesFragment = LicensePlatesFragment(salesOrderNo) {
            licensePlateViewModel.load(it.no)
        }

        val fragments = arrayListOf(
            FragmentModel(PickItemFragment(), "Lines"),
            FragmentModel(StagingItemFragment(), "Staging"),
            FragmentModel(licensePlatesFragment, "LP"),
            FragmentModel(CrossdockLabelsFragment(salesOrderNo), "Crossdock")
        )

        fragAdapter.fragments = fragments
        binding.viewPager.adapter = fragAdapter
        binding.viewPager.offscreenPageLimit = 3

        binding.tabs.setupWithViewPager(binding.viewPager)
    }

    private fun populateDetails() {
        binding.orderId.text = salesOrderNo
        binding.sellToCustomerNameTxt.text = "Pak N Save Rangiora (9012)"
        binding.addressId.text = "Pak N Save Rangiora \n2 Southbrook Road \nRangiora \n7400"

        val formatter = SimpleDateFormat("dd MMM yyyy")
        binding.shipmentDateId.text = formatter.format(Date())
    }
}