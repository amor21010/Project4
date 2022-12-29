package com.udacity.project4.authentication

import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

class AuthenticationRepository {
    private val auth = FirebaseAuth.getInstance()

    fun getFirebaseAuthState() : AuthenticationLiveData {
        return AuthenticationLiveData(auth)
    }

    fun signOut() {
        AuthUI.getInstance().signOut(auth.app.applicationContext).addOnCompleteListener { auth.signOut() }
    }
}