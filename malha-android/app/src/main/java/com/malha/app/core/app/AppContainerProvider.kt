package com.malha.app.core.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.malha.app.MalhaApplication

val AndroidViewModel.appContainer: AppContainer
    get() = (getApplication<Application>() as MalhaApplication).appContainer

