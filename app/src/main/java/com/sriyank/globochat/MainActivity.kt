package com.sriyank.globochat

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.preference.PreferenceManager


//implement interface ->shared preference
class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener{

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get NavHost and NavController
        val navHostFrag = supportFragmentManager.findFragmentById(R.id.nav_host_frag) as NavHostFragment
        navController = navHostFrag.navController

        // Get AppBarConfiguration
        appBarConfiguration = AppBarConfiguration(navController.graph)

        // Link ActionBar with NavController
        setupActionBarWithNavController(navController, appBarConfiguration)

        //Read preference values in a fragment
        //step 1: get reference to the shared preference (XML file)
        val sharePreferences = PreferenceManager.getDefaultSharedPreferences(this)
        //step2: get the 'value' using the 'key'
        val autoReplyTime = sharePreferences.getString("key_auto_reply_time", "") //it will prevent null pointer exception
        Log.i("MainActivity", "Auto Reply Time: $autoReplyTime")

        val publicInfo: Set<String>? = sharePreferences.getStringSet("key_public_info", null)
        Log.i("MainActivity", "Public Info: $publicInfo")
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    //only executed 'after' the preference value has changed
    //on preference changed method, the method is called 'before' the preference value has changed
    //key of the preference object whose value has changed - second param
    //reference to the shared preference - first param
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "key_status") {
            val newStatus = sharedPreferences?.getString(key, "")
            Toast.makeText(this, "New Status: $newStatus", Toast.LENGTH_SHORT).show()
        }

        //check the key first
        if (key == "key_auto_reply") {

            val autoReply = sharedPreferences?.getBoolean(key, false)
            if (autoReply!!) {
                Toast.makeText(this, "Auto Reply: ON", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Auto Reply: OFF", Toast.LENGTH_SHORT).show()
            }
        }

    }

    //register the listener within this activity
    override fun onResume() {
        super.onResume()
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)

    }

    //onPause method we need to unregister the listener as well
    // to avoid any memory leak

    override fun onPause() {
        super.onPause()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)

    }

    //if you want to use this interface within a fragment the steps would be exactly the same
    //but the difference will be the way you pass the context, instead of using 'this', you will
    //use  method 'getContext'


}
