package com.example.studyApp.comment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CommentActivityViewModel : ViewModel() {
    public var mCommentStatus = MutableLiveData<Boolean>()

}