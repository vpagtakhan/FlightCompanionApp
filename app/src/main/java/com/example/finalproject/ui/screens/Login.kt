package com.example.finalproject.ui.screens

import android.app.Activity
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
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider


/**
 * This is the primary login page
 *
 *
 */
@Composable
fun Login() {
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
                        Toast.makeText(context, "Google sign-in failed.", Toast.LENGTH_LONG).show()
                    }
                }
        } catch (e: ApiException) {
            Toast.makeText(context, "Google sign-in cancelled or failed.", Toast.LENGTH_LONG).show()
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
            performSignIn(email, password, context, keyboardController)
        }) {
            Text("Login")
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

        Spacer(Modifier.height(8.dp))

//        val activity = context as? Activity
//        Button(
//            onClick = {
//                if(activity != null) {
//                    performSignInWithGitHub(activity)
//                } else {
//                    Toast.makeText(context, "Github sign-in not available", Toast.LENGTH_SHORT)
//                }
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Continue With Github")
//        }
    }
}

/**
 * Attempts to sign in the user with Firebase Authentication
 * On success, navigates user to [MainActivity]
 *
 * @param email The email that the user inputs
 * @param password The password that the user inputs
 * @param context Context used to show toasts and launch [MainActivity]
 * @param keyboardController Hides the software keyboard
 */
private fun performSignIn(
    email: String,
    password: String,
    context: Context,
    keyboardController: SoftwareKeyboardController?
) {
    val auth = FirebaseAuth.getInstance()
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
        if (task.isSuccessful) {
            Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("userID", auth.currentUser?.uid)
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Login Failed", Toast.LENGTH_LONG).show()
        }
            keyboardController?.hide()
        }
}

//private fun performSignInWithGitHub(activity: Activity) {
//    val auth = FirebaseAuth.getInstance()
//    val providerBuilder = OAuthProvider.newBuilder("github.com")
//
//    val pendingResultTask = auth.pendingAuthResult
//    if (pendingResultTask != null) {
//        pendingResultTask
//            .addOnSuccessListener { result ->
//                Toast.makeText(
//                    activity,
//                    "Github sign-in successful: ${result.user?.email ?: "No Email"}",
//                    Toast.LENGTH_SHORT
//                ).show()
//
//                val intent = Intent(activity, MainActivity::class.java).apply {
//                    putExtra("userID", result.user?.uid)
//                }
//                activity.startActivity(intent)
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(
//                    activity,
//                    "GitHub sign-in failed: ${e.localizedMessage}",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//    } else {
//        // Start a new sign-in flow
//        auth
//            .startActivityForSignInWithProvider(activity, providerBuilder.build())
//            .addOnSuccessListener { result ->
//                Toast.makeText(
//                    activity,
//                    "GitHub sign-in successful: ${result.user?.email ?: "No email"}",
//                    Toast.LENGTH_SHORT
//                ).show()
//
//                val intent = Intent(activity, MainActivity::class.java).apply {
//                    putExtra("userID", result.user?.uid)
//                }
//                activity.startActivity(intent)
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(
//                    activity,
//                    "GitHub sign-in failed: ${e.localizedMessage}",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//    }
//}
