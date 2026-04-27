package com.example.liboffan.screens.profile

sealed interface ProfileSubScreen {
    object Main : ProfileSubScreen
    object CreateStory : ProfileSubScreen
}