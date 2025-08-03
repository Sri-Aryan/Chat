package files

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.cometchat.chat.models.BaseMessage
import com.cometchat.chat.models.Group
import com.cometchat.chat.models.User
import com.cometchat.chatuikit.shared.constants.UIKitConstants.DialogState
import com.cometchat.chatuikit.shared.framework.ChatConfigurator
import com.cometchat.chatuikit.shared.interfaces.Function3
import com.cometchat.chatuikit.shared.models.AdditionParameter
import com.cometchat.chatuikit.shared.models.CometChatMessageTemplate
import com.cometchat.chatuikit.shared.resources.utils.Utils
import com.cometchat.chatuikit.shared.resources.utils.keyboard_utils.KeyBoardUtils
import com.example.chat.GroupDetailActivity
import com.example.chat.R
import com.example.chat.ThreadActivity
import com.example.chat.UserDetailsActivity
import com.example.chat.databinding.ActivityMessagesBinding
import com.example.chat.databinding.OverflowMenuLayoutBinding
import com.google.gson.Gson
import kotlin.jvm.java

class MessagesActivity : AppCompatActivity() {
    private var user: User? = null
    private var group: Group? = null
    private var baseMessage: BaseMessage? = null
    private lateinit var viewModel: MessagesViewModel
    private lateinit var binding: ActivityMessagesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessagesBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)
        adjustWindowSettings()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.parent_view)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Create an instance of the MessagesViewModel
        viewModel = ViewModelProvider
            .NewInstanceFactory()
            .create(
                MessagesViewModel::class.java
            )

        // Deserialize the user and group data from the Intent
        user = Gson().fromJson(
            intent.getStringExtra(getString(R.string.app_user)), User::class.java
        )

        group = Gson().fromJson(
            intent.getStringExtra(getString(R.string.app_group)), Group::class.java
        )

        MyApplication.currentOpenChatId = if (group != null) group!!.guid else user?.uid

        // Set the user and group in the ViewModel
        viewModel.setUser(user)
        viewModel.setGroup(group)

        // Add listeners for ViewModel updates
        viewModel.addListener()

        viewModel.updatedGroup.observe(
            this
        ) { group: Group ->
            this.updateGroupJoinedStatus(
                group
            )
        }

        viewModel.baseMessage.observe(
            this
        ) { baseMessage: BaseMessage? ->
            if (baseMessage != null) {
                this.setBaseMessage(baseMessage)
            }
        }

        viewModel.updateUser.observe(
            this
        ) { user: User ->
            this.user = user
            this.updateUserBlockStatus(user)
        }

        viewModel
            .openUserChat()
            .observe(
                this
            ) { user: User? -> this.openUserChat(user) }

        viewModel.isExitActivity.observe(
            this
        ) { exit: Boolean -> this.exitActivity(exit) }

        viewModel.unblockButtonState.observe(
            this
        ) { dialogState: DialogState -> this.setUnblockButtonState(dialogState) }

        // Initialize UI components
        addViews()
        setOverFlowMenu()

        // Set click listener for the unblock button
        binding.unblockBtn.setOnClickListener { view: View? -> viewModel.unblockUser() }

        binding.messageList.mentionsFormatter.setOnMentionClick { context: Context, user: User? ->
            val intent = Intent(
                context, MessagesActivity::class.java
            )
            intent.putExtra(context.getString(R.string.app_user), Gson().toJson(user))
            context.startActivity(intent)
        }

        binding.messageList.setOnThreadRepliesClick { context: Context, baseMessage: BaseMessage, cometchatMessageTemplate: CometChatMessageTemplate? ->
            val intent = Intent(
                context, ThreadActivity::class.java
            )
            if (user != null)
                intent.putExtra("user", Gson().toJson(user))
            intent.putExtra(getString(R.string.app_message_id), baseMessage.id)
            context.startActivity(intent)
        }
    }


    private fun adjustWindowSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true)
        } else {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }

     private fun setUnblockButtonState(dialogState: DialogState) {
        if (dialogState == DialogState.INITIATED) {
            binding.unblockText.visibility = View.GONE
            binding.progress.visibility = View.VISIBLE
        } else if (dialogState == DialogState.SUCCESS || dialogState == DialogState.FAILURE) {
            binding.unblockText.visibility = View.VISIBLE
            binding.progress.visibility = View.GONE
        }
    }

    private fun updateGroupJoinedStatus(group: Group) {
        if (!group.isJoined) {
            binding.unblockBtn.visibility = View.GONE
            binding.messageComposer.visibility = View.GONE
            binding.infoLayout.visibility = View.VISIBLE
        } else {
            binding.unblockBtn.visibility = View.GONE
            binding.messageComposer.visibility = View.VISIBLE
            binding.infoLayout.visibility = View.GONE
        }
    }

    private fun setBaseMessage(baseMessage: BaseMessage) {
        this.baseMessage = baseMessage
    }

   private fun updateUserBlockStatus(user: User) {
        if (user.isBlockedByMe) {
            binding.messageComposer.visibility = View.GONE
            binding.unblockLayout.visibility = View.VISIBLE
        } else {
            binding.messageComposer.visibility = View.VISIBLE
            binding.unblockLayout.visibility = View.GONE
        }
    }

    private fun openUserChat(user: User?) {
        if (user != null) {
            val intent = Intent(this, MessagesActivity::class.java)
            intent.putExtra(getString(R.string.app_user), Gson().toJson(user))
            startActivity(intent)
        }
    }


    private fun exitActivity(exit: Boolean) {
        if (exit) {
            finish()
        }
    }

    private fun setOverFlowMenu() {
        binding.messageHeader.auxiliaryButtonView = Function3 { context: Context?, user: User?, group: Group? ->
            val linearLayout = LinearLayout(context)
            val view = ChatConfigurator
                .getDataSource()
                .getAuxiliaryHeaderMenu(
                    context, user, group, AdditionParameter()
                )

            val overflowMenuLayoutBinding = OverflowMenuLayoutBinding.inflate(layoutInflater)
            overflowMenuLayoutBinding.ivMenu.setImageResource(R.drawable.ic_info)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.gravity = Gravity.CENTER_VERTICAL

            if ((group != null && group.isJoined) || (user != null && !Utils.isBlocked(user))) {
                if (view != null) {
                    linearLayout.addView(view)
                }
                linearLayout.addView(overflowMenuLayoutBinding.root)
            }

            overflowMenuLayoutBinding.ivMenu.setOnClickListener { view1: View? ->
                openDetailScreen(
                    group
                )
            }
            linearLayout
        }
    }

    private fun openDetailScreen(group: Group?) {
        var intent: Intent? = null
        if (user != null) {
            intent = Intent(this, UserDetailsActivity::class.java)
            intent.putExtra(getString(R.string.app_user), Gson().toJson(user))
            intent.putExtra(getString(R.string.app_base_message), Gson().toJson(binding.messageList.viewModel.lastMessage))
        } else if (group != null) {
            intent = Intent(this, GroupDetailActivity::class.java)
            intent.putExtra(getString(R.string.app_group), Gson().toJson(group))
        }
        startActivity(intent)
    }

    private fun addViews() {
        KeyBoardUtils.setKeyboardVisibilityListener(
            this, binding.root
        ) { keyboardVisible: Boolean ->
            if (binding.messageComposer.messageInput.composeBox.isFocused && keyboardVisible) {
                if (binding.messageList.atBottom()) {
                    binding.messageList.scrollToBottom()
                }
            }
        }

        // Set user or group data to the message header and composer
        if (user != null) {
            binding.messageHeader.user = user!!
            binding.messageList.user = user
            binding.messageComposer.user = user
            updateUserBlockStatus(user!!)
        } else if (group != null) {
            binding.messageHeader.group = group!!
            binding.messageList.group = group
            binding.messageComposer.group = group
            updateGroupJoinedStatus(group!!)
        }

        // Set up back button behavior
        binding.messageHeader.setOnBackButtonPressed {
            Utils.hideKeyBoard(
                this, binding.root
            )
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy() // Remove listener from the ViewModel to prevent memory leaks
        viewModel.removeListener()
        MyApplication.currentOpenChatId = null
    }
}
