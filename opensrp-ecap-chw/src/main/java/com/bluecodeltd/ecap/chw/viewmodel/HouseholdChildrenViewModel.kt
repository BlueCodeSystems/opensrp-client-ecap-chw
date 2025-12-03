package com.bluecodeltd.ecap.chw.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao
import com.bluecodeltd.ecap.chw.model.Child
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class HouseholdChildrenState(
    val children: ArrayList<Child> = arrayListOf(),
    val count: String? = null
)

class HouseholdChildrenViewModel: ViewModel() {
    private val _state = MutableLiveData(HouseholdChildrenState())
    val state: LiveData<HouseholdChildrenState> = _state

    fun refresh(householdId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val list = ArrayList(IndexPersonDao.getFamilyChildren(householdId))
                val count = IndexPersonDao.countChildren(householdId)
                _state.postValue(HouseholdChildrenState(list, count))
            } catch (_: Exception) {}
        }
    }
}

