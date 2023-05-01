package com.itb.dam.jiafuchen.spothub.ui.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.itb.dam.jiafuchen.spothub.MainNavDirections
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.app
import com.itb.dam.jiafuchen.spothub.databinding.ActivityMainBinding
import com.itb.dam.jiafuchen.spothub.domain.model.Setting
import com.itb.dam.jiafuchen.spothub.ui.fragment.AddPostFragment
import com.itb.dam.jiafuchen.spothub.ui.fragment.HomeFragment
import com.itb.dam.jiafuchen.spothub.ui.fragment.SettingFragment
import com.itb.dam.jiafuchen.spothub.ui.viewmodel.SharedViewModel
import com.itb.dam.jiafuchen.spothub.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = "settings")

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
                    val currentFragment = navHostFragment.childFragmentManager.fragments[0]
                    sharedViewModel.homeShouldUpdate = currentFragment is HomeFragment
                    if(sharedViewModel.homeShouldUpdate){
                        badgeRemove()
                    }
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
                val direction = MainNavDirections.toAddPost(null)
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

                sharedViewModel.watchForNewPosts()

            }
        }

        sharedViewModel.newPost.observe(this){
            if(it != null){
                badgeSet()
                sharedViewModel.newPost.postValue(null)
            }
        }

        sharedViewModel.homeUpdated.observe(this){
            if(it){
                badgeRemove()
                sharedViewModel.homeUpdated.postValue(false)
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            getSettings().collect {
                if(it.darkMode){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
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
                        finish();
                        startActivity(intent);
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
            binding.bottomNav.menu.getItem(0).icon = AppCompatResources.getDrawable(this@MainActivity, R.drawable.refresh_icon)
            delay(500)
            binding.bottomNav.menu.getItem(0).icon = AppCompatResources.getDrawable(this@MainActivity, R.drawable.nav_home_icon)
        }
    }

    private fun getSettings() = dataStore.data.map { setting ->
        Setting(
            setting[booleanPreferencesKey("dark_mode")] ?: false,
            setting[stringPreferencesKey("language")].orEmpty()
        )
    }
    //endregion

}