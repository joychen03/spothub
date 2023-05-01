package com.itb.dam.jiafuchen.spothub.ui.fragment

import android.annotation.SuppressLint
import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.databinding.FragmentHomeBinding
import com.itb.dam.jiafuchen.spothub.databinding.FragmentProfileBinding
import com.itb.dam.jiafuchen.spothub.databinding.FragmentSettingBinding
import com.itb.dam.jiafuchen.spothub.domain.model.Setting
import com.itb.dam.jiafuchen.spothub.ui.activity.MainActivity
import com.itb.dam.jiafuchen.spothub.ui.activity.dataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class SettingFragment : Fragment(R.layout.fragment_setting) {

    lateinit var binding: FragmentSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setSupportActionBar(binding.toolbar)

        val navController = view.findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        (activity as MainActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        //switch UiModeManager.MODE_NIGHT_YES


        lifecycleScope.launch(Dispatchers.Main) {
            getSettings().collect{
                println(it)
                if(it.darkMode){
                    binding.darkModeSwitch.isChecked = true
                }

                binding.LanguageSelector.setSelection(when(it.language){
                    "English" -> 0
                    "Spanish" -> 1
                    "Chinese" -> 2
                    else -> 0
                })
            }

        }


        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                lifecycleScope.launch(Dispatchers.Main) {
                    saveTheme(true)
                }
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                lifecycleScope.launch(Dispatchers.Main) {
                    saveTheme(false)
                }
            }
        }

        //change app language when click on the language spinner
        binding.LanguageSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun getSettings() = requireActivity().dataStore.data.map { setting ->
        Setting(
            setting[booleanPreferencesKey("dark_mode")] ?: false,
            setting[stringPreferencesKey("language")].orEmpty()
        )
    }

    private suspend fun saveTheme(darkMode : Boolean){
        requireActivity().dataStore.edit { settings ->
            settings[booleanPreferencesKey("dark_mode")] = darkMode
        }
    }

    private suspend fun saveLanguage(language : String){
        requireActivity().dataStore.edit { settings ->
            settings[stringPreferencesKey("language")] = language
        }
    }


}
