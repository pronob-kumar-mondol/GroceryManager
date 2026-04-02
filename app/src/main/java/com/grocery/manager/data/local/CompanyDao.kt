package com.grocery.manager.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CompanyDao {

    @Query("SELECT * FROM company ORDER BY name ASC")
    fun getAllCompanies(): Flow<List<Company>>

    @Query("SELECT * FROM company WHERE id = :id")
    suspend fun getCompanyById(id: Int): Company?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompany(company: Company): Long

    @Update
    suspend fun updateCompany(company: Company)

    @Delete
    suspend fun deleteCompany(company: Company)
}