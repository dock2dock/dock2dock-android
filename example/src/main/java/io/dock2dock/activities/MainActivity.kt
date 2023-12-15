package io.dock2dock.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import io.dock2dock.activities.databinding.ActivityPickItemBinding
import io.dock2dock.adapters.FragmentModel
import io.dock2dock.adapters.PagerAdapter
import io.dock2dock.android.components.LicensePlateScreen
import io.dock2dock.android.configuration.Dock2DockConfiguration
import io.dock2dock.android.fragments.CrossdockLabelsFragment
import io.dock2dock.android.fragments.LicensePlatesFragment
import io.dock2dock.android.viewModels.LicensePlateViewModel
import io.dock2dock.fragments.PickItemFragment
import io.dock2dock.fragments.StagingItemFragment
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

        licensePlateViewModel = LicensePlateViewModel()

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

        var licensePlatesFragment = LicensePlatesFragment(salesOrderNo) {
            licensePlateViewModel.refresh(it.no)
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
        binding.sellToCustomerNameTxt.text = "New World Durham Street"
        binding.customerNameId.text = "New World Durham Street"
        binding.address1Id.text = "175 Durham Street South"
        binding.address2Id.text = "Christchurch Central City"
        binding.address3Id.text = "Christchurch"

        val formatter = SimpleDateFormat("dd MMM yyyy")
        binding.shipmentDateId.text = formatter.format(Date())
    }
}