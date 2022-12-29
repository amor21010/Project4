package com.udacity.project4.authentication

import androidx.lifecycle.LiveData

class AuthenticationViewModel {
    private val repository = AuthenticationRepository()

    fun getAuthenticationState() : LiveData<Boolean> {
        return repository.getFirebaseAuthState()
    }

    fun logout() {
        repository.signOut()
    }
}