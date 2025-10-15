package com.bluecodeltd.ecap.chw.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluecodeltd.ecap.chw.dao.*
import com.bluecodeltd.ecap.chw.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class HouseholdDetailsState(
    val children: ArrayList<Child> = arrayListOf(),
    val weServiceCaregiverModel: WeServiceCaregiverModel? = null,
    val caregiverAssessmentModel: CaregiverAssessmentModel? = null,
    val caregiverVisitationModel: CaregiverVisitationModel? = null,
    val caregiverHivAssessmentModel: CaregiverHivAssessmentModel? = null,
    val graduationModel: GraduationModel? = null,
    val updatedCaregiver: newCaregiverModel? = null,
    val house: Household? = null,
    val countFemales: String? = null,
    val countMales: String? = null,
    val allMalesBirthDates: List<String>? = null,
    val allFemalesBirthDates: List<String>? = null,
    val allChildrenBirthDates: List<String>? = null
)

class HouseholdDetailsViewModel : ViewModel() {
    private val _state = MutableLiveData<HouseholdDetailsState>()
    val state: LiveData<HouseholdDetailsState> = _state

    fun refresh(householdId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val children = ArrayList(IndexPersonDao.getFamilyChildren(householdId))
                val weService = WeServiceCaregiverDoa.getWeServiceCaregiver(householdId)
                val assessment = CaregiverAssessmentDao.getCaregiverAssessment(householdId)
                val visitation = CaregiverVisitationDao.getCaregiverVisitation(householdId)
                val hivAssessment = CaregiverHivAssessmentDao.getCaregiverHivAssessment(householdId)
                val grad = GraduationDao.getGraduation(householdId)
                val updated = newCaregiverDao.getNewCaregiverById(householdId)
                val house = HouseholdDao.getHousehold(householdId)
                val females = IndexPersonDao.countFemales(householdId)
                val males = IndexPersonDao.countMales(householdId)
                val malesBirthdates = IndexPersonDao.getMalesBirthdates(householdId)
                val femalesBirthdates = IndexPersonDao.getAllFemalesBirthdate(householdId)
                val childrenBirthdates = IndexPersonDao.getAllChildrenBirthdate(householdId)

                _state.postValue(
                    HouseholdDetailsState(
                        children = children,
                        weServiceCaregiverModel = weService,
                        caregiverAssessmentModel = assessment,
                        caregiverVisitationModel = visitation,
                        caregiverHivAssessmentModel = hivAssessment,
                        graduationModel = grad,
                        updatedCaregiver = updated,
                        house = house,
                        countFemales = females,
                        countMales = males,
                        allMalesBirthDates = malesBirthdates,
                        allFemalesBirthDates = femalesBirthdates,
                        allChildrenBirthDates = childrenBirthdates
                    )
                )
            } catch (_: Exception) {
            }
        }
    }
}

