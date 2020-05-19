package com.example.fragmentz

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.android.synthetic.main.fragment_login.view.logInProgressBar
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {

    private lateinit var logInEmailEditText: TextInputEditText
    private lateinit var logInPasswordEditText: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val loginView = inflater.inflate(R.layout.fragment_login, container, false)

        // Inflate the layout for this fragment
        logInEmailEditText = loginView.emailInputLayout.editText as TextInputEditText
        logInPasswordEditText = loginView.passwordInputLayout.editText as TextInputEditText

        loginView.registrationButton.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragmentsContainer, RegistrationFragment(),"registration")?.
                addToBackStack("registration")?.commit()
        }

        loginView.signInButton.setOnClickListener {
            logInProgressBar.visibility = View.VISIBLE
            signIn()
        }

        return loginView
    }

    private fun signIn() {
        val emailText = logInEmailEditText.text.toString()
        val passwordText = logInPasswordEditText.text.toString()

        if (isEmailValid(logInEmailEditText) && isPasswordValid(logInPasswordEditText)) {
            HttpRequest.logInUser(UserModel(emailText, passwordText),

                object : HttpRequest.RequestCallBacks {
                    override fun onSuccess(successJsonString: String) {
                        logInProgressBar.visibility = View.GONE
                        val jsonObject = JSONObject(successJsonString)
                        if (jsonObject.has("token")) {
                            activity?.supportFragmentManager?.beginTransaction()
                                ?.replace(
                                    R.id.fragmentsContainer, DashboardFragment(
                                        email = emailText
                                    ),
                                    "dashBoard"
                                )?.addToBackStack("dashBoard")
                                ?.commit()
                        }
                    }

                    override fun onFailure(failureMessage: String) {
                        logInProgressBar.visibility = View.GONE
                        Toast.makeText(context, failureMessage, Toast.LENGTH_LONG).show()
                    }

                })
        }
    }


    companion object {
        fun isEmailValid(emailEditText: TextInputEditText): Boolean {
            val emailText = emailEditText.text.toString().trim()
            return if (emailText.isEmpty()) {
                emailEditText.error = "Enter email"
                false
            } else {
                val isValid = Patterns.EMAIL_ADDRESS.matcher(emailText).matches()
                if (!isValid) {
                    emailEditText.error = "Email is not valid"
                }
                return isValid
            }
        }

        fun isPasswordValid(passwordEditText: TextInputEditText) =
            passwordEditText.text.toString().isNotEmpty()
    }

}
