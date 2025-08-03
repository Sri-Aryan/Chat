package com.example.chat

import Tab.TabbedActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cometchat.chat.core.CometChat
import com.cometchat.chat.exceptions.CometChatException
import com.cometchat.chat.models.User
import com.cometchat.chatuikit.shared.cometchatuikit.CometChatUIKit
import com.cometchat.chatuikit.shared.cometchatuikit.UIKitSettings
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private val appID = "279249b8001d2e57"
    private val region = "IN"
    private val authKey = "1d0398bb2a9a288d3785dee39645c307b050cf02"
    private val uiKitSettings = UIKitSettings.UIKitSettingsBuilder()
        .setRegion(region)
        .setAppId(appID)
        .setAuthKey(authKey)
        .subscribePresenceForAllUsers()
        .build()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        CometChatUIKit.init(this, uiKitSettings, object : CometChat.CallbackListener<String?>() {
            override fun onSuccess(successString: String?) {

                Log.d(TAG, "Initialization completed successfully")

                loginUser()
            }

            override fun onError(e: CometChatException?) {}
        })

    }

    private fun loginUser() {
        CometChatUIKit.login("cometchat-uid-1", object : CometChat.CallbackListener<User>() {
            override fun onSuccess(user: User) {
                val intent = Intent(this@MainActivity, MessageActivity::class.java)
                intent.putExtra("uid", "cometchat-uid-1")
                startActivity(intent)
            }

            override fun onError(e: CometChatException) {
                Log.e("Login", "Login failed: ${e.message}")
            }
        })
    }
}