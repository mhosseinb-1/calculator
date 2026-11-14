package com.example.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {

    private lateinit var display: TextView
    private var input = ""
    private var first: Double? = null
    private var op: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        display = findViewById(R.id.display)

        listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        ).forEachIndexed { digit, id -> click(id) { append(digit.toString()) } }

        mapOf(
            R.id.btnAdd to "+", R.id.btnSubtract to "-",
            R.id.btnMultiply to "×", R.id.btnDivide to "÷"
        ).forEach { (id, symbol) -> click(id) { setOperator(symbol) } }

        click(R.id.btnDot) { append(".") }
        click(R.id.btnEquals) { calculate() }
        click(R.id.btnClear) { clear() }
    }

    private fun click(id: Int, action: () -> Unit) =
        findViewById<Button>(id).setOnClickListener { action() }

    private fun clear() {
        input = ""; first = null; op = null
        updateDisplay()
    }


    private fun append(ch: String) {
        if (input.length >= 12 || (ch == "." && "." in input)) return
        if (op == null) first = null
        input = when {
            input.isEmpty() && ch == "." -> "0."
            input == "0" && ch != "." -> ch
            else -> input + ch
        }
        updateDisplay()
    }

    private fun setOperator(symbol: String) {
        if (op != null && input.isNotEmpty()) calculate()
        input.toDoubleOrNull()?.let { first = it; input = "" }
        if (first != null) {
            op = symbol
            updateDisplay()
        }
    }

    private fun calculate() {
        val a = first ?: return
        val o = op ?: return
        val b = input.toDoubleOrNull() ?: return

        if (o == "÷" && b == 0.0) {
            clear()
            display.text = "Error"
            return
        }
        first = when (o) {
            "+" -> a + b
            "-" -> a - b
            "×" -> a * b
            else -> a / b
        }
        input = ""; op = null
        updateDisplay()
    }

    private fun updateDisplay() {
        display.text = listOfNotNull(first?.let(::format), op, input.ifEmpty { null })
            .joinToString(" ")
            .ifEmpty { "0" }
    }


    private fun format(n: Double) =
        BigDecimal(n).setScale(10, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
}