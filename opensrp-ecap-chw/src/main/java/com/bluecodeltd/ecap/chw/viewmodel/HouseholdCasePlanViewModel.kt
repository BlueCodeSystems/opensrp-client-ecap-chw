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

    fun refresh(householdId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val list = ArrayList(HouseholdDao.getCasePlansById(householdId))
                _casePlans.postValue(list)
            } catch (_: Exception) {}
        }
    }
}

