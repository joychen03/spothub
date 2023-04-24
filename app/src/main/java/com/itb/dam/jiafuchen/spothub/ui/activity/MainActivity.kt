package com.itb.dam.jiafuchen.spothub.ui.activity

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.get
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
import io.realm.kotlin.notifications.DeletedObject
import io.realm.kotlin.notifications.InitialObject
import io.realm.kotlin.notifications.UpdatedObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
        R.id.editPostFragment,
        R.id.userDetailFragment,
    )

    //region Lifecycle

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
                    badgeRemove()
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

        sharedViewModel.currentUser.observe(this){
            if(it != null){
                sharedViewModel.getCurrentUserAsFlow().onEach { user ->
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
                    }
                }.launchIn(CoroutineScope(Dispatchers.Main))

                sharedViewModel.getTotalPosts().onEach {
                    if(!sharedViewModel.appJustStarted){
                        if(it.isNotEmpty() && sharedViewModel.lastTotalPostCount != it.count()) {
                            badgeSet()
                        }
                    }else{
                        sharedViewModel.lastTotalPostCount = it.count()
                        sharedViewModel.appJustStarted = false
                    }
                }.launchIn(CoroutineScope(Dispatchers.Main))
            }
        }

    }

    //endregion

    //region Callbacks

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
                        sharedViewModel.removeCurrentUser()
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

    //endregion

    //region Functions

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

    fun openDrawer(){
        binding.drawerLayout.open()
    }

    private fun badgeSet(){
        val badge = binding.bottomNav.getOrCreateBadge(R.id.nav_home)
        badge.isVisible = true
    }

    private fun badgeRemove(){
        val badge = binding.bottomNav.getOrCreateBadge(R.id.nav_home)
        badge.isVisible = false
    }

    fun homeNavRefreshAnimationStart(){
        CoroutineScope(Dispatchers.Main).launch {
            binding.bottomNav.menu.getItem(0).icon = getDrawable(R.drawable.refresh_icon)
            delay(1000)
            binding.bottomNav.menu.getItem(0).icon = getDrawable(R.drawable.nav_home_icon)
        }
    }
    //endregion

}