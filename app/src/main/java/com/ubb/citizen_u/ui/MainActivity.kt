package com.ubb.citizen_u.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.ActivityMainBinding
import com.ubb.citizen_u.ui.util.loadLocale
import com.ubb.citizen_u.ui.viewmodels.AuthenticationViewModel
import com.ubb.citizen_u.util.NotificationsConstants.NOTIFICATION_PERIODIC_EVENT_EVENT_ID_KEY
import com.ubb.citizen_u.util.NotificationsConstants.NOTIFICATION_PUBLIC_RELEASE_EVENT_ID_KEY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "UBB-MainActivity"
        private const val CITIZEN_ID_ARG_KEY = "citizenId"
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    private val authenticationViewModel: AuthenticationViewModel by viewModels()
    private val args: MainActivityArgs by navArgs()

    /**
     *  Uncomment in order to show App Bar
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        loadLocale()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // setSupportActionBar(binding.toolbar)

        navController =
            (supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment).navController

        val bundle = Bundle()
        bundle.putString(CITIZEN_ID_ARG_KEY, args.citizenId)
        bundle.putString(NOTIFICATION_PERIODIC_EVENT_EVENT_ID_KEY, args.periodicEventDetailsId)
        navController.setGraph(R.navigation.nav_graph_main, bundle)

        drawerLayout = binding.drawerLayout
        binding.navigationView.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        overrideNavigationDrawerItems()
//         setupActionBarWithNavController(navController, appBarConfiguration)
//        supportActionBar?.hide()
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.drawer_menu, menu)
//        return true
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    private fun overrideNavigationDrawerItems() {
        binding.navigationView.menu.findItem(R.id.loginFragment).setOnMenuItemClickListener {
            authenticationViewModel.signOut()

            // TODO: This doesn't work as expected for Screens other than Home (has to be pressed twice)
            super.onBackPressed()
            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onNavigateUp()
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id == navController.currentDestination?.parent?.startDestinationId) {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } else {
            super.onBackPressed()
        }
    }
}
