package ru.mygarden.mvflow.myapp.android.screens.mygarden_main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import net.pedroloureiro.mvflow.MVFlowWithEffects

class MyGardenMainViewModel : ViewModel() {

    val mvFlow: MVFlowWithEffects<MyGardenMainMVFlow.State, MyGardenMainMVFlow.Action, MyGardenMainMVFlow.Effect>

    init {
        mvFlow = MyGardenMainMVFlow.create(coroutineScope = viewModelScope)
    }
}
