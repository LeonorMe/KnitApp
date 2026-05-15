package com.malha.app.core.app

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.malha.app.MalhaApplication

val AndroidViewModel.appContainer: AppContainer
    get() = (getApplication<Application>() as MalhaApplication).appContainer

val Context.appContainer: AppContainer
    get() = (applicationContext as MalhaApplication).appContainer
