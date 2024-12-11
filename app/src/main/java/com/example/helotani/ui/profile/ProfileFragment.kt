package com.example.helotani.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.helotani.databinding.FragmentProfileBinding
import com.example.helotani.data.pref.UserPref

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var userPref: UserPref

    override fun onResume() {
        super.onResume()
        // Hide the ActionBar when this fragment is visible
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }

    override fun onPause() {
        super.onPause()
        // Show the ActionBar again when leaving this fragment
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        userPref = UserPref(requireContext())

        setupUserProfile()
        return binding.root
    }

    private fun setupUserProfile() {
        val userName = userPref.getUserName() ?: "Nama tidak tersedia"
        val email = userPref.getUserEmail() ?: "Email tidak tersedia"

        binding.tvUserName.text = userName
        binding.tvUserEmail.text = email
        binding.tvFullName.text = userName
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

