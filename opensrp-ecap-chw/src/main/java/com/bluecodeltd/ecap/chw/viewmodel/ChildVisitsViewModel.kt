package com.bluecodeltd.ecap.chw.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluecodeltd.ecap.chw.dao.VcaVisitationDao
import com.bluecodeltd.ecap.chw.model.VcaVisitationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChildVisitsViewModel: ViewModel() {
    private val _visits = MutableLiveData<ArrayList<VcaVisitationModel>>(arrayListOf())
    val visits: LiveData<ArrayList<VcaVisitationModel>> = _visits

    fun refresh(childId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val list = ArrayList(VcaVisitationDao.getVisitsByID(childId))
                _visits.postValue(list)
            } catch (_: Exception) {}
        }
    }
}

