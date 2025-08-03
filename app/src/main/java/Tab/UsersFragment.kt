package Tab

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.chat.R
import com.example.chat.databinding.FragmentUsersBinding
import com.google.gson.Gson
import files.MessagesActivity

class UsersFragment : Fragment() {

    private lateinit var binding: FragmentUsersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.users.setOnItemClick { view, position, user ->
            val intent = Intent(requireActivity(), MessagesActivity::class.java)
            intent.putExtra(getString(R.string.app_user), Gson().toJson(user))
            startActivity(intent)
        }
    }
}
