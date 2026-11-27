package com.example.btsallot.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btsallot.domain.model.Duty
import com.example.btsallot.domain.model.DutyApplication
import com.example.btsallot.domain.model.User
import com.example.btsallot.domain.repository.AuthRepository
import com.example.btsallot.domain.repository.DutyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import java.time.YearMonth
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedYearMonth: YearMonth = YearMonth.now(),
    val totalDutiesCount: Int = 0,
    val totalApplicationsCount: Int = 0,
    val dutiesWithApplicationsCount: Int = 0,
    val activeVolunteersCount: Int = 0,
    val upcomingDuties: List<Duty> = emptyList()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dutyRepository: DutyRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _selectedYearMonth = MutableStateFlow(YearMonth.now())
    val selectedYearMonth: StateFlow<YearMonth> = _selectedYearMonth

    private val _errorMessage = MutableStateFlow<String?>(null)

    init {
        startRealTimeSync()
    }

    fun startRealTimeSync() {
        val startMonth = YearMonth.now().minusMonths(6).atDay(1).toString()
        val endMonth = YearMonth.now().plusMonths(6).atEndOfMonth().toString()

        dutyRepository.listenToDutyUpdates(startMonth, endMonth)
            .catch { e -> _errorMessage.value = e.message }
            .launchIn(viewModelScope)

        dutyRepository.listenToApplications()
            .catch { e -> _errorMessage.value = e.message }
            .launchIn(viewModelScope)
    }

    private val duties = dutyRepository.getAllDuties()
        .catch { e -> _errorMessage.value = e.message ?: "Failed to load duties" }

    private val applications = dutyRepository.getAllApplications()
        .catch { e -> _errorMessage.value = e.message ?: "Failed to load applications" }

    private val volunteers = authRepository.getUsers()
        .catch { e -> _errorMessage.value = e.message ?: "Failed to load volunteers" }

    val uiState: StateFlow<DashboardUiState> = combine(
        _selectedYearMonth,
        duties,
        applications,
        volunteers,
        _errorMessage
    ) { yearMonth: YearMonth, dutiesList: List<Duty>, appsList: List<DutyApplication>, usersList: List<User>, error: String? ->
        val monthStr = yearMonth.toString()
        val monthDuties = dutiesList.filter { it.date.startsWith(monthStr) }
        val targetDuties = if (monthDuties.isNotEmpty()) monthDuties else dutiesList

        val targetDutyIds = targetDuties.map { it.id }.toSet()
        val monthApplications = appsList.filter { it.dutyId in targetDutyIds }
        val targetApplications = if (monthApplications.isNotEmpty()) monthApplications else appsList

        val totalDutiesCount = targetDuties.size
        val totalApplicationsCount = targetApplications.size
        val dutiesWithAppsCount = targetDuties.count { duty ->
            duty.btsReservedCount > 0 || appsList.any { it.dutyId == duty.id }
        }
        val activeVolunteersCount = if (usersList.isNotEmpty()) usersList.size else targetApplications.map { it.userId }.distinct().size

        val sortedUpcoming = targetDuties
            .sortedWith(compareBy<Duty> { it.date }.thenBy { it.startMinutes })

        DashboardUiState(
            isLoading = false,
            errorMessage = error,
            selectedYearMonth = yearMonth,
            totalDutiesCount = totalDutiesCount,
            totalApplicationsCount = totalApplicationsCount,
            dutiesWithApplicationsCount = dutiesWithAppsCount,
            activeVolunteersCount = activeVolunteersCount,
            upcomingDuties = sortedUpcoming
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState(isLoading = true)
    )

    fun onMonthSelected(yearMonth: YearMonth) {
        _selectedYearMonth.value = yearMonth
    }
}
