package ellis.moneymouse

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface NewExpensesDao {
    @Insert
    fun insertNewExpense(vararg: NewExpenses)

    @Query("SELECT newExpense FROM newexpenses WHERE eid = :id")
    fun getLastTenExpenses(id: Int) : Double

    @Query("SELECT date FROM newexpenses WHERE eid = :id")
    fun getDate(id: Int) : String

    @Query("SELECT eid FROM newexpenses WHERE eid = (SELECT MAX(eid) FROM newexpenses)")
    fun getLastEid() : Int

    @Query("DELETE FROM newexpenses")
    fun delete()
}