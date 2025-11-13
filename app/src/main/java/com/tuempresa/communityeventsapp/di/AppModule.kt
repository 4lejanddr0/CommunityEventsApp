package com.tuempresa.communityeventsapp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tuempresa.communityeventsapp.data.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        db: FirebaseFirestore
    ): AuthRepository = AuthRepository(auth, db)
}
