package com.example.calculatorkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import net.objecthunter.exp4j.ExpressionBuilder
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvExpression.isSelected = true
        bt0.setOnClickListener { doSomething("0") }
        bt1.setOnClickListener { doSomething("1") }
        bt2.setOnClickListener { doSomething("2") }
        bt3.setOnClickListener { doSomething("3") }
        bt4.setOnClickListener { doSomething("4") }
        bt5.setOnClickListener { doSomething("5") }
        bt6.setOnClickListener { doSomething("6") }
        bt7.setOnClickListener { doSomething("7") }
        bt8.setOnClickListener { doSomething("8") }
        bt9.setOnClickListener { doSomething("9") }
        btDouble0.setOnClickListener { doSomething("00") }
        btDot.setOnClickListener { doSomething(".") }
        btClear.setOnClickListener { doSomething("clear") }
        btBackspace.setOnClickListener { doSomething("backspace") }
        btPercent.setOnClickListener { doSomething("%") }
        btDivide.setOnClickListener { doSomething("÷") }
        btMultiply.setOnClickListener { doSomething("×") }
        btMinus.setOnClickListener { doSomething("-") }
        btPlus.setOnClickListener { doSomething("+") }
        btEqual.setOnClickListener { doSomething("=") }
    }

    private fun doSomething(p0: String) {
        //Toast.makeText(this, tvExpression.text.toString(), Toast.LENGTH_LONG).show()
        if (tvExpression.text.toString().toIntOrNull() == 0) {
            tvExpression.text = ""
            findResult()
        }
        //if (tvExpression.text.toString().isEmpty())
        if (tvExpression.text.toString().isEmpty()) {
            when (p0) {
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "0" -> {
                    tvExpression.append(p0)
                    findResult()
                }
                "00" -> {
                    tvExpression.append("0")
                    findResult()
                }
                "-" -> {//**************************************************************************
                    tvExpression.append(p0)
                    findResult()
                }
                "." -> {
                    tvExpression.append("0.")
                    findResult()
                }
            }

        } else {

            when (p0) {
                "clear" -> {
                    tvExpression.text = ""
                    tvResult.text = ""
                }
                "backspace" -> {
                    tvExpression.text = tvExpression.text.toString()
                        .substring(0, tvExpression.text.toString().length - 1)
                    findResult()
                }
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "00" -> {
                    if ((!isNotLastOperatorDot(tvExpression.text.toString())) || isNotLastDigitZero(
                            tvExpression.text.toString()
                        )
                    ) {
                        tvExpression.append(p0)
                        findResult()
                    } else {
                        //remove 0 and append p0 255x0 --> 255x${p0}  for example: 255x0  --> 255x1
                        tvExpression.text = tvExpression.text.toString()
                            .substring(0, tvExpression.text.toString().length - 1)
                        if (p0 == "00") {
                            /*even no need just make tvExpression.append(p0) explicit of "00"
                             so that the output will not become 255x00 where x is multiplication
                             symbol whee is only used as an example*/
                            tvExpression.append("0")
                            findResult()
                        } else {
                            tvExpression.append(p0)
                            findResult()

                        }
                    }
                }
                "." -> {
                    if (isLastCharOperator(tvExpression.text.toString())) {
                        tvExpression.append("0.")
                    } else if (isNotLastOperatorDot(tvExpression.text.toString()) && isNotLastCharDot(
                            tvExpression.text.toString()
                        )
                    ) {
                        tvExpression.append(p0)
                    }
                }
                "%", "÷", "×", "+" -> {
                    if (isLastCharOperator(tvExpression.text.toString()) && tvExpression.text.toString() != "-" && isNotLastOperatorMinusWithAdjacentOperator(
                            tvExpression.text.toString()
                        )
                    ) {
                        tvExpression.text = tvExpression.text.toString()
                            .substring(0, tvExpression.text.toString().length - 1)
                        tvExpression.append(p0)
                    } else if (tvExpression.text.toString() != "-" && isNotLastOperatorMinusWithAdjacentOperator(
                            tvExpression.text.toString()
                        )
                    ) {
                        tvExpression.append(p0)
                    } else if (!isNotLastOperatorMinusWithAdjacentOperator(tvExpression.text.toString())) {
                        tvExpression.text = tvExpression.text.toString()
                            .substring(0, tvExpression.text.toString().length - 2)
                        tvExpression.append(p0)
                    }
                }
                "-" -> {
                    if (isNotLastCharMinus(tvExpression.text.toString()) && isNotLastCharDot((tvExpression.text.toString())) && !isLastCharPlus()) {
                        tvExpression.append(p0)
                    } else if (isLastCharPlus()) {
                        tvExpression.text = tvExpression.text.toString()
                            .substring(0, tvExpression.text.toString().length - 1)
                        tvExpression.append(p0)
                    }
                }
                "=" -> {
                    applyResult()
                }
            }
        }
    }

    private fun isNotLastOperatorMinusWithAdjacentOperator(p0: String): Boolean {
        val regex = Regex("(?<=\\D)-$")
        val output: String? = regex.find(p0)?.value
        return output == null
    }

    private fun applyResult() {
        try {
            val p0 = tvExpression.text.toString()
            var inputExpression = p0.replace('×', '*')
            inputExpression = inputExpression.replace('÷', '/')
            val expression = ExpressionBuilder(inputExpression).build()
            val result = expression.evaluate()
            val longResult = result.toLong()
            if (result == longResult.toDouble()) {
                tvResult.text = longResult.toString()
            } else {
                tvResult.text = result.toString()
            }
            tvExpression.text = tvResult.text
            tvResult.text = ""
        } catch (e: Exception) {
            Log.e("Exception ❣❣❣❣❣ ✌✌✌ ", "message" + e.message)
            if (e.message == "Expression can not be empty")
                tvResult.text = ""
        }

    }

    private fun isLastCharPlus(): Boolean {
        return tvExpression.text.toString().isNotEmpty() && tvExpression.text.toString()
            .takeLast(1) == "+"
    }

    private fun isNotLastDigitZero(p0: String): Boolean {
        /*
        * This is used to check weather the expression after operator contains 0
        * Like in Expression 255x0
        * it will return false and then the else statement will work
        * as we append 1 in a normal expression like 255x5  -->  255x51
        * but when we append 1 with 0 it should give output like 255x0  -->  255x1*/
        val regex = Regex("\\d+$")
        val output: String? = regex.find(p0)?.value
        return output?.toIntOrNull() != 0
    }

    private fun isLastCharOperator(p0: String): Boolean {
        val regex = Regex("\\D$")
        val output: String? = regex.find(p0)?.value
        return output != null
    }

    private fun isNotLastCharDot(p0: String): Boolean {
        val regex = Regex("\\.$")
        val output: String? = regex.find(p0)?.value
        return output == null
    }

    private fun isNotLastCharMinus(p0: String): Boolean {
        val regex = Regex("-$")
        val output: String? = regex.find(p0)?.value
        return output == null
    }

    private fun isNotLastOperatorDot(p0: String): Boolean {
        val regex = Regex("\\.(?:\\d)+$")
        val output: String? = regex.find(p0)?.value
        return output == null
    }

    private fun findResult() {
        try {
            val p0 = tvExpression.text.toString()
            var inputExpression = p0.replace('×', '*')
            inputExpression = inputExpression.replace('÷', '/')
            val expression = ExpressionBuilder(inputExpression).build()
            val result = expression.evaluate()
            val longResult = result.toLong()
            if (result == longResult.toDouble()) {
                tvResult.text = longResult.toString()
            } else {
                tvResult.text = result.toString()
            }
        } catch (e: Exception) {
            Log.e("Exception ❣❣❣❣❣ ✌✌✌ ", "message" + e.message)
            if (e.message == "Expression can not be empty")
                tvResult.text = ""
        }
    }

}
