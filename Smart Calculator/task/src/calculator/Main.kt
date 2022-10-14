package calculator

import kotlin.system.exitProcess

fun main() {
    println("For calculating sum, input integer numbers in one line, separated by space:")
    
    while (true) {
        val inputString = readln().trim()
    
        if (inputString.startsWith("/")) {
            checkCommands(inputString)
            
        } else if (inputString.isEmpty()) {
            continue
    
        } else {
            try {
                if (invalidExpressionCheck(inputString)) {
                    throw NumberFormatException()
                }
                
                val inputParts = decomposedInput(inputString)
                sum(inputParts)
        
            } catch (e: NumberFormatException) {
                println("Invalid expression")
            }
        }
    }
}
fun  decomposedInput(inputString: String): MutableList<String> {
    
    val listOfStrings = inputString.split(" ")
    val inputParts = mutableListOf<String>()
    var plusSigns = 0
    var minusSigns = 0
    var sign = ""
    
    for (string in listOfStrings) {
        try {
            inputParts.add(string.toInt().toString()) // adding numbers to list
            
        } catch (e: NumberFormatException) {
            for (char in string) {
                if (char == '+') {
                    plusSigns++
                } else if (char == '-') {
                    minusSigns++
                }
                
                sign = if (minusSigns > 0 && minusSigns % 2 == 0) {
                    "+"
                } else if (minusSigns == 1 || minusSigns % 2 != 0) {
                    "-"
                } else {
                    "+"
                }
            }
            
            inputParts.add(sign) // adding signs to list
            plusSigns = 0
            minusSigns = 0
        }
    }
    return inputParts
}
fun sum(inputParts: MutableList<String>) {
    
    val editedList = mutableListOf<Int>()
    
    for (part in inputParts.indices) { // adding numbers to editedList
        if (inputParts[part] != "+" && inputParts[part] != "-") {
                editedList.add(inputParts[part].toInt())
            
        } else if (inputParts[part] == "+") { // if the sign is '+', do nothing
            editedList.add(0)
            continue
            
        } else if (inputParts[part] == "-") { // if the sign is '-', add number with opposite sign to editedList
            editedList.add(-(inputParts[part + 1].toInt()))
            
        } else if (editedList[part] == inputParts[part].toInt()) { // if the number is already added, change it to '0'
            editedList[part] = 0
        }
    }
    
    for (part in inputParts.indices) { // remove numbers with changed sign
        if (part > 0 && inputParts[part] != "-" && inputParts[part] != "+"
            && inputParts[part].toInt() == -(editedList[part-1]))
            
            editedList[part] = 0
    }
    
    editedList.removeAll(listOf(0)) // remove all zeroes from editedList
    println(editedList.sum())
}
fun invalidExpressionCheck(inputString: String): Boolean {
    
    var invalidExpression = false
    val charNonGrata = "[!-):-~]".toRegex()
    val regex = "(-|\\++){2,}\\d".toRegex()
    val noSpaceRegex = "\\d(-|\\++)\\d".toRegex()
    val noSign = inputString.contains("(\\d)+(\\s)+(\\d)".toRegex())
    
    val wrongInput = inputString.contains(charNonGrata) || inputString.contains(regex)
            || inputString.contains(noSpaceRegex)

    if ( inputString.endsWith("-") || inputString.endsWith("+") || wrongInput || noSign) {
        
        invalidExpression = true
    }
    return invalidExpression
}
fun checkCommands(inputString: String) {
    when (inputString) {
        "/exit" -> {
            println("Bye!")
            exitProcess(0)
        }
        
        "/help" -> {
            println("The program calculates the sum of numbers")
        }
        
        else -> println("Unknown command")
    }
}