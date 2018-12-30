package ellis.moneymouse

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey var uid: Int,
    @ColumnInfo(name = "monthlyIncome") var monthlyIncome: Double?,
    @ColumnInfo(name = "monthlyExpenses") var monthlyExpenses: Double?,
    @ColumnInfo(name = "newIncome") var newIncome: Double?,
    @ColumnInfo(name = "newExpense") var newExpense: Double?,
    @ColumnInfo(name = "todayMoney") var todayMoney: Double?,
    @ColumnInfo(name = "month") var month: Int?
)