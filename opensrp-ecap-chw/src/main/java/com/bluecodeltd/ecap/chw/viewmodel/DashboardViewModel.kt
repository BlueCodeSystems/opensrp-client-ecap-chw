package com.bluecodeltd.ecap.chw.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluecodeltd.ecap.chw.dao.CaregiverVisitationDao
import com.bluecodeltd.ecap.chw.dao.HouseholdDao
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao
import com.bluecodeltd.ecap.chw.model.Child
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalTime

data class DashboardState(
    val visitsDue: Int = 0,
    val subpops: ArrayList<Int> = arrayListOf(),
    val householdsCount: String? = null,
    val vcasCount: String? = null,
    val lastUpdated: LocalTime? = null
)

class DashboardViewModel : ViewModel() {
    private val _state = MutableLiveData(DashboardState())
    val state: LiveData<DashboardState> = _state

    fun refresh(caseworkerPhone: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val now = LocalTime.now()
            val visitDates = CaregiverVisitationDao.getAllVisitDates()
            val visitsDue = computeVisitsDue(visitDates)
            val subpops: ArrayList<Int> = if (caseworkerPhone.isNullOrEmpty())
                countSubpop(IndexPersonDao.getAllChildrenSubpops())
            else
                countSubpop(IndexPersonDao.getAllChildrenSubpopsByCaseworkerPhoneNumber(caseworkerPhone))
            val householdsCount = if (caseworkerPhone.isNullOrEmpty())
                HouseholdDao.countNumberoFHouseholds() else HouseholdDao.countNumberOfHouseholdsByCaseworkerPhone(caseworkerPhone)
            val vcasCount = if (caseworkerPhone.isNullOrEmpty())
                IndexPersonDao.countAllChildren() else IndexPersonDao.countAllChildrenByCaseworkerPhoneNumber(caseworkerPhone)

            _state.postValue(
                DashboardState(
                    visitsDue = visitsDue,
                    subpops = subpops,
                    householdsCount = householdsCount,
                    vcasCount = vcasCount,
                    lastUpdated = now
                )
            )
        }
    }

    private fun computeVisitsDue(dates: List<String>?): Int {
        if (dates.isNullOrEmpty()) return 0
        // Dates formatted as dd-MM-u in app logic; keep logic consistent using Java time via helpers if needed
        // To avoid parsing overhead here, keep behavior aligned with existing Java code paths
        return try {
            val formatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-u")
            val today = java.time.LocalDate.now()
            dates.count { d ->
                try {
                    if (d.isNullOrEmpty()) false
                    else java.time.Period.between(java.time.LocalDate.parse(d, formatter), today).days < 1
                } catch (_: Exception) { false }
            }
        } catch (_: Exception) { 0 }
    }

    private fun countSubpop(childList: List<Child>?): ArrayList<Int> {
        val totals = arrayListOf(0, 0, 0, 0, 0, 0)
        if (childList == null) return totals
        childList.forEach { c ->
            if (c.subpop1 == "true") totals[0]++
            if (c.subpop2 == "true") totals[1]++
            if (c.subpop3 == "true") totals[2]++
            if (c.subpop4 == "true") totals[3]++
            if (c.subpop5 == "true") totals[4]++
            if (c.subpop6 == "true") totals[5]++
        }
        return totals
    }
}

