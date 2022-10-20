package calculator

import java.util.Stack
import kotlin.system.exitProcess

fun main() {
    val inputMap = mutableMapOf<String, Int>()
    
    while (true) { // looping until '/exit' command
        val inputString = readln().trim() // reading input string
        
        if (inputString.isEmpty()) {
            continue
        } else {
            ValidateInput().inputSorter(inputString, inputMap)
        }
    }
}
class ValidateInput {
    fun inputSorter(inputString: String, inputMap: MutableMap<String, Int>) {
        if (inputString.startsWith("/")) {
            checkCommands(inputString)
            
        } else if (inputString.contains("=")) {
            decouplingEquation(inputString, inputMap)
            
        } else if (!inputString.contains(" ")) {
            checkSoloInput(inputString, inputMap)
            
        } else if (inputString.contains(" ")) {
            inputContainsSpaces(inputString, inputMap)
        }
    }
    private fun inputContainsSpaces(inputString: String, inputMap: MutableMap<String, Int>) {
        val listFromString = inputString.split(" ").filterNot { it == "" }
        
        val sumStack = Stack<String>()
        val sumList = mutableListOf<String>()
        var inputError = false
        
        loop@ for (i in listFromString) {
            val part = i.trim()
            val partIsNumber = part.matches("([+|-])?(\\d+)".toRegex())
            val partIsVariable = part.matches("([+|-])?([a-zA-Z]+)".toRegex())
            val partIsSigns = part.matches("([+|-]+)".toRegex())
            val invalidPart = !partIsNumber && !partIsVariable && !partIsSigns
            
            if (invalidPart) {
                println("Invalid expression")
                inputError = true
                break@loop
                
            } else if (partIsNumber) {
                val numberSign = if (part.startsWith("-") || part.startsWith("+")) {
                    part.first().toString()
                } else ""
                
                val numberWithoutSign = part.removePrefix(numberSign)
                
                if (numberSign == "-") {
                    sumStack.push("$numberSign$numberWithoutSign")
                    sumList.add("$numberSign$numberWithoutSign")
                } else {
                    sumStack.push(numberWithoutSign)
                    sumList.add(numberWithoutSign)
                }
                
            } else if (partIsSigns) {
                var minus = 0
                for (char in part) {
                    if (char == '-') {
                        minus++
                    }
                }
                val sign = if (minus > 0 && minus % 2 != 0) "-" else "+"
                
                try {
                    if (sumStack.lastElement() == "+" || sumStack.lastElement() == "-") {
                        println("Invalid expression")
                        inputError = true
                        break@loop
        
                    } else {
                        sumStack.push(sign)
                        sumList.add(sign)
                    }
                    
                } catch (e: NoSuchElementException) {
                    println("Invalid expression")
                    break@loop
                }
                
            } else {  // if part is variable
                val variableSign = if (part.startsWith("-") || part.startsWith("+")) {
                    part.first().toString()
                } else ""
                
                val variableWithoutSign = part.removePrefix(variableSign)
                val existingVariable = inputMap.containsKey(variableWithoutSign)
                
                if (!existingVariable) {
                    println("Unknown variable")
                    inputError = true
                    break@loop
                    
                } else {
                    val fullVariable = when (variableSign) {
                        "-" -> "${-inputMap.getValue(variableWithoutSign)}"
                        "+" -> "${inputMap.getValue(variableWithoutSign)}"
                        else -> "${inputMap.getValue(part)}"
                    }
                    
                    sumStack.push(fullVariable)
                    sumList.add(fullVariable)
                }
            }
        }
        
        if (!inputError) {
            val sum = mutableListOf<Int>()
            var j = 0
            
            for (i in sumList) {
                if (j < sumList.size && sumList[j] != "-" && sumList[j] != "+") {
                    sum.add(sumList[j].toInt())
                    j++
                    
                } else if (j < sumList.size && sumList[j] == "-") {
                    sum.add(-sumList[j + 1].toInt())
                    j += 2
                    
                } else if (j < sumList.size && sumList[j] == "+") {
                    sum.add(sumList[j + 1].toInt())
                    j += 2
                }
            }
            if (sum.isNotEmpty()) {
                println("${sum.sum()}")
            }
        }
    }
    private fun checkSoloInput(inputString: String, inputMap: MutableMap<String, Int>) {
        
        val inputSign = if (inputString.startsWith("-") || inputString.startsWith("+")) {
            inputString.first().toString()
        } else ""
        
        val inputWithoutSign = inputString.removePrefix(inputSign)
        
        val inputIsNumber = inputWithoutSign.matches("(\\d+)".toRegex())
        val mixedInput = inputString.contains("([a-zA-Z]+)".toRegex()) && inputString.contains("(\\d+)".toRegex())
        val inputIsVariable = inputWithoutSign.matches("([a-zA-Z]+)".toRegex())
        val inputIsNumOrVar = inputIsNumber || inputIsVariable
        val inputIsExistingVar = inputIsVariable && inputMap.containsKey(inputWithoutSign)
        
        if (mixedInput) {
            println("Invalid identifier")
            
        } else if (!inputIsNumOrVar) {    // if inputString is not a number or a variable
            println("Invalid expression")
            
        } else if (inputIsVariable && !inputIsExistingVar) {    // if inputString is a variable, but it's not in inputMap
            println("Unknown variable")
            
        } else {
            if (inputIsNumber) {
                println(inputString.toInt())
                
            } else {
                if (inputSign == "-") {
                    println(-inputMap.getValue(inputWithoutSign))
                } else {
                    println(inputMap.getValue(inputWithoutSign))
                }
            }
        }
    }
    private fun decouplingEquation(inputString: String, inputMap: MutableMap<String, Int>) {
        val left = inputString.substringBefore("=").trim()
        val right = inputString.substringAfter("=").trim()
        
        val rightSign = if (right.startsWith("-") || right.startsWith("+")) {
            right.first().toString()
        } else ""
        
        val leftSign = if (left.startsWith("-") || left.startsWith("+")) {
            left.first().toString()
        } else ""
        
        val leftWithoutSign = left.removePrefix(leftSign)
        val rightWithoutSign = right.removePrefix(rightSign)
        
        val rightIsVariable = rightWithoutSign.matches("([a-zA-Z]+)".toRegex())
        val rightIsNumber = rightWithoutSign.matches("(\\d+)".toRegex())
        val rightIsVariableOrNumber = rightIsVariable || rightIsNumber
        val leftIsVariable = leftWithoutSign.matches("([a-zA-Z]+)".toRegex())
        
        if (!leftIsVariable) {
            println("invalid identifier")
            
        } else if (!rightIsVariableOrNumber)
            println("invalid assignment")
        
        else if (rightIsVariable && !inputMap.containsKey(rightWithoutSign)) {
            println("Unknown variable")
            
        } else {
            if (rightIsVariable) {   // if both sides are variables ...
                if (rightSign == "-" && inputMap.containsKey(rightWithoutSign)) {   // ... and right side has a minus sign
                    inputMap[leftWithoutSign] = -inputMap.getValue(rightWithoutSign)
                    
                } else if (rightSign == "+" && inputMap.containsKey(rightWithoutSign)) {  // ... and right has a plus sign
                    inputMap[leftWithoutSign] = inputMap.getValue(rightWithoutSign)
                    
                } else {   // ... and right side has no sign
                    if (inputMap.containsKey(right)) inputMap[left] = inputMap.getValue(right)
                }
                
            } else {    // if right side is (anything else) a number ...
                when (rightSign) {
                    "-" -> inputMap[leftWithoutSign] = -(rightWithoutSign.toInt())
                    "+" -> inputMap[leftWithoutSign] = (rightWithoutSign.toInt())
                    else -> inputMap[left] = (right.toInt())
                }
            }
        }
    }
    private fun checkCommands(inputString: String) {
        when (inputString) {
            "/exit" -> {
                println("Bye!"); exitProcess(0)
            }
            "/help" -> println("The program calculates the sum of numbers")
            else -> println("Unknown command")
        }
    }
}

