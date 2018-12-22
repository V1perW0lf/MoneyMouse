package ellis.moneymouse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.activity_income.*
import java.math.BigDecimal

class IncomeActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_income -> {
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
        setContentView(R.layout.activity_income)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.selectedItemId = R.id.navigation_income

        newMoneyBox.requestFocus()

        val incomePref = getSharedPreferences(
            getString(R.string.saved_income_key), Context.MODE_PRIVATE
        )

        val monthlyInc = incomePref.getString(getString(R.string.saved_income_key), "0.00")

        if (monthlyInc == "0.00")
        {
            monthlyIncome.setText("")
        }
        else
        {
            monthlyIncome.setText(monthlyInc)
        }

    }

    override fun onPause() {
        super.onPause()

        if(monthlyIncome.text.toString() == "")
        {
            monthlyIncome.setText(R.string.zero_point_zero_zero)
        }


        val incomePref = getSharedPreferences(
            getString(R.string.saved_income_key), Context.MODE_PRIVATE) ?: return
        with (incomePref.edit()) {
            putString(getString(R.string.saved_income_key), BigDecimal(monthlyIncome.text.toString()).format(2).toString())
            apply()
        }

        if(newMoneyBox.text.toString() == "0" || newMoneyBox.text.toString() == "" || newMoneyBox.text.toString() == "0.0" || newMoneyBox.text.toString() == "0.00")
        {

        }
        else
        {
            val newMoney1Pref = getSharedPreferences(
                getString(R.string.saved_newMoney_key), Context.MODE_PRIVATE) ?: return
            val newMoney: String? = newMoney1Pref.getString(getString(R.string.saved_newMoney_key), "0")

            val newMoney2Pref = getSharedPreferences(
                getString(R.string.saved_newMoney_key), Context.MODE_PRIVATE) ?: return
            with (newMoney2Pref.edit()) {
                putString(getString(R.string.saved_newMoney_key), (BigDecimal(newMoneyBox.text.toString()) + BigDecimal(newMoney)).format(2).toString())
                apply()
            }
        }
    }

    private fun BigDecimal.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

}