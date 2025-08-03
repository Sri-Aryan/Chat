package com.example.chat

import CallLogsFragment
import ChatsFragment
import GroupsFragment
import Tab.UsersFragment
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.cometchat.chatuikit.CometChatTheme
import com.example.chat.databinding.ActivityHomeBinding
import files.NewChatActivity

class HomeActivity : AppCompatActivity(), AdapterView.OnItemClickListener {
        private lateinit var binding: ActivityHomeBinding
        private var currentFragment = R.id.nav_chats // Default to the Chats fragment

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState != null) {
            currentFragment = savedInstanceState.getInt(SELECTED_FRAGMENT_KEY, R.id.nav_chats)
        } // Set the selected item in the bottom navigation to match the current fragment
        binding.bottomNavigationView.selectedItemId = currentFragment
        loadFragment(getFragment(currentFragment))
        configureBottomNavigation()
    }

  private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }
  private fun configureBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener { item: MenuItem ->
            if (currentFragment == item.itemId) {
                return@setOnItemSelectedListener true // No action needed if the fragment is already selected
            }
            currentFragment = item.itemId
            loadFragment(
                getFragment(
                    currentFragment
                )
            )
            true
        } // Create a ColorStateList for icon and text color based on the checked state
        val colorStateList = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()), intArrayOf(
                CometChatTheme.getIconTintHighlight(this), CometChatTheme.getIconTintSecondary(
                    this
                )
            )
        )

        binding.bottomNavigationView.itemIconTintList = colorStateList
        binding.bottomNavigationView.itemTextColor = colorStateList
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SELECTED_FRAGMENT_KEY, currentFragment) // Save the selected fragment ID
    }

     fun onItemClick() {
        val intent = Intent(this, NewChatActivity::class.java)
        startActivity(intent)
    }

    override fun onItemClick(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ) {
        TODO("Not yet implemented")
    }

    companion object {
        private val TAG: String = HomeActivity::class.java.simpleName
        private const val SELECTED_FRAGMENT_KEY = "selected_fragment"

        private fun getFragment(itemId: Int): Fragment {
            val selectedFragment = when (itemId) {
                R.id.nav_chats -> {
                    ChatsFragment()
                }

                R.id.nav_calls -> {
                    CallLogsFragment()
                }

                R.id.nav_users -> {
                    UsersFragment()
                }

                R.id.nav_groups -> {
                    GroupsFragment()
                }

                else -> {
                    ChatsFragment()
                }
            }
            return selectedFragment
        }
    }

}