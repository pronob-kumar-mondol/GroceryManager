package com.grocery.manager.data.repository

import com.grocery.manager.data.local.AppDatabase
import com.grocery.manager.data.local.Category
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val db: AppDatabase) {

    private val categoryDao = db.categoryDao()

    fun getAllCategories(): Flow<List<Category>> =
        categoryDao.getAllCategories()

    suspend fun insertCategory(category: Category) =
        categoryDao.insertCategory(category)

    suspend fun deleteCategory(category: Category) =
        categoryDao.deleteCategory(category)
}
