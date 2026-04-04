package com.grocery.manager.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        Company::class,
        Category::class,
        Product::class,
        Variant::class,
        RecentSearch::class,
        Contact::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun companyDao(): CompanyDao
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun variantDao(): VariantDao
    abstract fun contactDao(): ContactDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
            CREATE TABLE IF NOT EXISTS `contact` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `companyId` INTEGER NOT NULL,
                `name` TEXT NOT NULL,
                `role` TEXT NOT NULL DEFAULT '',
                `phone` TEXT NOT NULL,
                FOREIGN KEY(`companyId`) REFERENCES `company`(`id`) ON DELETE CASCADE
            )
        """)
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_contact_companyId` ON `contact` (`companyId`)"
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "grocery_manager_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
