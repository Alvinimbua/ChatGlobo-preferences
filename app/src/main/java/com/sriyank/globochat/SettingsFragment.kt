package com.sriyank.globochat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.*
import kotlinx.coroutines.internal.artificialFrame


class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(
            R.xml.settings,
            rootKey
        ) //key of our root element which is preference screen present in settings xml - rootkey

        val dataStore = DataStore()
        //enable preferenceDataStore for entire hierarchy and disables the sharedPreferences
        //preferenceManager.preferenceDataStore = dataStore
        val accSettingsPref =
            findPreference<Preference>(getString(R.string.key_account_settings))//will return preference object

        accSettingsPref?.setOnPreferenceClickListener {

            //code in general to navigate to any fragment incase you are using nav comp library
            //only need to take care of the direction class which should be same as per your fragment name
            //and also take care of action method which should be as per id of the action in the nav graph
            //and the code is only possible whe  using nav component
            val navHostFragment =
                activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_frag) as NavHostFragment
            val navController = navHostFragment.navController
            //SettingsFragmentDirections is auto generated class created which is created by nav comp library
            val action = SettingsFragmentDirections.actionSettingsToAccSettings()
            navController.navigate(action)

            true
        }

        //Read preference values in a fragment
        //step 1: get reference to the shared preference (XML file)
        val sharePreferences = PreferenceManager.getDefaultSharedPreferences(context)
        //step2: get the 'value' using the 'key'
        val autoReplyTime = sharePreferences.getString(
            "key_auto_reply_time",
            ""
        ) //it will prevent null pointer exception
        Log.i("SettingsFragment", "Auto Reply Time: $autoReplyTime")

        val autoDownload = sharePreferences.getBoolean("key-auto_download", false)
        Log.i("SettingsFragment", "Auto Download: $autoDownload")

        //onPreferenceChangeListener can only be used at one preference at at a time
        //and only in the same fragment on which it is present i.e it cannot be used in other parts of application
        val statusPref = findPreference<EditTextPreference>("key_status")
        //first param is preference object itself which is same as the instance i.e statusPref
        //second param is the new status value which you have updated in the preference
        statusPref?.setOnPreferenceChangeListener { preference, newValue ->
            Log.i("SettingsFragment", "New Status: $newValue")

            val newStatus = newValue as String
            if (newStatus.contains("bad")) {
                Toast.makeText(
                    context,
                    "Inappropriate Status. Please maintain community guidelines.",
                    Toast.LENGTH_SHORT
                ).show()

                false  //false: reject the new value
            } else {
                true //true: accept the new status ,

            }

        }

        //find the preference object
        val notificationPref =
            findPreference<SwitchPreferenceCompat>(getString(R.string.key_new_msg_notif))
        notificationPref?.summaryProvider =
            Preference.SummaryProvider<SwitchPreferenceCompat> { switchPref ->

                if (switchPref?.isChecked!!)
                    "Status: ON"
                else
                    "Status: OFF"

                //summary provider is used for all kind of preference objects
                //using summary provider you can customise your preference summary


            }

        notificationPref?.preferenceDataStore = dataStore

        //execute boolean method as per app requirement
        val isNotifEnabled = dataStore.getBoolean("key_new_msg_notif", false)
    }

    class DataStore : PreferenceDataStore(){
        //override method only as per you need
        //DO NOT override methods which you don't need to use
        //After overriding remove the super call. (could throw unsupportedOperationException)

        override fun getBoolean(key: String?, defValue: Boolean): Boolean {

            if (key == "key_new_msg_notif") {
                //retrieve value from cloud or local db
                Log.i("Data store", "getBoolean executed for $key")
            }

            return defValue
        }

        override fun putBoolean(key: String?, value: Boolean) {

            if (key=="key_new_msg_notif") {
                //save to cloud or local db
                Log.i("SettingsFragment", "putBoolean executed for $key with new value: $value" )
            }
        }

    }


}

//onPreferenceChangeListener method is executed before the Preference value
//has changed in the sharedPreference file

//onSharedPreferenceChangeListener can be used to track multiple Preference values at a time
//and can be used in any part of the application -> as long as the component is in active state