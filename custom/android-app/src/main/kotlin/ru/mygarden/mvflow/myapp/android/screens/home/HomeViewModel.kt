package ru.mygarden.mvflow.myapp.android.screens.home
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import net.pedroloureiro.mvflow.MVFlowWithEffects

class HomeViewModel : ViewModel() {

    val mvFlow: MVFlowWithEffects<HomeMVFlow.State, HomeMVFlow.Action, HomeMVFlow.Effect>

    init {
        mvFlow = HomeMVFlow.create(coroutineScope = viewModelScope)
    }
}
