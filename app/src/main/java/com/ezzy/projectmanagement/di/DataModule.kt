package com.ezzy.projectmanagement.di

import com.ezzy.core.data.ActivityRepository
import com.ezzy.core.data.OrganizationRepository
import com.ezzy.core.data.ProjectRepository
import com.ezzy.core.data.UserRepository
import com.ezzy.core.interactors.*
import com.ezzy.projectmanagement.data.remote.ActivityDataSourceImpl
import com.ezzy.projectmanagement.data.remote.OrganizationDataSourceImpl
import com.ezzy.projectmanagement.data.remote.ProjectDataSourceImpl
import com.ezzy.projectmanagement.data.remote.UserDataSourceImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideOrganizationRepository(
        fireStore: FirebaseFirestore,
        storage: FirebaseStorage,
        saveUserOrganizations: SaveUserOrganizations,
        firebaseAuth: FirebaseAuth
    ) = OrganizationRepository(OrganizationDataSourceImpl(
        fireStore, storage, saveUserOrganizations, firebaseAuth
    ))

    @Provides
    @Singleton
    fun provideProjectRepository(
        fireStore: FirebaseFirestore,
        saveUserProjects: SaveUserProjects,
        firebaseAuth: FirebaseAuth
    ) = ProjectRepository(
        ProjectDataSourceImpl(fireStore, saveUserProjects, firebaseAuth)
    )

    @Provides
    @Singleton
    fun provideUserRepository(
        fireStore: FirebaseFirestore, 
        storage: FirebaseStorage,
        firebaseAuth: FirebaseAuth
    ) = UserRepository(UserDataSourceImpl(fireStore, storage, firebaseAuth))

    @Singleton
    @Provides
    fun provideActivityRepository(
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore
    ) = ActivityRepository(ActivityDataSourceImpl(firebaseAuth, fireStore))

    @Provides
    fun provideAddOrganization(repository: OrganizationRepository) =
        AddOrganization(repository)

    @Provides
    fun provideRetrievedOrganizations(repository: OrganizationRepository) =
        RetrieveOrganizations(repository)

    @Provides
    fun provideSearchedOrganizations(repository: OrganizationRepository) =
        SearchOrganizations(repository)

    @Provides
    fun provideAddedOrgs(repository: OrganizationRepository) =
        AddOrgs(repository)

    @Provides
    fun provideAddedMembers(repository: OrganizationRepository) =
        AddMembers(repository)

    @Provides
    fun provideOrganizationMembers(repository: OrganizationRepository) =
        RetrieveOrganizationMembers(repository)

    @Provides
    fun provideOrganizationProjects(repository: OrganizationRepository) =
        RetrieveOrganizationProjects(repository)

    @Provides
    fun provideOrgId(repository: OrganizationRepository) = GetOrgId(repository)

    @Provides
    fun provideUserOrganizations(repository: OrganizationRepository)
        = GetUserOrganizations(repository)

    @Provides
    fun provideOrganizationsIds(repository: OrganizationRepository) =
        GetOrganizationsIds(repository)

    //Project Use case dependencies
    @Provides
    fun provideAddProject(repository: ProjectRepository) =
        AddProject(repository)

    @Provides
    fun provideGetAllProjects(repository: ProjectRepository) =
        GetAll(repository)

    @Provides
    fun attachedOrganizations(repository: ProjectRepository) =
        AttachOrganization(repository)

    @Provides
    fun provideAttachMembers(repository: ProjectRepository) =
        AttachMembers(repository)

    @Provides
    fun provideGetUserProjects(repository: ProjectRepository) =
        GetUserProjects(repository)


    //User use case
    @Provides
    fun provideAllUsers(repository: UserRepository) =
        GetAllUser(repository)

    @Provides
    fun provideSearchedMembers(repository: UserRepository) =
        SearchMembers(repository)

    @Provides
    fun provideAddMember(repository: UserRepository) =
        AddMember(repository)

    @Provides
    fun provideSaveUserOrganizations(repository: UserRepository) =
        SaveUserOrganizations(repository)

    @Provides
    fun provideSaveUserProjects(repository: UserRepository) =
        SaveUserProjects(repository)

    @Provides
    fun provideUpdateUser(repository: UserRepository) =
        UpdateUser(repository)

    @Provides
    fun provideSaveUserImage(repository: UserRepository) =
        SaveUserImage(repository)

    @Provides
    fun provideGetUserDetails(repository: UserRepository) =
        GetUserDetails(repository)

    //activity usecase
    @Provides
    fun provideAddActivity(repository: ActivityRepository) =
        ActivityUseCase(repository)

    @Provides
    fun provideGetActivities(repository: ActivityRepository) =
        GetActivities(repository)
}