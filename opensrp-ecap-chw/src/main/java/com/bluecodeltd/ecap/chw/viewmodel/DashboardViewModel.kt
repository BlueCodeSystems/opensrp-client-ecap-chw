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
import com.bluecodeltd.ecap.chw.dao.ServiceRegisterDao
import com.bluecodeltd.ecap.chw.util.ServiceRegister
import com.bluecodeltd.ecap.chw.util.Subpopulation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
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
    val appointmentsCount: String = "0",
    val prepCount: String = "0",
    val referralLinkagesCount: String = "0",
    val lastUpdated: LocalDateTime? = null
)

class DashboardViewModel : ViewModel() {
    private val _state = MutableLiveData(DashboardState())
    val state: LiveData<DashboardState> = _state

    private var refreshJob: Job? = null

    fun refresh(caseworkerPhone: String? = null) {
        // Overlapping refreshes (onResume + filter change) used to run concurrently; supersede the old one.
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch(Dispatchers.IO) {
            val now = LocalDateTime.now()
            val visitDates = CaregiverVisitationDao.getAllVisitDates()
            val visitsDue = computeVisitsDue(visitDates)
            ensureActive()
            // One count per Subpopulation in declaration order, then males and females
            val totals = IndexPersonDao.countSubpopsAndGender(caseworkerPhone)
            val subpopCount = Subpopulation.values().size
            val subpops = ArrayList(totals.take(subpopCount))
            ensureActive()
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
            val appointmentsCount = try { ServiceRegisterDao.countClients(ServiceRegister.APPOINTMENTS) ?: "0" } catch (_: Exception) { "0" }
            val prepCount = try { ServiceRegisterDao.countClients(ServiceRegister.PREP) ?: "0" } catch (_: Exception) { "0" }
            val referralLinkagesCount = try { ServiceRegisterDao.countClients(ServiceRegister.REFERRAL_LINKAGES) ?: "0" } catch (_: Exception) { "0" }

            ensureActive()
            _state.postValue(
                DashboardState(
                    visitsDue = visitsDue,
                    subpops = subpops,
                    householdsCount = householdsCount,
                    vcasCount = vcasCount,
                    maleCount = totals[subpopCount].toString(),
                    femaleCount = totals[subpopCount + 1].toString(),
                    caregiverMaleCount = caregiverMaleCount,
                    caregiverFemaleCount = caregiverFemaleCount,
                    mothersCount = mothersCount,
                    htsCount = htsCount,
                    pmtctCount = pmtctCount,
                    appointmentsCount = appointmentsCount,
                    prepCount = prepCount,
                    referralLinkagesCount = referralLinkagesCount,
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

}
