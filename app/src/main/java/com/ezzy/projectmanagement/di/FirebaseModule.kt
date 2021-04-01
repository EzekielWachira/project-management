package com.ezzy.projectmanagement.di

import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth () : FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore()  = Firebase.firestore

    @Provides
    @Singleton
    fun provideAuthUi() : AuthUI = AuthUI.getInstance()

//    @Provides
//    @Singleton
//    fun provideAuthUser(firebaseAuth: FirebaseAuth) : FirebaseUser = firebaseAuth!!.currentUser
}