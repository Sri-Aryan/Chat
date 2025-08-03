package files

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cometchat.calls.BuildConfig
import com.cometchat.chat.core.CometChat
import com.cometchat.chat.exceptions.CometChatException
import com.cometchat.chatuikit.shared.cometchatuikit.CometChatUIKit
import com.cometchat.chatuikit.shared.cometchatuikit.UIKitSettings
import com.example.chat.AppCredentials
import com.example.chat.R
import org.json.JSONObject

class SplashViewModel : ViewModel() {
    private val loginStatus: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    fun initUIKit(context: Context) {
        initKit(context, null)
    }

    fun initUIKit(
        context: Context, callbackListener: CometChat.CallbackListener<String>?
    ) {
        initKit(context, callbackListener)
    }

    private fun initKit(
        context: Context, callbackListener: CometChat.CallbackListener<String>?
    ) {

        val appId = AppUtils.getDataFromSharedPref(context, String::class.java, R.string.app_cred_id, AppCredentials.APP_ID)
        val region = AppUtils.getDataFromSharedPref(context, String::class.java, R.string.app_cred_region, AppCredentials.REGION)
        val authKey = AppUtils.getDataFromSharedPref(context, String::class.java, R.string.app_cred_auth, AppCredentials.AUTH_KEY)

        val uiKitSettings: UIKitSettings = UIKitSettings
            .UIKitSettingsBuilder()
            .setAutoEstablishSocketConnection(true)
            .setAppId(appId)
            .setRegion(region)
            .setAuthKey(authKey)
            .subscribePresenceForAllUsers()
            .build()

        CometChatUIKit.init(context, uiKitSettings, object : CometChat.CallbackListener<String>() {
            override fun onSuccess(s: String) {
                CometChat.setDemoMetaInfo(getAppMetadata(context))
                checkUserIsNotLoggedIn()
                callbackListener?.onSuccess(s)
            }

            override fun onError(e: CometChatException) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                callbackListener?.onError(e)
            }
        })
    }

    private fun getAppMetadata(context: Context): JSONObject {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("name", context.resources.getString(R.string.app_name))
            jsonObject.put("type", "sample")
            jsonObject.put("version", BuildConfig.VERSION_NAME)
            jsonObject.put("platform", "android")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jsonObject
    }

    fun checkUserIsNotLoggedIn() {
        if (CometChatUIKit.getLoggedInUser() != null) {
            loginStatus.setValue(true) // User is logged in
        } else {
            loginStatus.setValue(false) // User is not logged in
        }
    }

    fun getLoginStatus(): LiveData<Boolean> {
        return loginStatus
    }
}
