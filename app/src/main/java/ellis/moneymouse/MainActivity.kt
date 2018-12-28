package ellis.moneymouse

import android.arch.persistence.room.Room
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigDecimal
import java.util.*

class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_income -> {
                val intent = Intent(this, IncomeActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_expenses -> {
                val intent = Intent(this, ExpensesActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.selectedItemId = R.id.navigation_home

        moneyForToday()

    }

    private fun moneyForToday() {

        val cal: Calendar = Calendar.getInstance()

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "MoneyMouseDB"
        ).allowMainThreadQueries().build()

        if(db.userDao().getAll().isEmpty())
        {
            val user = User(0,0.00,0.00,0.00,0.00)
            db.userDao().insertOne(user)
        }

        val monthlyInc = BigDecimal(db.userDao().getMonthlyIncome()).format(2)
        val newMoney = BigDecimal(db.userDao().getNewIncome()).format(2)
        val monthlyExpenses = BigDecimal(db.userDao().getMonthlyExpenses()).format(2)
        val newExpense = BigDecimal(db.userDao().getNewExpense()).format(2)

        val newMoneyVal = ((BigDecimal(monthlyInc) - BigDecimal(monthlyExpenses)) / BigDecimal(cal.getActualMaximum(Calendar.DAY_OF_MONTH)))

        val todayMoneyVal = ((BigDecimal(cal.get(Calendar.DAY_OF_MONTH)) * (newMoneyVal)) + BigDecimal(newMoney) - BigDecimal(newExpense))

        val todayMoneyValString = "$" + todayMoneyVal.toString()

        if(todayMoneyVal < BigDecimal(0))
        {
            todayMoney.setTextColor(Color.RED)
        }
        else
        {
            todayMoney.setTextColor(Color.parseColor("#008000"))
        }

        todayMoney.text = todayMoneyValString

        if(newMoneyVal < BigDecimal(0))
        {
            newMoneyBox.setTextColor(Color.RED)
            newMoneyBox.text = getString(R.string.main_message_losing, newMoneyVal.toString())
        }
        else
        {
            newMoneyBox.setTextColor(Color.parseColor("#008000"))
            newMoneyBox.text = getString(R.string.main_message_gaining, newMoneyVal.toString())
        }

        //Log.d("days in this month", cal.getActualMaximum(Calendar.DAY_OF_MONTH).toString())

    }

    private fun BigDecimal.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

}