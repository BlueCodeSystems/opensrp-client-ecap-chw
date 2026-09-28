package com.bluecodeltd.ecap.chw.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluecodeltd.ecap.chw.dao.CaregiverVisitationDao
import com.bluecodeltd.ecap.chw.model.CaregiverVisitationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HouseholdVisitsViewModel: ViewModel() {
    private val _visits = MutableLiveData<ArrayList<CaregiverVisitationModel>>(arrayListOf())
    val visits: LiveData<ArrayList<CaregiverVisitationModel>> = _visits

    fun refresh(householdId: String?) {
        // Called from Java fragments; the id can be null while the host activity is still loading.
        if (householdId.isNullOrBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val list = ArrayList(CaregiverVisitationDao.getVisitsByID(householdId))
                _visits.postValue(list)
            } catch (_: Exception) {}
        }
    }
}

