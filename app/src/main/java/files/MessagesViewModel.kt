package files

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cometchat.chat.core.CometChat
import com.cometchat.chat.core.CometChat.GroupListener
import com.cometchat.chat.exceptions.CometChatException
import com.cometchat.chat.models.Action
import com.cometchat.chat.models.BaseMessage
import com.cometchat.chat.models.Conversation
import com.cometchat.chat.models.Group
import com.cometchat.chat.models.User
import com.cometchat.chatuikit.shared.cometchatuikit.CometChatUIKit
import com.cometchat.chatuikit.shared.constants.UIKitConstants.DialogState
import com.cometchat.chatuikit.shared.events.CometChatConversationEvents
import com.cometchat.chatuikit.shared.events.CometChatGroupEvents
import com.cometchat.chatuikit.shared.events.CometChatUIEvents
import com.cometchat.chatuikit.shared.events.CometChatUserEvents

class MessagesViewModel : ViewModel() {
    private val LISTENER_ID = System.currentTimeMillis().toString() + javaClass.simpleName
    private var mUser: User? = null
    private var mGroup: Group? = null
    val baseMessage: MutableLiveData<BaseMessage?> = MutableLiveData()

    val updatedGroup: MutableLiveData<Group> = MutableLiveData()


    val updateUser: MutableLiveData<User> = MutableLiveData()
    private val openUserChat = MutableLiveData<User>()

    val isExitActivity: MutableLiveData<Boolean> = MutableLiveData()

    val unblockButtonState: MutableLiveData<DialogState> = MutableLiveData()


    fun openUserChat(): MutableLiveData<User> {
        return openUserChat
    }

    fun setGroup(group: Group?) {
        mGroup = group
    }


    fun setUser(user: User?) {
        mUser = user
    }

    fun addListener() {
        CometChat.addGroupListener(LISTENER_ID, object : GroupListener() {
            override fun onGroupMemberLeft(
                action: Action, user: User, group: Group
            ) {
                updateGroupJoinedStatus(group, user, false)
            }

            override fun onGroupMemberKicked(
                action: Action, user: User, user1: User, group: Group
            ) {
                updateGroupJoinedStatus(group, user, false)
            }

            override fun onGroupMemberBanned(
                action: Action, user: User, user1: User, group: Group
            ) {
                updateGroupJoinedStatus(group, user, false)
            }

            override fun onMemberAddedToGroup(
                action: Action, addedBy: User, userAdded: User, addedTo: Group
            ) {
                updateGroupJoinedStatus(addedTo, userAdded, true)
            }

            override fun onGroupMemberJoined(
                action: Action, user: User, group: Group
            ) {
                updateGroupJoinedStatus(group, user, true)
            }
        })

        CometChatGroupEvents.addGroupListener(LISTENER_ID, object : CometChatGroupEvents() {
            override fun ccGroupDeleted(group: Group) {
                isExitActivity.value = true
            }

            override fun ccGroupLeft(
                actionMessage: Action, leftUser: User, leftGroup: Group
            ) {
                isExitActivity.value = true
            }
        })

        CometChatUserEvents.addUserListener(LISTENER_ID, object : CometChatUserEvents() {
            override fun ccUserBlocked(user: User) {
                updateUser.value = user
            }

            override fun ccUserUnblocked(user: User) {
                updateUser.value = user
            }
        })

        CometChatUIEvents.addListener(LISTENER_ID, object : CometChatUIEvents() {
            override fun ccActiveChatChanged(
                id: HashMap<String, String>, message: BaseMessage?, user: User?, group: Group?
            ) {
                baseMessage.value = message
            }

            override fun ccOpenChat(
                user: User?, group: Group?
            ) {
                openUserChat.value = user
            }
        })

        CometChatConversationEvents.addListener(LISTENER_ID, object : CometChatConversationEvents() {
            override fun ccConversationDeleted(conversation: Conversation) {
                isExitActivity.value = true
            }
        })
    }

    fun removeListener() {
        CometChat.removeGroupListener(LISTENER_ID)
        CometChatGroupEvents.removeListener(LISTENER_ID)
        CometChatUserEvents.removeListener(LISTENER_ID)
        CometChatUIEvents.removeListener(LISTENER_ID)
    }

    private fun updateGroupJoinedStatus(
        group: Group?, user: User, isJoined: Boolean
    ) {
        if (group != null && mGroup != null && group.guid == mGroup!!.guid) { // Add null check for group
            if (user.uid == CometChatUIKit.getLoggedInUser().uid) {
                group.setHasJoined(isJoined)
                updatedGroup.value = group
            }
        }
    }
    fun unblockUser() {
        if (mUser == null) return
        unblockButtonState.value = DialogState.INITIATED
        Repository.unblockUser(mUser!!, object : CometChat.CallbackListener<HashMap<String, String>>() {
            override fun onSuccess(resultMap: HashMap<String, String>) {
                if (AppConstants.SuccessConstants.SUCCESS.equals(
                        resultMap[mUser!!.uid], ignoreCase = true
                    )
                ) {
                    unblockButtonState.setValue(DialogState.SUCCESS)
                } else {
                    unblockButtonState.setValue(DialogState.FAILURE)
                }
            }

            override fun onError(e: CometChatException) {
                unblockButtonState.value = DialogState.FAILURE
            }
        })
    }
}
