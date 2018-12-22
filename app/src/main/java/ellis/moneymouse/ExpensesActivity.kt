package ellis.moneymouse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_expenses.*
import java.math.BigDecimal

class ExpensesActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                val intent = Intent(this, MainActivity::class.java)
                //intent.putExtra("income", value)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_income -> {
                val intent = Intent(this, IncomeActivity::class.java)
                //intent.putExtra("income", value)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_expenses -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expenses)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.selectedItemId = R.id.navigation_expenses

        val expPref = getSharedPreferences(
            getString(R.string.saved_expenses_key), Context.MODE_PRIVATE)

        val monthlyExp = expPref.getString(getString(R.string.saved_expenses_key), "0.00")

        if (monthlyExp == "0.00")
        {
            monthlyExpenses.setText("")
        }
        else
        {
            monthlyExpenses.setText(monthlyExp)
        }
    }

    override fun onPause() {
        super.onPause()


        if(monthlyExpenses.text.toString() == "")
        {
            monthlyExpenses.setText(R.string.zero_point_zero_zero)
        }

        val expPref = getSharedPreferences(
            getString(R.string.saved_expenses_key), Context.MODE_PRIVATE) ?: return
        with (expPref.edit()) {
            putString(getString(R.string.saved_expenses_key), BigDecimal(monthlyExpenses.text.toString()).format(2).toString())
            apply()
        }

        if(newExpenseBox.text.toString() == "0" || newExpenseBox.text.toString() == "" || newExpenseBox.text.toString() == "0.0" || newExpenseBox.text.toString() == "0.00")
        {

        }
        else
        {
            val newExpense1Pref = getSharedPreferences(
                getString(R.string.saved_newExpense_key), Context.MODE_PRIVATE) ?: return
            val newExpense: String? = newExpense1Pref.getString(getString(R.string.saved_newExpense_key), "0.00")

            val newExpense2Pref = getSharedPreferences(
                getString(R.string.saved_newExpense_key), Context.MODE_PRIVATE) ?: return
            with (newExpense2Pref.edit()) {
                putString(getString(R.string.saved_newExpense_key), (BigDecimal(newExpenseBox.text.toString()) + BigDecimal(newExpense)).format(2).toString())
                apply()
            }
        }

    }

    private fun BigDecimal.format(digits: Int) = java.lang.String.format("%.${digits}f", this)
}