package ellis.moneymouse

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class NewExpenses(
    @PrimaryKey var eid: Int,
    @ColumnInfo(name = "newExpense") var newExpense: Double?,
    @ColumnInfo(name = "date") var date: String?
)