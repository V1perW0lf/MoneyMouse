package ellis.moneymouse

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

//Create database with tables User, NewExpenses, and newIncome
@Database(entities = [User::class, NewExpenses::class, NewIncome::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun newExpensesDao(): NewExpensesDao
    abstract fun newIncomeDao(): NewIncomeDao
}