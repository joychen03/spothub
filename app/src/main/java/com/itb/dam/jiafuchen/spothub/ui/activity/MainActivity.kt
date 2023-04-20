package com.itb.dam.jiafuchen.spothub.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.itb.dam.jiafuchen.spothub.MainNavDirections
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.app
import com.itb.dam.jiafuchen.spothub.databinding.ActivityMainBinding
import com.itb.dam.jiafuchen.spothub.domain.model.AddEditPostArgs
import com.itb.dam.jiafuchen.spothub.ui.fragment.*
import com.itb.dam.jiafuchen.spothub.ui.viemodel.SharedViewModel
import com.itb.dam.jiafuchen.spothub.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
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
        R.id.mapLocatingFragment,
        R.id.settingFragment,
        R.id.postDetailFragment,
        R.id.profileEditFragment,
        R.id.editPostFragment
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
                val direction = MainNavDirections.toAddPost(AddEditPostArgs())
                navController.navigate(direction)
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

                val avatar = binding.navigationView.getHeaderView(0).findViewById<ImageView>(R.id.nav_avatar)
                avatar.setImageBitmap(Utils.byteArrayToImage(user.avatar))
            }else{
                Utils.makeSimpleAlert(this,"ERROR SYNC APP USER")
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