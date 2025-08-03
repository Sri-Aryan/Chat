package com.example.chat

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cometchat.chat.models.User
import com.cometchat.chatuikit.shared.interfaces.OnItemClick
import com.cometchat.chatuikit.shared.interfaces.OnLoad
import com.cometchat.chatuikit.users.CometChatUsers

class UsersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_users)
        val cometchatUsers =findViewById<CometChatUsers>(R.id.users)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        cometchatUsers.onItemClick = OnItemClick { view, position, user ->

        }

        cometchatUsers.setOnLoad(object : OnLoad<User?> {
            override fun onLoad(list: MutableList<User?>?) {

            }
        })
    }
}
