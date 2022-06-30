package com.ubb.citizen_u.ui

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.ActivityMainBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.fragments.SignedInFragment
import com.ubb.citizen_u.ui.util.loadLocale
import com.ubb.citizen_u.ui.viewmodels.AuthenticationViewModel
import com.ubb.citizen_u.util.DEFAULT_ERROR_MESSAGE
import com.ubb.citizen_u.util.NotificationsConstants.EVENT_PUSH_NOTIFICATION_TOPIC_ID
import com.ubb.citizen_u.util.NotificationsConstants.NOTIFICATION_PERIODIC_EVENT_EVENT_ID_KEY
import com.ubb.citizen_u.util.NotificationsConstants.PUBLIC_RELEASE_PUSH_NOTIFICATION_TOPIC_ID
import com.ubb.citizen_u.util.networking.NetworkReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging

    @Inject
    lateinit var networkReceiver: NetworkReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        loadLocale()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkReceiver, filter)

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

        firebaseMessaging.subscribeToTopic(EVENT_PUSH_NOTIFICATION_TOPIC_ID)
        firebaseMessaging.subscribeToTopic(PUBLIC_RELEASE_PUSH_NOTIFICATION_TOPIC_ID)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectSignOutState() }
            }
        }
    }

    private suspend fun collectSignOutState() {
        authenticationViewModel.signOutState.collect {
            Log.d(SignedInFragment.TAG, "collectSignOutState: Collecting response $it")
            when (it) {
                Response.Loading -> {
                }
                is Response.Error -> {
                    Log.d(SignedInFragment.TAG,
                        "collectSignOutState: An error has occurred ${it.message}")
                    Toast.makeText(this, DEFAULT_ERROR_MESSAGE, Toast.LENGTH_SHORT).show()
                }
                is Response.Success -> {
                    super.onBackPressed()
                }
            }
        }
    }

    private fun overrideNavigationDrawerItems() {
        binding.navigationView.menu.findItem(R.id.loginFragment).setOnMenuItemClickListener {
            authenticationViewModel.signOut()
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

    fun onBackPressedLogout() {
        super.onBackPressed()
    }
}
