package ellis.moneymouse

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

//Create database with tables User and NewExpenses
@Database(entities = [User::class, NewExpenses::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun newExpensesDao(): NewExpensesDao
}