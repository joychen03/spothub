package com.itb.dam.jiafuchen.spothub.ui.activity

import MapFragment
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
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



        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                }
                R.id.nav_search -> {
                    replaceFragment(SearchFragment())
                }
                R.id.nav_map -> {
                    replaceFragment(MapFragment())
                }
                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment())
                }

                else -> {

                }
            }
            true
        }

        binding.floatingActionButton.setOnClickListener {
            replaceFragment(AddPostFragment())
        }
        //menuItem.icon?.setTintList(ColorStateList.valueOf(ContextCompat.getColor(th.is, R.color.primary_btn)))
    }


    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainFragment, fragment)
        fragmentTransaction.commit()
    }
    fun setBottomNavigationVisibility(visible: Boolean) {
        binding.bottomAppBar.isVisible = visible
        binding.floatingActionButton.isVisible = visible
    }

}