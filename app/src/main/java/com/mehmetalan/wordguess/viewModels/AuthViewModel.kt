package com.mehmetalan.wordguess.viewModels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mehmetalan.wordguess.model.User

class AuthViewModel : ViewModel() {

    val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()
    val userReference = db.getReference("users")

    fun register(
        user: User,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(user.userEmail, user.userPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    firebaseUser?.let {
                        val userId = it.uid
                        userReference.child(userId).setValue(user)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    onSuccess()
                                } else {
                                    dbTask.exception?.let { exception ->
                                        onFailure(exception)
                                    }
                                }
                            }
                    }
                } else {
                    task.exception?.let {
                        onFailure(it)
                    }
                }
            }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    task.exception?.let{
                        onFailure(it)
                    }
                }
            }
    }

    fun signOut(onSignOut: () -> Unit) {
        auth.signOut()
        onSignOut()
    }

}