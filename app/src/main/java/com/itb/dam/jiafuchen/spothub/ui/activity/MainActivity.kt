package com.itb.dam.jiafuchen.spothub.ui.activity

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.databinding.ActivityMainBinding
import com.itb.dam.jiafuchen.spothub.ui.fragment.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private var hideBottomNavFragments = listOf(
        R.id.addPostFragment,
        R.id.loginFragment,
        R.id.registerFragment,
        R.id.cameraFragment,
        R.id.settingFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNav.background = null
        binding.bottomNav.menu.getItem(2).isEnabled = false
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainFragment) as NavHostFragment
        val navController = navHostFragment.navController

        //list of fragment that hides the bottom navigation bar


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


        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if(destination.id in hideBottomNavFragments){
                setBottomNavigationVisibility(false)
            }else{
                setBottomNavigationVisibility(true)
            }
        }


        binding.floatingActionButton.setOnClickListener {
//            val currentFragment = navHostFragment.childFragmentManager.fragments[0]
//            if(currentFragment !is AddPostFragment){
//                navController.navigate(R.id.toAddPost)
//            }

//            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
//                Configuration.UI_MODE_NIGHT_NO -> {
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//                }
//                Configuration.UI_MODE_NIGHT_YES -> {
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//                }
//            }

            navController.navigate(R.id.action_global_settingFragment)

        }


        //menuItem.icon?.setTintList(ColorStateList.valueOf(ContextCompat.getColor(th.is, R.color.primary_btn)))
    }

    fun setBottomNavigationVisibility(visible: Boolean) {
        binding.bottomAppBar.isVisible = visible

        if(visible){
            binding.floatingActionButton.show()
        }else{
            binding.floatingActionButton.hide()
        }
    }

}