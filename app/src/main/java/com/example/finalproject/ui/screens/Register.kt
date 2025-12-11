package com.example.finalproject.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import com.google.firebase.auth.FirebaseAuth

/**
 * This is the primary register page
 */
@Composable
fun Register() {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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