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

    fun refresh(householdId: String?) {
        val id = householdId?.trim()
        if (id.isNullOrEmpty()) {
            _state.postValue(HouseholdChildrenState(arrayListOf(), "0"))
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val list = ArrayList(IndexPersonDao.getFamilyChildren(id) ?: emptyList())
                val count = IndexPersonDao.countChildren(id) ?: "0"
                _state.postValue(HouseholdChildrenState(list, count))
            } catch (_: Exception) {}
        }
    }
}
