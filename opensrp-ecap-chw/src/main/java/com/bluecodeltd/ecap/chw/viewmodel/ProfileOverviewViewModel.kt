package com.bluecodeltd.ecap.chw.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluecodeltd.ecap.chw.dao.*
import com.bluecodeltd.ecap.chw.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class ProfileOverviewState(
    val household: Household? = null,
    val recentServices: List<VCAServiceModel> = emptyList(),
    val abym: AbymSubpopulationModel? = null,
    val muac: MuacModel? = null,
    val screen: VcaScreeningModel? = null
)

class ProfileOverviewViewModel: ViewModel() {
    private val _state = MutableLiveData(ProfileOverviewState())
    val state: LiveData<ProfileOverviewState> = _state

    fun refresh(householdId: String, childUniqueId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val household = HouseholdDao.getVcaSubPop(householdId, childUniqueId)
                val recent = VCAServiceReportDao.getRecentServicesByVCAID(childUniqueId)
                val abym = AbymSubpopulationDao.getAbymSubpopulation(childUniqueId)
                val muac = MuacDao.getMuac(childUniqueId)
                val screen = VCAScreeningDao.getVcaScreening(childUniqueId)
                _state.postValue(ProfileOverviewState(household, recent, abym, muac, screen))
            } catch (_: Exception) {}
        }
    }
}

