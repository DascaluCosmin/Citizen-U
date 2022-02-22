package com.ubb.citizen_u.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.ActivityMainBinding
import com.ubb.citizen_u.ui.fragments.SignedInFragment
import com.ubb.citizen_u.ui.util.loadLocale
import com.ubb.citizen_u.ui.viewmodels.AuthenticationViewModel
import com.ubb.citizen_u.ui.workers.NotificationWorker
import com.ubb.citizen_u.util.NotificationsConstants.NOTIFICATION_WORKER_TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        private const val CITIZEN_ID_ARG_KEY = "citizenId"
        private const val DAILY_NOTIFICATION_HOUR = 22
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
        navController.setGraph(R.navigation.nav_graph_main, bundle)

        drawerLayout = binding.drawerLayout
        binding.navigationView.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        overrideNavigationDrawerItems()
        startPeriodicNotificationWork()
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

    private fun startPeriodicNotificationWork() {
        val delay = computeDelayInMinutes()
        Log.d(SignedInFragment.TAG,
            "startPeriodicNotificationWork: Starting periodic notification work, to be executed in $delay minutes...")
        val notificationWork = PeriodicWorkRequest.Builder(
            NotificationWorker::class.java,
            1,
            TimeUnit.DAYS,
        )
            .setInitialDelay(delay, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).cancelAllWork()
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                NOTIFICATION_WORKER_TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                notificationWork
            )
    }

    private fun computeDelayInMinutes(): Long {
        val dailyNotificationHourCalendar = Calendar.getInstance()
        dailyNotificationHourCalendar.set(Calendar.HOUR_OF_DAY, 20)
        dailyNotificationHourCalendar.set(Calendar.MINUTE, 30)
        dailyNotificationHourCalendar.set(Calendar.SECOND, 0)

        val now = Calendar.getInstance()
        var delta = (dailyNotificationHourCalendar.timeInMillis - now.timeInMillis)
            .milliseconds
            .inWholeMinutes
        if (delta < 0) {
            delta += TimeUnit.DAYS.toMinutes(1)
        }
        return delta
    }
}
