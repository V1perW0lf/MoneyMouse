package ellis.moneymouse

import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
//import android.widget.ArrayAdapter
//import android.widget.SimpleAdapter
import kotlinx.android.synthetic.main.activity_expenses.*
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList
import android.widget.TextView
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import java.text.SimpleDateFormat


class ExpensesActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_income -> {
                val intent = Intent(this, IncomeActivity::class.java)
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

        val dateFormatter = SimpleDateFormat("EEE MMM d", Locale.US)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "MoneyMouseDB"
        ).allowMainThreadQueries().build()

        //var data = ArrayList<Map<String, String>>()

        var expenseList: ArrayList<String> = arrayListOf()
        var dateList : ArrayList<String> = arrayListOf()

        for(i in 1..db.newExpensesDao().getLastEid()) {
            //var datum = HashMap<String, String>(2)
            //datum.put("expense", db.newExpensesDao().getLastTenExpenses(i).toString())
            //datum.put("date", db.newExpensesDao().getDate(i).toString())
            //data.add(datum)
            expenseList.add("$" + BigDecimal(db.newExpensesDao().getLastTenExpenses(i)).format(2))
            dateList.add(db.newExpensesDao().getDate(i).toString())
        }

        expenseList.reverse()
        dateList.reverse()

        var adap: DataListAdapter
        adap = DataListAdapter(expenseList, dateList)
        //adap = ArrayAdapter(this, R.layout.money_mouse_list_item, R.id.dateText, list)
        var test : ListView = findViewById(R.id.expensesList)
        test.adapter = adap

        monthlyExpensesBox.setText(BigDecimal(db.userDao().getMonthlyExpenses().toString()).format(2))

        if(monthlyExpensesBox.text.toString() == "0.00")
        {
            monthlyExpensesBox.setText("")
        }

        monthlyExpensesBox.setOnEditorActionListener { v, actionId, _ ->
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

        newExpenseBox.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                val newExpenseVal = newExpenseBox.text.toString()

                if(newExpenseVal != "") {
                    db.userDao().updateNewExpense(db.userDao().getNewExpense() + newExpenseVal.toDouble())

                    val newExpenseDB = NewExpenses(
                        db.newExpensesDao().getLastEid() + 1,
                        newExpenseVal.toDouble(),
                        (dateFormatter.format(cal.time)).toString()
                    )
                    db.newExpensesDao().insertNewExpense(newExpenseDB)

                    expenseList = arrayListOf()
                    dateList = arrayListOf()

                    for (i in 1..db.newExpensesDao().getLastEid()) {
                        expenseList.add("$" + BigDecimal(db.newExpensesDao().getLastTenExpenses(i)).format(2))
                        dateList.add(db.newExpensesDao().getDate(i))
                    }

                    expenseList.reverse()
                    dateList.reverse()

                    //adap = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
                    adap = DataListAdapter(expenseList, dateList)
                    var test : ListView = findViewById(R.id.expensesList)
                    test.adapter = adap

                    newExpenseBox.setText("")
                }
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                return@setOnEditorActionListener true
            }
            false
        }

    }

    internal inner class DataListAdapter : BaseAdapter {
        var Expense: ArrayList<String>? = arrayListOf()
        var Date: ArrayList<String>? = arrayListOf()

        constructor() {
            Expense = arrayListOf()
            Date = arrayListOf()
        }

        constructor(text: ArrayList<String>?, text1: ArrayList<String>?) {
            Expense = text
            Date = text1
        }

        override fun getCount(): Int {
            // TODO Auto-generated method stub
            return Expense!!.size
        }

        override fun getItem(arg0: Int): Any? {
            // TODO Auto-generated method stub
            return null
        }

        override fun getItemId(position: Int): Long {
            // TODO Auto-generated method stub
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

            val inflater = layoutInflater
            val row: View
            row = inflater.inflate(R.layout.money_mouse_list_item, parent, false)
            val expense: TextView
            val date: TextView
            expense = row.findViewById(R.id.expenseText)
            date = row.findViewById(R.id.dateText)
            expense.text = Expense!![position]
            date.text = Date!![position]

            return row
        }
    }

    private fun BigDecimal.format(digits: Int) = java.lang.String.format("%.${digits}f", this)
}