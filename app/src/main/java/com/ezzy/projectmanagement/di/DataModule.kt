package com.ezzy.projectmanagement.di

import com.ezzy.core.data.OrganizationRepository
import com.ezzy.core.data.ProjectRepository
import com.ezzy.core.data.UserRepository
import com.ezzy.core.interactors.*
import com.ezzy.projectmanagement.data.remote.RemoteOrganizationDataSource
import com.ezzy.projectmanagement.data.remote.RemoteProjectDataSource
import com.ezzy.projectmanagement.data.remote.RemoteUserDataSource
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
    ) = OrganizationRepository(RemoteOrganizationDataSource(
        fireStore, storage, saveUserOrganizations, firebaseAuth
    ))

    @Provides
    @Singleton
    fun provideProjectRepository(
        fireStore: FirebaseFirestore,
        saveUserProjects: SaveUserProjects,
        firebaseAuth: FirebaseAuth
    ) = ProjectRepository(
        RemoteProjectDataSource(fireStore, saveUserProjects, firebaseAuth)
    )

    @Provides
    @Singleton
    fun provideUserRepository(fireStore: FirebaseFirestore)
        = UserRepository(RemoteUserDataSource(fireStore))

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
}