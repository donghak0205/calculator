package fastcampus.aop.part2.chapter04

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.room.Room
import fastcampus.aop.part2.chapter04.model.History1

class MainActivity : AppCompatActivity() {


    private val expressionTextView: TextView by lazy {
        findViewById(R.id.expressionTextView)
    }

    private val resultTextView: TextView by lazy {
        findViewById(R.id.resultTextView)
    }

    private val historyLayout: View by lazy {
        findViewById(R.id.historyLayout)
    }

    private val historyLinearLayout: LinearLayout by lazy {
        findViewById(R.id.historyLinearLayout)
    }

    var isOperator = false
    var hasOperator = false

    lateinit var db: AppDatabase1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase1::class.java,
            "history1"
        ).build()

    }

    //Button Clicked
    fun buttonClicked(v: View) {
        when (v.id) {
            R.id.button0 -> numberButtonClicked("0")
            R.id.button1 -> numberButtonClicked("1")
            R.id.button2 -> numberButtonClicked("2")
            R.id.button3 -> numberButtonClicked("3")
            R.id.button4 -> numberButtonClicked("4")
            R.id.button5 -> numberButtonClicked("5")
            R.id.button6 -> numberButtonClicked("6")
            R.id.button7 -> numberButtonClicked("7")
            R.id.button8 -> numberButtonClicked("8")
            R.id.button9 -> numberButtonClicked("9")
            R.id.buttonModulo -> operatorButtonClicked("%")
            R.id.buttonMulti -> operatorButtonClicked("*")
            R.id.buttonDivider -> operatorButtonClicked("??")
            R.id.buttonPlus -> operatorButtonClicked("+")
            R.id.buttonMinus -> operatorButtonClicked("-")
        }
    }


    //ClearButton Clicked
    fun clearButtonClicked(v: View) {
        expressionTextView.text = ""
        resultTextView.text = ""
        isOperator = false
        hasOperator = false
    }

    private fun numberButtonClicked(number: String) {

        //isOperator????????? ???????????? ????????? ??????!
        if (isOperator) {
            expressionTextView.append(" ")
            //???????????? ??????????????? ?????? ????????????.
        }

        isOperator = false

        var expressionText = expressionTextView.text.split(" ")
        if (expressionText.isNotEmpty() && expressionText.last().length > 15) {
            Toast.makeText(this, "15??????????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show()
            return;
        } else if (expressionText.last().isEmpty() && number == "0") {
            Toast.makeText(this, "?????? 0??? ???????????? ????????????.", Toast.LENGTH_SHORT).show()
            return;
        }

        expressionTextView.append(number)
        resultTextView.text = calculateExpression()

    }

    private fun operatorButtonClicked(operator: String) {
        //????????? ????????? ??????
        //??? ??????????????? ????????? ??????

        if(expressionTextView.text.isEmpty()){
            return
        }

        when {
            isOperator -> {
                var text = expressionTextView.text.toString()
                expressionTextView.text = text.dropLast(1)+operator
            }
            hasOperator ->{ //????????? ???????????? ??????????????? ??????
                return
            }
            else -> {
                expressionTextView.append(" $operator")
            }
        }

        val ssb = SpannableStringBuilder(expressionTextView.text)
        ssb.setSpan(
            ForegroundColorSpan(Color.GREEN),
            expressionTextView.text.length-1,
            expressionTextView.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        expressionTextView.text = ssb
        isOperator = true
        hasOperator = true
    }


    //????????????
    private fun calculateExpression(): String? {

        var expressionText = expressionTextView.text.split(" ")
        if (hasOperator.not() && expressionText.size != 3) {
            return ""
        } else if (expressionText[0].isNumber().not() && expressionText[2].isNumber().not()) {
            return ""
        }

        var exp1 = expressionText[0].toBigInteger()
        var op = expressionText[1]
        var exp2 = expressionText[2].toBigInteger()

        return when (op) {
            "+" -> (exp1 + exp2).toString()
            "-" -> (exp1 - exp2).toString()
            "/" -> (exp1 / exp2).toString()
            "%" -> (exp1 % exp2).toString()
            "*" -> (exp1 * exp2).toString()
            else -> ""
        }

    }

    fun resultButtonClicked(v: View) {

        var expressionText = expressionTextView.text.toString()
        var resultText = resultTextView.text.toString()

        Thread(
            Runnable {
                db.historyDao1().insertHistory(
                    History1(
                        null,
                        expressionText,
                        resultText
                    )
                )
            }
        ).start()

        expressionTextView.text = ""
        expressionTextView.text = resultTextView.text

        resultTextView.text = ""
        hasOperator = false
        isOperator = false
    }

    fun historyButtonClicked(v: View) {

        historyLayout.isVisible = true
        historyLinearLayout.removeAllViews()


        //forEach??? ????????? ?????? ?????????
        Thread(Runnable {
            db.historyDao1().getAll().reversed().forEach {
                runOnUiThread {
                    val historyView =
                        LayoutInflater.from(this).inflate(R.layout.history_row, null, false)
                    historyView.findViewById<TextView>(R.id.expressionTextView).text = it.expression
                    historyView.findViewById<TextView>(R.id.resultTextView).text = "= ${it.result}"

                    historyLinearLayout.addView(historyView)
                }
            }
        }).start()
        //-> ???????????? ????????? ?????????????????? ????????????????????????
        // ????????? ?????? ?????? ???????????? ???????????????!!!!

        //TODO history ????????? ????????? ??????.
        //TODO ?????? ?????? ????????????
    }


    fun historyCloseButtonClicked(v: View) {
        historyLayout.isVisible = false
    }

    fun historyClearButtonClicked(v: View) {
        historyLinearLayout.removeAllViews()

        Thread(Runnable {
            db.historyDao1().deleteAll()
        }).start()
        //TODO history??? DB?????? ?????? ????????? ??????.
    }
}

private fun String.isNumber(): Boolean {
    return try {
        this.toBigInteger()
        true
    } catch (e: NumberFormatException) {
        false
    }
}
