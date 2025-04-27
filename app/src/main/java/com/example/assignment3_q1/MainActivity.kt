package com.example.assignment3_q1

import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var matrixAInputs: Array<Array<EditText>>
    private lateinit var matrixBInputs: Array<Array<EditText>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rowsAInput = findViewById<EditText>(R.id.rowsAInput)
        val colsAInput = findViewById<EditText>(R.id.colsAInput)
        val rowsBInput = findViewById<EditText>(R.id.rowsBInput)
        val colsBInput = findViewById<EditText>(R.id.colsBInput)
        val generateButton = findViewById<Button>(R.id.generateButton)
        val matrixATable = findViewById<TableLayout>(R.id.matrixA_table)
        val matrixBTable = findViewById<TableLayout>(R.id.matrixB_table)
        val operationSpinner = findViewById<Spinner>(R.id.operationSpinner)
        val calculateButton = findViewById<Button>(R.id.calculateButton)
        val resultTable = findViewById<TableLayout>(R.id.result_table)
        val resultLabel = findViewById<TextView>(R.id.resultLabel)

        val operations = arrayOf("Add", "Subtract", "Multiply", "Divide")
        operationSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, operations)

        generateButton.setOnClickListener {
            val rowsA = rowsAInput.text.toString().toIntOrNull() ?: 2
            val colsA = colsAInput.text.toString().toIntOrNull() ?: 2
            val rowsB = rowsBInput.text.toString().toIntOrNull() ?: 2
            val colsB = colsBInput.text.toString().toIntOrNull() ?: 2

            matrixAInputs = Array(rowsA) { Array(colsA) { EditText(this) } }
            matrixBInputs = Array(rowsB) { Array(colsB) { EditText(this) } }
            generateMatrixTable(matrixATable, matrixAInputs)
            generateMatrixTable(matrixBTable, matrixBInputs)
            resultTable.removeAllViews()
            resultLabel.visibility = View.GONE
            resultTable.visibility = View.GONE
        }

        calculateButton.setOnClickListener {
            try {
                val rowsA = rowsAInput.text.toString().toInt()
                val colsA = colsAInput.text.toString().toInt()
                val rowsB = rowsBInput.text.toString().toInt()
                val colsB = colsBInput.text.toString().toInt()
                val a = getMatrixFromInputs(matrixAInputs, rowsA, colsA)
                val b = getMatrixFromInputs(matrixBInputs, rowsB, colsB)
                val op = operationSpinner.selectedItem.toString()
                val result: Array<DoubleArray> = when (op) {
                    "Add" -> {
                        if (rowsA != rowsB || colsA != colsB) throw Exception("Addition requires same dimensions")
                        val resString = MatrixCalculatorJNI.addMatrix(flattenMatrix(a), flattenMatrix(b), rowsA, colsA)
                        parseMatrixString(resString)
                    }
                    "Subtract" -> {
                        if (rowsA != rowsB || colsA != colsB) throw Exception("Subtraction requires same dimensions")
                        val resString = MatrixCalculatorJNI.subtractMatrix(flattenMatrix(a), flattenMatrix(b), rowsA, colsA)
                        parseMatrixString(resString)
                    }
                    "Multiply" -> {
                        if (colsA != rowsB) throw Exception("Multiplication requires A's columns = B's rows")
                        val resString = MatrixCalculatorJNI.multiplyMatrix(flattenMatrix(a), flattenMatrix(b), rowsA, colsA, colsB)
                        parseMatrixString(resString)
                    }
                    "Divide" -> {
                        if (rowsA != rowsB || colsA != colsB) throw Exception("Division requires same dimensions")
                        val resString = MatrixCalculatorJNI.divideMatrix(flattenMatrix(a), flattenMatrix(b), rowsA, colsA)
                        parseMatrixString(resString)
                    }
                    else -> throw Exception("Invalid operation")
                }
                showResultMatrix(result, resultTable)
                resultLabel.visibility = View.VISIBLE
                resultTable.visibility = View.VISIBLE
            } catch (e: Exception) {
                resultLabel.visibility = View.VISIBLE
                resultTable.visibility = View.GONE
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun generateMatrixTable(table: TableLayout, inputs: Array<Array<EditText>>) {
        table.removeAllViews()
        val rows = inputs.size
        val cols = inputs[0].size
        for (i in 0 until rows) {
            val tr = TableRow(this)
            for (j in 0 until cols) {
                val et = EditText(this)
                et.layoutParams = TableRow.LayoutParams(120, TableRow.LayoutParams.WRAP_CONTENT)
                et.gravity = Gravity.CENTER
                et.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
                inputs[i][j] = et
                tr.addView(et)
            }
            table.addView(tr)
        }
    }

    private fun getMatrixFromInputs(inputs: Array<Array<EditText>>, rows: Int, cols: Int): Array<DoubleArray> {
        return Array(rows) { i ->
            DoubleArray(cols) { j ->
                inputs[i][j].text.toString().toDoubleOrNull() ?: 0.0
            }
        }
    }

    private fun showResultMatrix(result: Array<DoubleArray>, table: TableLayout) {
        table.removeAllViews()
        for (i in result.indices) {
            val tr = TableRow(this)
            for (j in result[i].indices) {
                val tv = TextView(this)
                tv.text = "%.2f".format(result[i][j])
                tv.gravity = Gravity.CENTER
                tv.setPadding(16, 8, 16, 8)
                tr.addView(tv)
            }
            table.addView(tr)
        }
    }

    // --- JNI Helper Functions ---

    // Flatten a 2D array to 1D
    private fun flattenMatrix(matrix: Array<DoubleArray>): DoubleArray {
        return matrix.flatMap { it.asList() }.toDoubleArray()
    }

    // Parse the JNI result string (e.g., "1 2;3 4") to Array<DoubleArray>
    private fun parseMatrixString(result: String): Array<DoubleArray> {
        if (result.startsWith("Error")) throw Exception(result)
        return result.split(";").map { row ->
            row.trim().split(" ").map { it.toDouble() }.toDoubleArray()
        }.toTypedArray()
    }
}
