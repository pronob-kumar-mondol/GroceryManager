package com.grocery.manager.data.repository

import com.grocery.manager.data.local.AppDatabase
import com.grocery.manager.data.local.Company
import kotlinx.coroutines.flow.Flow

class CompanyRepository(private val db: AppDatabase) {

    private val companyDao = db.companyDao()

    fun getAllCompanies(): Flow<List<Company>> =
        companyDao.getAllCompanies()

    suspend fun getCompanyById(id: Int): Company? =
        companyDao.getCompanyById(id)

    suspend fun insertCompany(company: Company): Long =
        companyDao.insertCompany(company)

    suspend fun updateCompany(company: Company) =
        companyDao.updateCompany(company)

    suspend fun deleteCompany(company: Company) =
        companyDao.deleteCompany(company)
}