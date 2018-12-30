package ellis.moneymouse

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface UserDao {
    @Query("UPDATE user SET monthlyIncome = :monthlyInc WHERE uid = 0")
    fun updateMonthlyIncome(monthlyInc: Double)

    @Query("SELECT monthlyIncome FROM user where uid = 0")
    fun getMonthlyIncome() : Double

    @Query("UPDATE user SET newIncome = :newInc WHERE uid = 0")
    fun updateNewIncome(newInc: Double)

    @Query("SELECT newIncome FROM user where uid = 0")
    fun getNewIncome() : Double

    @Query("UPDATE user SET monthlyExpenses = :monthlyExp WHERE uid = 0")
    fun updateMonthlyExpenses(monthlyExp: Double)

    @Query("SELECT monthlyExpenses FROM user where uid = 0")
    fun getMonthlyExpenses() : Double

    @Query("UPDATE user SET newExpense = :newExp WHERE uid = 0")
    fun updateNewExpense(newExp: Double)

    @Query("SELECT newExpense FROM user where uid = 0")
    fun getNewExpense() : Double

    @Query("UPDATE user SET todayMoney = :todayMon WHERE uid = 0")
    fun updateTodayMoney(todayMon: Double)

    @Query("SELECT todayMoney FROM user where uid = 0")
    fun getTodayMoney() : Double

    @Query("UPDATE user SET month = :newMonth WHERE uid = 0")
    fun updateMonth(newMonth: Int)

    @Query("SELECT month FROM user where uid = 0")
    fun getMonth() : Int

    @Insert
    fun insertOne(vararg users: User)

    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    /*@Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): User

    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)*/
}