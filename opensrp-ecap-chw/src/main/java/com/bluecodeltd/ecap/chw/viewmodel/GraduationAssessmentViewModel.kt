package com.bluecodeltd.ecap.chw.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluecodeltd.ecap.chw.dao.GraduationDao
import com.bluecodeltd.ecap.chw.model.GraduationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GraduationAssessmentViewModel : ViewModel() {
    private val _assessments = MutableLiveData<ArrayList<GraduationModel>>(arrayListOf())
    val assessments: LiveData<ArrayList<GraduationModel>> = _assessments

    fun refresh(householdId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val list = ArrayList(GraduationDao.getAssessment(householdId))
                _assessments.postValue(list)
            } catch (_: Exception) {
            }
        }
    }
}
