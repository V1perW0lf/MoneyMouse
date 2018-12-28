package ellis.moneymouse

import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.SimpleAdapter
import kotlinx.android.synthetic.main.activity_expenses.*
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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

        val cal: Calendar = Calendar.getInstance()

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "MoneyMouseDB"
        ).allowMainThreadQueries().build()

        //var data = ArrayList<Map<String, String>>()

        var list: ArrayList<String> = arrayListOf()

        for(i in 1..db.newExpensesDao().getLastEid()) {
            var datum = HashMap<String, String>(2)
            //datum.put("expense", db.newExpensesDao().getLastTenExpenses(i).toString())
            //datum.put("date", db.newExpensesDao().getDate(i).toString())
            //data.add(datum)
            list.add(db.newExpensesDao().getLastTenExpenses(i).toString())
        }

        list.reverse()

        var adap: ArrayAdapter<String>
        adap = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        //adap = SimpleAdapter(this, data, android.R.layout.simple_list_item_2, Array<String>(0, {i -> ""}), IntArray(android.R.id.text1, {android.R.id.text2}))
        expensesList.adapter = adap

        monthlyExpensesBox.setText(BigDecimal(db.userDao().getMonthlyExpenses().toString()).format(2))

        if(monthlyExpensesBox.text.toString() == "0.0")
        {
            monthlyExpensesBox.setText("")
        }

        monthlyExpensesBox.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val monthlyExpensesVal = monthlyExpensesBox.text.toString()
                if(monthlyExpensesVal != "") {
                    db.userDao().updateMonthlyExpenses(monthlyExpensesVal.toDouble())
                }
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                return@setOnEditorActionListener true
            }
            false
        }

        newExpenseBox.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                val newExpenseVal = newExpenseBox.text.toString()

                if(newExpenseVal != "") {
                    db.userDao().updateNewExpense(db.userDao().getNewExpense() + newExpenseVal.toDouble())

                    val newExpenseDB = NewExpenses(
                        db.newExpensesDao().getLastEid() + 1,
                        newExpenseVal.toDouble(),
                        cal.get(Calendar.DATE).toString()
                    )
                    db.newExpensesDao().insertNewExpense(newExpenseDB)

                    list = arrayListOf()

                    for (i in 1..db.newExpensesDao().getLastEid()) {
                        list.add(BigDecimal(db.newExpensesDao().getLastTenExpenses(i)).format(2))
                    }

                    list.reverse()

                    adap = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
                    expensesList.adapter = adap

                    newExpenseBox.setText("")
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