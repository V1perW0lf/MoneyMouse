package ellis.moneymouse

import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.activity_income.*
import android.view.inputmethod.InputMethodManager
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

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "MoneyMouseDB"
        ).allowMainThreadQueries().build()

        monthlyIncomeBox.setText(BigDecimal(db.userDao().getMonthlyIncome().toString()).format(2))

        if(monthlyIncomeBox.text.toString() == "0.0")
        {
            monthlyIncomeBox.setText("")
        }

        monthlyIncomeBox.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val monthlyIncomeVal = monthlyIncomeBox.text.toString()
                if(monthlyIncomeVal != "") {
                    db.userDao().updateMonthlyIncome(monthlyIncomeVal.toDouble())
                }
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                return@setOnEditorActionListener true
            }
            false
        }

        newMoneyBox.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val newMoneyVal = newMoneyBox.text.toString()
                if(newMoneyVal != "") {
                    db.userDao().updateNewIncome(db.userDao().getNewIncome() + newMoneyVal.toDouble())
                    newMoneyBox.setText("")
                }
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                return@setOnEditorActionListener true
            }
            false
        }

    }

    private fun BigDecimal.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

}