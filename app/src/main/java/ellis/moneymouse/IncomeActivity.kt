package ellis.moneymouse

import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.activity_income.*
import android.view.inputmethod.InputMethodManager
import android.widget.BaseAdapter
import android.widget.TextView
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

class IncomeActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            //Start the MainActivity
            R.id.navigation_home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            //Do nothing when income button is pressed
            R.id.navigation_income -> {
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
        setContentView(R.layout.activity_income)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.selectedItemId = R.id.navigation_income

        //Request focus here because it wouldn't default to it for some reason
        //And because it will be used more than monthlyIncomeBox
        newMoneyBox.requestFocus()

        income()
    }

    private fun income() {
        val cal: Calendar = Calendar.getInstance()

        //Format looks like this: Mon Jan 23
        val dateFormatter = SimpleDateFormat("EEE MMM d", Locale.US)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "MoneyMouseDB"
        ).allowMainThreadQueries().build()

        var incomeList: ArrayList<String> = arrayListOf()
        var dateList : ArrayList<String> = arrayListOf()

        //Populate list view with recently entered incomes and dates for each income
        for(i in 1..db.newIncomeDao().getLastEid()) {
            incomeList.add("$" + BigDecimal(db.newIncomeDao().getNewIncome(i)).format(2))
            dateList.add(db.newIncomeDao().getDate(i))
        }

        //Reverse lists so newest entries are viewed first
        incomeList.reverse()
        dateList.reverse()

        //Create custom adapter for list view; found at end of code
        var adapter: DataListAdapter
        adapter = DataListAdapter(incomeList, dateList)
        incomeListBox.adapter = adapter

        //Set monthlyIncomeBox to what the user last input as their income and format it properly
        monthlyIncomeBox.setText(BigDecimal(db.userDao().getMonthlyIncome().toString()).format(2))

        //If they have not entered an income or input 0.00 as their income, force box to be blank when starting activity
        if(monthlyIncomeBox.text.toString() == "0.00")
        {
            monthlyIncomeBox.setText("")
        }

        //Defines what happens after user presses the check or "enter" button to input their value
        monthlyIncomeBox.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //Store inputted value to database
                val monthlyIncomeVal = monthlyIncomeBox.text.toString()
                if(monthlyIncomeVal != "") {
                    db.userDao().updateMonthlyIncome(monthlyIncomeVal.toDouble())
                }
                //Closes the number pad
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                return@setOnEditorActionListener true
            }
            false
        }

        //Defines what happens after user presses the check or "enter" button to input their value
        newMoneyBox.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                val newMoneyVal = newMoneyBox.text.toString()
                if(newMoneyVal != "" && BigDecimal(newMoneyVal).format(2) != "0.00") {

                    //Store inputted value to database
                    db.userDao().updateNewIncome(db.userDao().getNewIncome() + newMoneyVal.toDouble())

                    val newIncomeDB = NewIncome(
                        db.newIncomeDao().getLastEid() + 1,
                        newMoneyVal.toDouble(),
                        (dateFormatter.format(cal.time)).toString()
                    )
                    db.newIncomeDao().insertNewIncome(newIncomeDB)

                    //Reset lists in order to show updated income list
                    incomeList = arrayListOf()
                    dateList = arrayListOf()

                    for(i in 1..db.newIncomeDao().getLastEid()) {
                        incomeList.add("$" + BigDecimal(db.newIncomeDao().getNewIncome(i)).format(2))
                        dateList.add(db.newIncomeDao().getDate(i))
                    }

                    incomeList.reverse()
                    dateList.reverse()

                    adapter = DataListAdapter(incomeList, dateList)
                    incomeListBox.adapter = adapter
                }

                //Reset newMoneyBox to blank
                newMoneyBox.setText("")

                //Closes the number pad
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                return@setOnEditorActionListener true
            }
            false
        }
    }

    internal inner class DataListAdapter : BaseAdapter {
        var incomeList: ArrayList<String>? = arrayListOf()
        var dateList: ArrayList<String>? = arrayListOf()

        constructor() {
            incomeList = arrayListOf()
            dateList = arrayListOf()
        }

        constructor(text: ArrayList<String>?, text1: ArrayList<String>?) {
            incomeList = text
            dateList = text1
        }

        override fun getCount(): Int {
            // TODO Auto-generated method stub
            return incomeList!!.size
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
                row = inflater.inflate(R.layout.money_mouse_income_list, parent, false)
                val income: TextView = row.findViewById(R.id.incomeText)
                val date: TextView = row.findViewById(R.id.dateText)
                income.text = incomeList!![position]
                date.text = dateList!![position]
                return row
            }
            else {
                row = convertView
                val income: TextView = row.findViewById(R.id.incomeText)
                val date: TextView = row.findViewById(R.id.dateText)
                income.text = incomeList!![position]
                date.text = dateList!![position]
                return row
            }

        }

    }

    //Function used to force numbers to be formatted with two spots after decimal place
    private fun BigDecimal.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

}