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

enum class VolunteerFilter {
    ALL, WITH_DUTIES, NO_DUTIES
}

data class VolunteerItemState(
    val user: User,
    val dutyCount: Int
)

data class VolunteersUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedYearMonth: YearMonth = YearMonth.now(),
    val searchQuery: String = "",
    val selectedFilter: VolunteerFilter = VolunteerFilter.ALL,
    val allCount: Int = 0,
    val withDutiesCount: Int = 0,
    val noDutiesCount: Int = 0,
    val volunteers: List<VolunteerItemState> = emptyList()
)

@HiltViewModel
class VolunteersViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dutyRepository: DutyRepository
) : ViewModel() {

    private val _selectedYearMonth = MutableStateFlow(YearMonth.now())
    val selectedYearMonth: StateFlow<YearMonth> = _selectedYearMonth

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedFilter = MutableStateFlow(VolunteerFilter.ALL)
    val selectedFilter: StateFlow<VolunteerFilter> = _selectedFilter

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

    private val volunteers = authRepository.getUsers()
        .catch { e -> _errorMessage.value = e.message ?: "Failed to load volunteers" }

    private val applications = dutyRepository.getAllApplications()
        .catch { e -> _errorMessage.value = e.message ?: "Failed to load applications" }

    private val duties = dutyRepository.getAllDuties()
        .catch { e -> _errorMessage.value = e.message ?: "Failed to load duties" }

    val uiState: StateFlow<VolunteersUiState> = combine(
        _selectedYearMonth,
        _searchQuery,
        _selectedFilter,
        volunteers,
        applications,
        duties
    ) { flows ->
        @Suppress("UNCHECKED_CAST")
        val yearMonth = flows[0] as YearMonth
        @Suppress("UNCHECKED_CAST")
        val query = flows[1] as String
        @Suppress("UNCHECKED_CAST")
        val filter = flows[2] as VolunteerFilter
        @Suppress("UNCHECKED_CAST")
        val usersList = flows[3] as List<User>
        @Suppress("UNCHECKED_CAST")
        val appsList = flows[4] as List<DutyApplication>
        @Suppress("UNCHECKED_CAST")
        val dutiesList = flows[5] as List<Duty>

        val monthStr = yearMonth.toString()
        val monthDuties = dutiesList.filter { it.date.startsWith(monthStr) }
        val targetDutyIds = if (monthDuties.isNotEmpty()) {
            monthDuties.map { it.id }.toSet()
        } else {
            dutiesList.map { it.id }.toSet()
        }

        val relevantApps = if (targetDutyIds.isNotEmpty()) {
            appsList.filter { it.dutyId in targetDutyIds }
        } else {
            appsList
        }

        val allItems = usersList.map { user ->
            val count = relevantApps.count { it.userId == user.uid }
            VolunteerItemState(user = user, dutyCount = count)
        }

        val allCount = allItems.size
        val withDutiesCount = allItems.count { it.dutyCount > 0 }
        val noDutiesCount = allItems.count { it.dutyCount == 0 }

        val searchFiltered = if (query.isBlank()) {
            allItems
        } else {
            allItems.filter {
                it.user.name.contains(query, ignoreCase = true) ||
                        it.user.email.contains(query, ignoreCase = true)
            }
        }

        val finalFiltered = when (filter) {
            VolunteerFilter.ALL -> searchFiltered
            VolunteerFilter.WITH_DUTIES -> searchFiltered.filter { it.dutyCount > 0 }
            VolunteerFilter.NO_DUTIES -> searchFiltered.filter { it.dutyCount == 0 }
        }

        VolunteersUiState(
            isLoading = false,
            errorMessage = _errorMessage.value,
            selectedYearMonth = yearMonth,
            searchQuery = query,
            selectedFilter = filter,
            allCount = allCount,
            withDutiesCount = withDutiesCount,
            noDutiesCount = noDutiesCount,
            volunteers = finalFiltered
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = VolunteersUiState(isLoading = true)
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onFilterSelected(filter: VolunteerFilter) {
        _selectedFilter.value = filter
    }

    fun onMonthSelected(yearMonth: YearMonth) {
        _selectedYearMonth.value = yearMonth
    }
}
