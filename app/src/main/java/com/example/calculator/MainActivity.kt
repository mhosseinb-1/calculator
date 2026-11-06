package com.example.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {

    private lateinit var display: TextView
    private var currentInput = ""
    private var firstNumber: Double? = null
    private var operator: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        display = findViewById(R.id.display)

        val digitButtons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )
        digitButtons.forEachIndexed { digit, id ->
            findViewById<Button>(id).setOnClickListener { appendDigit(digit.toString()) }
        }

        val operatorButtons = mapOf(
            R.id.btnAdd to "+",
            R.id.btnSubtract to "-",
            R.id.btnMultiply to "×",
            R.id.btnDivide to "÷"
        )
        operatorButtons.forEach { (id, symbol) ->
            findViewById<Button>(id).setOnClickListener { setOperator(symbol) }
        }

        findViewById<Button>(R.id.btnDot).setOnClickListener { appendDot() }
        findViewById<Button>(R.id.btnEquals).setOnClickListener { calculate() }
        findViewById<Button>(R.id.btnClear).setOnClickListener { clear() }
    }

    private fun clear() {
        currentInput = ""
        firstNumber = null
        operator = null
        updateDisplay()
    }

    private fun appendDigit(digit: String) {
        if (currentInput.length >= 12) return
        currentInput = if (currentInput == "0") digit else currentInput + digit
        updateDisplay()
    }

    private fun appendDot() {
        if ("." in currentInput) return
        currentInput = if (currentInput.isEmpty()) "0." else "$currentInput."
        updateDisplay()
    }

    private fun setOperator(symbol: String) {
        if (currentInput.isNotEmpty() && operator != null) calculate()
        if (currentInput.isNotEmpty()) {
            firstNumber = currentInput.toDouble()
            currentInput = ""
        }
        if (firstNumber != null) {
            operator = symbol
            updateDisplay()
        }
    }

    private fun calculate() {
        val first = firstNumber ?: return
        val op = operator ?: return
        val second = currentInput.toDoubleOrNull() ?: return

        if (op == "÷" && second == 0.0) {
            clear()
            display.text = "Error"
            return
        }

        firstNumber = when (op) {
            "+" -> first + second
            "-" -> first - second
            "×" -> first * second
            else -> first / second
        }
        currentInput = ""
        operator = null
        updateDisplay()
    }

    private fun updateDisplay() {
        val first = firstNumber
        val op = operator
        display.text = when {
            first != null && op != null -> "${format(first)} $op $currentInput".trim()
            currentInput.isNotEmpty() -> currentInput
            first != null -> format(first)
            else -> "0"
        }
    }

    private fun format(number: Double): String =
        BigDecimal(number)
            .setScale(10, RoundingMode.HALF_UP)
            .stripTrailingZeros()
            .toPlainString()
}