package ellis.moneymouse

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class NewIncome(
    @PrimaryKey var eid: Int,
    @ColumnInfo(name = "newIncome") var newIncome: Double?,
    @ColumnInfo(name = "date") var date: String?
)