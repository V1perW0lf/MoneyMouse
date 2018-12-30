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
            //Do nothing when home button is pressed
            R.id.navigation_home -> {
                return@OnNavigationItemSelectedListener true
            }
            //Start the IncomeActivity
            R.id.navigation_income -> {
                val intent = Intent(this, IncomeActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            //Start the ExpensesActivity
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

        moneyCalculations()
    }

    private fun moneyCalculations() {

        val cal: Calendar = Calendar.getInstance()

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "MoneyMouseDB"
        ).allowMainThreadQueries().build()

        //If the app is newly installed, create one user
        if(db.userDao().getAll().isEmpty())
        {
            val user = User(0,0.00,0.00,0.00,0.00, 0.00, cal.get(Calendar.MONTH))
            db.userDao().insertOne(user)
        }

        //val newMonth = 1
        //val newDay = 1

        //If the month has changed, update it and reset new income
        if(db.userDao().getMonth() != cal.get(Calendar.MONTH))
        {
            db.userDao().updateMonth(cal.get(Calendar.MONTH))

            val lastDayAmount = (db.userDao().getMonthlyIncome() - db.userDao().getMonthlyExpenses()) +
                    (db.userDao().getNewIncome() - db.userDao().getNewExpense())

            if(lastDayAmount < 0) {
                db.userDao().updateNewExpense(lastDayAmount * -1)
            }
            else {
                db.userDao().updateNewExpense(0.00)
            }

            db.userDao().updateNewIncome(0.00)

            db.newIncomeDao().delete()
            db.newExpensesDao().delete()
        }

        //Retrieving values from database
        val monthlyInc = BigDecimal(db.userDao().getMonthlyIncome()).format(2)
        val newIncome = BigDecimal(db.userDao().getNewIncome()).format(2)
        val monthlyExpenses = BigDecimal(db.userDao().getMonthlyExpenses()).format(2)
        val newExpense = BigDecimal(db.userDao().getNewExpense()).format(2)

        //Calculating the money that the user is either gaining or losing each day
        val newMoneyVal = ((BigDecimal(monthlyInc) - BigDecimal(monthlyExpenses)) / BigDecimal(cal.getActualMaximum(Calendar.DAY_OF_MONTH)))
        //Calculating the money that is available today
        val todayMoneyVal = ((BigDecimal(cal.get(Calendar.DAY_OF_MONTH)) * (newMoneyVal)) + BigDecimal(newIncome) - BigDecimal(newExpense))
        //Update today money in database
        db.userDao().updateTodayMoney(todayMoneyVal.toDouble())

        var dollarAppendage = "$"

        //Change color of today's money to red if negative and green if positive
        if(todayMoneyVal < BigDecimal(0))
        {
            todayMoney.setTextColor(Color.RED)
            dollarAppendage = "-$"
        }
        else
        {
            todayMoney.setTextColor(Color.parseColor("#008000"))
        }

        //Displaying the money that is available today
        todayMoney.text = getString(R.string.main_message_todayMoney, dollarAppendage + todayMoneyVal.toString().removePrefix("-"))

        //Changing color and message to green and gaining if money per day is positive or red and losing if negative and displaying it
        if(newMoneyVal < BigDecimal(0))
        {
            newMoneyBox.setTextColor(Color.RED)
            newMoneyBox.text = getString(R.string.main_message_losing, "-$" + newMoneyVal.toString().removePrefix("-"))
        }
        else
        {
            newMoneyBox.setTextColor(Color.parseColor("#008000"))
            newMoneyBox.text = getString(R.string.main_message_gaining, "$" + newMoneyVal.toString())
        }

    }

    //Function used to force numbers to be formatted with two spots after decimal place
    private fun BigDecimal.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

}