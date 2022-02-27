package com.ubb.citizen_u.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.ActivityAuthenticationBinding
import com.ubb.citizen_u.util.NotificationsConstants.NOTIFICATION_PERIODIC_EVENT_EVENT_ID_KEY
import com.ubb.citizen_u.util.NotificationsConstants.NOTIFICATION_PUBLIC_RELEASE_EVENT_ID_KEY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController =
            (supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment).navController

        shouldGoToPeriodicEventDetails()?.let {
            val bundle = Bundle()
            bundle.putString(NOTIFICATION_PERIODIC_EVENT_EVENT_ID_KEY, it)

            navController.setGraph(R.navigation.nav_graph_authentication, bundle)
        }
    }

    private fun shouldGoToPeriodicEventDetails(): String? {
        return intent.getStringExtra(NOTIFICATION_PERIODIC_EVENT_EVENT_ID_KEY)
    }
}