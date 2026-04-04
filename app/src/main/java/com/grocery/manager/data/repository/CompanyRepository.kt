package com.grocery.manager.data.repository

import com.grocery.manager.data.local.AppDatabase
import com.grocery.manager.data.local.Company
import com.grocery.manager.data.local.Contact
import kotlinx.coroutines.flow.Flow

class CompanyRepository(private val db: AppDatabase) {

    private val companyDao = db.companyDao()
    private val contactDao = db.contactDao()

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

    // Contact operations
    fun getContactsForCompany(companyId: Int): Flow<List<Contact>> =
        contactDao.getContactsForCompany(companyId)

    suspend fun insertCompanyWithContacts(
        company: Company,
        contacts: List<Contact>
    ): Long {
        val companyId = companyDao.insertCompany(company)
        val contactsWithId = contacts.map { it.copy(companyId = companyId.toInt()) }
        contactDao.insertContacts(contactsWithId)
        return companyId
    }

    suspend fun updateCompanyWithContacts(
        company: Company,
        contacts: List<Contact>
    ) {
        companyDao.updateCompany(company)
        contactDao.deleteAllContactsForCompany(company.id)
        val contactsWithId = contacts.map { it.copy(companyId = company.id) }
        contactDao.insertContacts(contactsWithId)
    }

    suspend fun deleteContact(contact: Contact) =
        contactDao.deleteContact(contact)
}