package com.itb.dam.jiafuchen.spothub.ui.activity

import android.app.Activity
import com.itb.dam.jiafuchen.spothub.ui.fragment.MapFragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.databinding.ActivityMainBinding
import com.itb.dam.jiafuchen.spothub.ui.fragment.AddPostFragment
import com.itb.dam.jiafuchen.spothub.ui.fragment.HomeFragment
import com.itb.dam.jiafuchen.spothub.ui.fragment.ProfileFragment
import com.itb.dam.jiafuchen.spothub.ui.fragment.SearchFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
//        setTheme(R.style.Theme_Spothub);
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNav.background = null
        binding.bottomNav.menu.getItem(2).isEnabled = false
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainFragment) as NavHostFragment
        val navController = navHostFragment.navController



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

        binding.floatingActionButton.setOnClickListener {
            if(navController.currentDestination?.id != 1 ){
                println(navController.currentDestination?.label)
                navController.navigate(R.id.toAddPost)
            }

        }
        //menuItem.icon?.setTintList(ColorStateList.valueOf(ContextCompat.getColor(th.is, R.color.primary_btn)))
    }

    fun setBottomNavigationVisibility(visible: Boolean) {
        binding.bottomAppBar.isVisible = visible
        binding.floatingActionButton.isVisible = visible
    }

}