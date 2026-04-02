package com.grocery.manager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.grocery.manager.data.local.AppDatabase
import com.grocery.manager.data.local.Company
import com.grocery.manager.data.repository.CompanyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CompanyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CompanyRepository(AppDatabase.getDatabase(application))

    val companies: StateFlow<List<Company>> = repository
        .getAllCompanies()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insertCompany(company: Company) {
        viewModelScope.launch {
            repository.insertCompany(company)
        }
    }

    fun updateCompany(company: Company) {
        viewModelScope.launch {
            repository.updateCompany(company)
        }
    }

    fun deleteCompany(company: Company) {
        viewModelScope.launch {
            repository.deleteCompany(company)
        }
    }

    suspend fun getCompanyById(id: Int): Company? =
        repository.getCompanyById(id)
}