package com.example.utils

sealed class ProfileUpdateResult {
    object NotFound : ProfileUpdateResult()          // user has no walker/wanderer
    object Forbidden : ProfileUpdateResult()         // mismatch walkerId/wandererId
    object Failed : ProfileUpdateResult()            // DB update failed
    object Success : ProfileUpdateResult()           // updated successfully
}