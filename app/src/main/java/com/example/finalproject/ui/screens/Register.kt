package com.example.finalproject.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.finalproject.MainActivity
import com.example.finalproject.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

/**
 * This is the primary register page
 */
@Composable
fun Register() {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { signInTask ->
                    if (signInTask.isSuccessful) {
                        Toast.makeText(context, "Google sign-in successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, MainActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            putExtra("userID", auth.currentUser?.uid)
                        }
                        context.startActivity(intent)
                    } else {
                        val message = signInTask.exception?.localizedMessage ?: "Unknown Error"
                        Toast.makeText(context,
                            "Google sign-in failed: $message",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        } catch (e: ApiException) {
            Toast.makeText(context,
                "Google sign-in cancelled or failed. ${e.statusCode}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ){
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            performSignUp(email, password, context, keyboardController)
        }) {
            Text("Register")
        }
        Spacer(Modifier.height(24.dp))
        Text("Or continue with")
        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                googleLauncher.launch(googleSignInClient.signInIntent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue with Google")
        }
    }
}

/**
 * Attempts to create a new Firebase Authentication user account
 * On success, navigate to [MainActivity]
 *
 * This function:
 * Calls the FirebaseAuth.createUserWithEmailAndPassword with the given credentials
 * Shows a toast message indicating success or failure
 * Starts [MainActivity] and passes the new User ID with the intents
 *
 * @param email The email entered by the user
 * @param password The password entered by the user
 * @param context The context to show toasts and launch the [MainActivity]
 * @param keyboardController Hides the software keyboard
 */
private fun performSignUp(
    email: String,
    password: String,
    context: Context,
    keyboardController: SoftwareKeyboardController?
) {
    val auth = FirebaseAuth.getInstance()
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if(task.isSuccessful) {
                Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("userID", auth.currentUser?.uid)
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Registration Failed", Toast.LENGTH_LONG).show()
            }
            keyboardController?.hide()
        }
}