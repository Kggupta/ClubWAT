package com.example.clubwat.viewmodels

import androidx.lifecycle.ViewModel
import com.example.clubwat.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ClubManagementViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {}
