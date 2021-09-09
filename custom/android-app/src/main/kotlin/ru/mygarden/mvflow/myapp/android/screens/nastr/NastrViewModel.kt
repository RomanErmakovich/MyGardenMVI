package ru.mygarden.mvflow.myapp.android.screens.nastr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import net.pedroloureiro.mvflow.MVFlowWithEffects

class NastrViewModel : ViewModel() {

    val mvFlow: MVFlowWithEffects<NastrMVFlow.State, NastrMVFlow.Action,
            NastrMVFlow.Effect>

    init {
        mvFlow = NastrMVFlow.create(coroutineScope = viewModelScope)
    }
}
