package ellis.moneymouse

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface NewIncomeDao {
    @Insert
    fun insertNewIncome(vararg: NewIncome)

    @Query("SELECT newIncome FROM newincome WHERE eid = :id")
    fun getNewIncome(id: Int) : Double

    @Query("SELECT date FROM newincome WHERE eid = :id")
    fun getDate(id: Int) : String

    @Query("SELECT eid FROM newincome WHERE eid = (SELECT MAX(eid) FROM newincome)")
    fun getLastEid() : Int

    @Query("DELETE FROM newincome")
    fun delete()
}