package com.example.androidchateeinclass

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.androidchateeinclass.databinding.ActivityAuthBinding
import com.google.firebase.auth.FirebaseAuth

class AuthActivity: AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityAuthBinding
    private lateinit var authMode: AuthMode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding = ActivityAuthBinding.inflate(layoutInflater)
         setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        authMode = intent?.extras?.getParcelable(AuthModeExtraName)!!

        when(authMode){
            is AuthMode.Login ->{ binding.authButton.text = getString(R.string.login) }
            is AuthMode.Register ->{ binding.authButton.text = getString(R.string.register) }
        }

        binding.authButton.setOnClickListener {
            when(authMode){
                is AuthMode.Login ->{
                    auth.signInWithEmailAndPassword(binding.emailInput.text.toString(), binding.passwordInput.text.toString() )
                        .addOnCompleteListener(this) { task ->
                            if(task.isSuccessful) {
                                val user = auth.currentUser
                               startActivity(Intent(Intent(this, ChatActivity::class.java)))
                            }
                            //else there is an error and we can add error handling
                        }
                }
                is AuthMode.Register ->{
                    auth.createUserWithEmailAndPassword(binding.emailInput.text.toString(), binding.passwordInput.text.toString() )
                        .addOnCompleteListener(this) { task ->
                            if(task.isSuccessful) {
                                val user = auth.currentUser
                                startActivity(Intent(Intent(this, ChatActivity::class.java)))
                            }
                            //else there is an error and we can add error handling
                        }
                }
            }
        }

//        binding.loginButton.setOnClickListener {
//            hideKeyboard()
//        }

    }

//    fun Activity.hideKeyboard() {
//        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0);
//    }

    companion object {
        const val AuthModeExtraName = "AUTH_MODE"
        fun newIntent(authMode: AuthMode, context: Context): Intent {
            val intent = Intent(context, AuthActivity::class.java)
            intent.putExtra(AuthModeExtraName, authMode)
            return intent
        }
    }

}

