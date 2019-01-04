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
import java.util.*
import kotlin.collections.ArrayList
import android.widget.TextView
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import java.text.SimpleDateFormat

class ExpensesActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            //Start the MainActivity
            R.id.navigation_home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            //Start the IncomeActivity
            R.id.navigation_income -> {
                val intent = Intent(this, IncomeActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            //Do nothing when expense button is pressed
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

        expenses()
    }

    private fun expenses() {
        val cal: Calendar = Calendar.getInstance()

        //Format looks like this: Mon Jan 23
        val dateFormatter = SimpleDateFormat("EEE MMM d", Locale.US)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "MoneyMouseDB"
        ).allowMainThreadQueries().build()

        //Create and display expense list
        lists(db)

        //Set monthlyExpensesBox to what the user last input as their expenses and format it properly
        monthlyExpensesBox.setText(BigDecimal(db.userDao().getMonthlyExpenses().toString()).format(2))

        //If they have not entered an income or input 0.00 as their income, force box to be blank when starting activity
        if(monthlyExpensesBox.text.toString() == "0.00")
        {
            monthlyExpensesBox.setText("")
        }

        //Defines what happens after user presses the check or "enter" button to input their value
        monthlyExpensesBox.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //Store inputted value to database
                val monthlyExpensesVal = monthlyExpensesBox.text.toString()
                if(monthlyExpensesVal != "") {
                    db.userDao().updateMonthlyExpenses(monthlyExpensesVal.toDouble())
                }
                //Closes the number pad
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                return@setOnEditorActionListener true
            }
            false
        }

        //Defines what happens after user presses the check or "enter" button to input their value
        newExpenseBox.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                val newExpenseVal = newExpenseBox.text.toString()

                if(newExpenseVal != "" && BigDecimal(newExpenseVal).format(2) != "0.00") {

                    //Store inputted value to database
                    db.userDao().updateNewExpense(db.userDao().getNewExpense() + newExpenseVal.toDouble())

                    val newExpenseDB = NewExpenses(
                        db.newExpensesDao().getLastEid() + 1,
                        newExpenseVal.toDouble(),
                        (dateFormatter.format(cal.time)).toString()
                    )
                    db.newExpensesDao().insertNewExpense(newExpenseDB)

                    //Reset lists in order to show updated income list
                    lists(db)
                }
                //Reset newExpenseBox to blank
                newExpenseBox.setText("")

                //Closes the number pad
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun lists(db: AppDatabase) {

        val expenseList: ArrayList<String> = arrayListOf()
        val dateList : ArrayList<String> = arrayListOf()

        //Populate list view with recently entered expenses and dates for each income
        for(i in 1..db.newExpensesDao().getLastEid()) {
            expenseList.add("$" + BigDecimal(db.newExpensesDao().getNewExpenses(i)).format(2))
            dateList.add(db.newExpensesDao().getDate(i))
        }

        //Reverse lists so newest entries are viewed first
        expenseList.reverse()
        dateList.reverse()

        //Create custom adapter for list view; found at end of code
        val adapter: DataListAdapter
        adapter = DataListAdapter(expenseList, dateList)
        expensesListBox.adapter = adapter

    }

    internal inner class DataListAdapter : BaseAdapter {
        var expenseList: ArrayList<String>? = arrayListOf()
        var dateList: ArrayList<String>? = arrayListOf()

        constructor() {
            expenseList = arrayListOf()
            dateList = arrayListOf()
        }

        constructor(text: ArrayList<String>?, text1: ArrayList<String>?) {
            expenseList = text
            dateList = text1
        }

        override fun getCount(): Int {
            // TODO Auto-generated method stub
            return expenseList!!.size
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

            val row : View
            val inflater = layoutInflater

            if(convertView == null) {
                row = inflater.inflate(R.layout.money_mouse_expense_list, parent, false)
                val expense: TextView = row.findViewById(R.id.expenseText)
                val date: TextView = row.findViewById(R.id.dateText)
                expense.text = expenseList!![position]
                date.text = dateList!![position]
                return row
            }
            else {
                row = convertView
                val income: TextView = row.findViewById(R.id.expenseText)
                val date: TextView = row.findViewById(R.id.dateText)
                income.text = expenseList!![position]
                date.text = dateList!![position]
                return row
            }

        }

    }

    //Function used to force numbers to be formatted with two spots after decimal place
    private fun BigDecimal.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

}