package ellis.moneymouse

import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
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

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "MoneyMouseDB"
        ).allowMainThreadQueries().build()

        monthlyExpensesBox.setText(BigDecimal(db.userDao().getMonthlyExpenses().toString()).format(2))

        if(monthlyExpensesBox.text.toString() == "0.0")
        {
            monthlyExpensesBox.setText("")
        }

        monthlyExpensesBox.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val monthlyExpensesVal = monthlyExpensesBox.text.toString()
                db.userDao().updateMonthlyExpenses(monthlyExpensesVal.toDouble())
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                return@setOnEditorActionListener true
            }
            false
        }

        newExpenseBox.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val newExpenseVal = newExpenseBox.text.toString()
                db.userDao().updateNewExpense(db.userDao().getNewExpense() + newExpenseVal.toDouble())
                newExpenseBox.setText("")
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                return@setOnEditorActionListener true
            }
            false
        }

    }

    private fun BigDecimal.format(digits: Int) = java.lang.String.format("%.${digits}f", this)
}