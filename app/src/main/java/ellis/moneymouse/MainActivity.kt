package ellis.moneymouse

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
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

        val incomePref = getSharedPreferences(
            getString(R.string.saved_income_key), Context.MODE_PRIVATE) ?: return
        val monthlyInc: BigDecimal = incomePref.getString(getString(R.string.saved_income_key), "0.00").toBigDecimal()

        val expPref = getSharedPreferences(
            getString(R.string.saved_expenses_key), Context.MODE_PRIVATE) ?: return
        val monthlyExp: BigDecimal = expPref.getString(getString(R.string.saved_expenses_key), "0.00").toBigDecimal()

        val newMoneyPref = getSharedPreferences(
            getString(R.string.saved_newMoney_key), Context.MODE_PRIVATE) ?: return
        val newMoney: BigDecimal = newMoneyPref.getString(getString(R.string.saved_newMoney_key), "0.00").toBigDecimal()

        val newExpensePref = getSharedPreferences(
            getString(R.string.saved_newExpense_key), Context.MODE_PRIVATE) ?: return
        val newExpense: BigDecimal = newExpensePref.getString(getString(R.string.saved_newExpense_key), "0.00").toBigDecimal()

        //val temp = newMoney / (BigDecimal(31.0) - BigDecimal(Date().date))
        val newMoneyVal = (monthlyInc - monthlyExp) / BigDecimal(31.0)

        val todayMoneyVal = (BigDecimal(Date().date) * (newMoneyVal)) + newMoney - newExpense

        val todayMoneyValString = "$" + todayMoneyVal.toString()

        if(todayMoneyVal < BigDecimal(0))
        {
            todayMoney.setTextColor(Color.RED)
        }
        else
        {
            todayMoney.setTextColor(Color.parseColor("#008000"))
        }

        todayMoney.setText(todayMoneyValString)

        if(newMoneyVal < BigDecimal(0))
        {
            newMoneyBox.setTextColor(Color.RED)
            newMoneyBox.setText("Losing " + newMoneyVal.toString() + " per day")
        }
        else
        {
            newMoneyBox.setTextColor(Color.parseColor("#008000"))
            newMoneyBox.setText("Gaining " + newMoneyVal.toString() + " per day")
        }

        Log.d("The Date is: ", Date().date.toString())

    }

}