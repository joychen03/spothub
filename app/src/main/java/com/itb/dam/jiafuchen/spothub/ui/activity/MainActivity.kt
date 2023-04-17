package com.itb.dam.jiafuchen.spothub.ui.activity

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarItemView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.TAG
import com.itb.dam.jiafuchen.spothub.app
import com.itb.dam.jiafuchen.spothub.data.mongodb.AuthRepository
import com.itb.dam.jiafuchen.spothub.databinding.ActivityMainBinding
import com.itb.dam.jiafuchen.spothub.domain.model.User
import com.itb.dam.jiafuchen.spothub.ui.fragment.*
import com.itb.dam.jiafuchen.spothub.ui.viemodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.realm.kotlin.mongodb.ext.customDataAsBsonDocument
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener {

    lateinit var binding: ActivityMainBinding
    lateinit var navHostFragment : NavHostFragment
    lateinit var navController : NavController
    private val sharedViewModel : SharedViewModel by viewModels()

    private var hideBottomNavFragments = listOf(
        R.id.addPostFragment,
        R.id.loginFragment,
        R.id.registerFragment,
        R.id.cameraFragment,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        binding.navigationView.setNavigationItemSelectedListener(this)

        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nav_home -> {
                    navController.navigate(R.id.toHome)
                }
                R.id.nav_search -> {
                    navController.navigate(R.id.toSearch)
                }
                R.id.nav_map -> {
                    navController.navigate(R.id.toMap)
                }
                R.id.nav_profile -> {
                    navController.navigate(R.id.toProfile)
                }

                else -> {

                }
            }
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if(destination.id in hideBottomNavFragments){
                setBottomNavigationVisibility(false)
            }else{
                setBottomNavigationVisibility(true)
            }
        }

        binding.floatingActionButton.setOnClickListener {
            val currentFragment = navHostFragment.childFragmentManager.fragments[0]
            if(currentFragment !is AddPostFragment){
                navController.navigate(R.id.toAddPost)
            }

        }

        sharedViewModel.currentUser.observe(this) { user ->
            if(user != null) {
                val name = binding.navigationView.getHeaderView(0).findViewById<TextView>(R.id.nav_nickname)
                name.text = user.username

                val email = binding.navigationView.getHeaderView(0).findViewById<TextView>(R.id.nav_email)
                email.text = user.email

                val followers = binding.navigationView.getHeaderView(0).findViewById<TextView>(R.id.nav_followers)
                followers.text = user.followers.count().toString()

                val following = binding.navigationView.getHeaderView(0).findViewById<TextView>(R.id.nav_following)
                following.text = user.followings.count().toString()
            }
        }
    }

    private fun init(){
        navHostFragment = supportFragmentManager.findFragmentById(R.id.mainFragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNav.background = null
        binding.bottomNav.menu.getItem(2).isEnabled = false

        sharedViewModel.getCurrentUser()
    }


    fun setBottomNavigationVisibility(visible: Boolean) {
        binding.bottomAppBar.isVisible = visible

        if(visible){
            binding.floatingActionButton.show()
        }else{
            binding.floatingActionButton.hide()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_settings -> {
                binding.drawerLayout.close()
                val currentFragment = navHostFragment.childFragmentManager.fragments[0]
                if(currentFragment !is SettingFragment){
                    navController.navigate(R.id.toSetting)
                }
            }
            R.id.nav_logout -> {
                binding.drawerLayout.close()
                CoroutineScope(Dispatchers.Main).launch {
                    runCatching {
                        app.currentUser?.logOut()
                        app.currentUser?.remove()
                    }.onSuccess {
                        val currentFragment = navHostFragment.childFragmentManager.fragments[0]
                        if(currentFragment !is LoginFragment){
                            navController.navigate(R.id.toLogin)
                        }
                    }.onFailure {
                        it.message?.let { it1 -> Log.v("ERROR", it1) }
                    }
                }
            }
        }
        return true
    }

    fun openDrawer(){
        binding.drawerLayout.open()
    }


}