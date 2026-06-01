package com.bluecodeltd.ecap.chw.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluecodeltd.ecap.chw.dao.CaregiverVisitationDao
import com.bluecodeltd.ecap.chw.dao.EcMotherIndexDao
import com.bluecodeltd.ecap.chw.dao.HivTestingServiceDao
import com.bluecodeltd.ecap.chw.dao.HouseholdDao
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao
import com.bluecodeltd.ecap.chw.dao.PMTCTMotherDao
import com.bluecodeltd.ecap.chw.model.Child
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class DashboardState(
    val visitsDue: Int = 0,
    val subpops: ArrayList<Int> = arrayListOf(),
    val householdsCount: String? = null,
    val vcasCount: String? = null,
    val maleCount: String = "0",
    val femaleCount: String = "0",
    val caregiverMaleCount: String = "0",
    val caregiverFemaleCount: String = "0",
    val mothersCount: String = "0",
    val htsCount: String = "0",
    val pmtctCount: String = "0",
    val lastUpdated: LocalDateTime? = null
)

class DashboardViewModel : ViewModel() {
    private val _state = MutableLiveData(DashboardState())
    val state: LiveData<DashboardState> = _state

    fun refresh(caseworkerPhone: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val now = LocalDateTime.now()
            val visitDates = CaregiverVisitationDao.getAllVisitDates()
            val visitsDue = computeVisitsDue(visitDates)
            val childList: List<Child>? = if (caseworkerPhone.isNullOrEmpty())
                IndexPersonDao.getAllChildrenSubpops()
            else
                IndexPersonDao.getAllChildrenSubpopsByCaseworkerPhoneNumber(caseworkerPhone)
            val subpops = countSubpop(childList)
            val genderCounts = countGender(childList)
            val householdsCount = if (caseworkerPhone.isNullOrEmpty())
                HouseholdDao.countNumberoFHouseholds() else HouseholdDao.countNumberOfHouseholdsByCaseworkerPhone(caseworkerPhone)
            val vcasCount = if (caseworkerPhone.isNullOrEmpty())
                IndexPersonDao.countAllChildren() else IndexPersonDao.countAllChildrenByCaseworkerPhoneNumber(caseworkerPhone)

            // Caregiver gender counts
            val caregiverMaleCount = try { HouseholdDao.countMaleCaregivers() ?: "0" } catch (_: Exception) { "0" }
            val caregiverFemaleCount = try { HouseholdDao.countFemaleCaregivers() ?: "0" } catch (_: Exception) { "0" }

            // Register counts
            val mothersCount = try { PMTCTMotherDao.countAllMotherIndexRecords() ?: "0" } catch (_: Exception) { "0" }
            val htsCount = try { HivTestingServiceDao.countAllHtsClients() ?: "0" } catch (_: Exception) { "0" }
            val pmtctCount = try { EcMotherIndexDao.countAllPmtctMothers() ?: "0" } catch (_: Exception) { "0" }

            _state.postValue(
                DashboardState(
                    visitsDue = visitsDue,
                    subpops = subpops,
                    householdsCount = householdsCount,
                    vcasCount = vcasCount,
                    maleCount = genderCounts[0].toString(),
                    femaleCount = genderCounts[1].toString(),
                    caregiverMaleCount = caregiverMaleCount,
                    caregiverFemaleCount = caregiverFemaleCount,
                    mothersCount = mothersCount,
                    htsCount = htsCount,
                    pmtctCount = pmtctCount,
                    lastUpdated = now
                )
            )
        }
    }

    private fun computeVisitsDue(dates: List<String>?): Int {
        if (dates.isNullOrEmpty()) return 0
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

    /** Returns [maleCount, femaleCount] from the child list. */
    private fun countGender(childList: List<Child>?): IntArray {
        var males = 0; var females = 0
        childList?.forEach { c ->
            when (c.gender?.lowercase()) {
                "male" -> males++
                "female" -> females++
            }
        }
        return intArrayOf(males, females)
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
