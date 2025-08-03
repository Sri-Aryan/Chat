package com.cometchat.sampleapp.kotlin.data.interfaces

import com.cometchat.chat.models.User

fun interface OnSampleUserSelected {
    fun onSelect(user: User?)
}
