package com.example.fragmentz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_registration.view.*
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 */
class RegistrationFragment : Fragment() {
    private lateinit var layoutView: View

    private lateinit var regEmailEditText: TextInputEditText
    private lateinit var regPasswordEditText: TextInputEditText
    private lateinit var regPasswordRepeatEditText: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        layoutView = inflater.inflate(R.layout.fragment_registration, container, false)
        initViews()
        return layoutView
    }

    private fun initViews() {
        regEmailEditText = layoutView.regEmailInputLayout.editText as TextInputEditText
        regPasswordEditText = layoutView.regPasswordInputLayout.editText as TextInputEditText
        regPasswordRepeatEditText = layoutView.regRepeatPasswordInputLayout.editText as TextInputEditText

        layoutView.regRegistrationButton.setOnClickListener {
            layoutView.progressBar.visibility = View.VISIBLE
            registerUser()
        }
    }

    private fun registerUser() {
        if (LoginFragment.isEmailValid(regEmailEditText) &&
            LoginFragment.isPasswordValid(regPasswordEditText) &&
            passwordsAreSame()
        ) {

            HttpRequest.registerUser(
                UserModel(regEmailEditText.text.toString(), regPasswordEditText.text.toString()),

                object : HttpRequest.RequestCallBacks {
                    override fun onSuccess(successJsonString: String) {
                        val jsonObject = JSONObject(successJsonString)
                        if (jsonObject.has("id") && jsonObject.has("token")) {
                            activity?.supportFragmentManager?.beginTransaction()
                                ?.replace(
                                    R.id.fragmentsContainer, DashboardFragment(
                                        userId  = jsonObject.getInt("id")),
                                    "dashBoard"
                                )?.addToBackStack("dashBoard")?.commit()
                        }

                        layoutView.progressBar.visibility = View.GONE
                    }

                    override fun onFailure(failureMessage: String) {
                        layoutView.errorMessageTextView.text = failureMessage
                        layoutView.progressBar.visibility = View.GONE
                    }

                })
        }
    }


    private fun passwordsAreSame(): Boolean {
        val areSame =
            regPasswordEditText.text.toString().trim() == regPasswordRepeatEditText.text.toString().trim()
        if(!areSame){
            regPasswordEditText.error = "Passwords doesn't match"
            regPasswordRepeatEditText.error = "Passwords doesn't match"
        }
        return areSame
    }
}
