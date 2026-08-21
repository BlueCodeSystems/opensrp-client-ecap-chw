package com.bluecodeltd.ecap.chw.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluecodeltd.ecap.chw.dao.HouseholdDao
import com.bluecodeltd.ecap.chw.model.CasePlanModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HouseholdCasePlanViewModel: ViewModel() {
    private val _casePlans = MutableLiveData<ArrayList<CasePlanModel>>(arrayListOf())
    val casePlans: LiveData<ArrayList<CasePlanModel>> = _casePlans

    fun refresh(householdId: String?) {
        val id = householdId?.trim()
        if (id.isNullOrEmpty()) {
            _casePlans.postValue(arrayListOf())
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val list = ArrayList(HouseholdDao.getCasePlansById(id) ?: emptyList())
                _casePlans.postValue(list)
            } catch (_: Exception) {}
        }
    }
}

