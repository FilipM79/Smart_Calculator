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
            ValidateInput(inputString, inputMap).inputSorter()
        }
    }
}
class ValidateInput(_inputString: String, _inputMap: MutableMap<String, Int>) {
    private val inputString = _inputString
    private val inputMap = _inputMap
    private var editedInputString = ""
    
    fun inputSorter() {
        if (inputString.startsWith("/")) {
            checkCommands()
        } else if (inputString.contains("=")) {
            decouplingEquation()
        } else if (!inputString.contains(" ")) {
            checkSoloInput()
        } else if (inputString.contains(" ")) {
            if (validExpression()) {
                val postfix = infixToPostfix()
                calculateExpression(postfix)
            }
        }
    }
    private fun checkCommands() {
        when (inputString) {
            "/exit" -> {
                println("Bye!"); exitProcess(0)
            }
            "/help" -> println("The program calculates the sum of numbers")
            else -> println("Unknown command")
        }
    }
    private fun decouplingEquation() {
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
    private fun checkSoloInput() {
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
    private fun validExpression(): Boolean {
        val firstString = inputString.replace("\\(".toRegex(), " ( ")
        val secondString = firstString.replace("\\)".toRegex(), " ) ")
        val listFromString = secondString.split(" ").filterNot { it == "" }
        
        var validInput = true
        
        loop@ for (i in listFromString) {
            
            if (inputString.endsWith("-") || inputString.endsWith("+")) {
                println("Invalid expression")
                validInput = false
                break@loop
            }
            
            val part = i.trim()
            val partIsNumber = part.matches("([+|-])?(\\d+)".toRegex())
            val partIsVariable = part.matches("([+|-])?([a-zA-Z]+)".toRegex())
            val partIsSigns = part.matches("([+|\\-*/]+)".toRegex())
            val brackets = part.matches("([)|(])".toRegex())
            val invalidPart = !partIsNumber && !partIsVariable && !partIsSigns && !brackets
            
            if (invalidPart) {
                println("Invalid expression")
                validInput = false
                break@loop
                
            } else if (partIsNumber) {
                val numberSign = if (part.startsWith("-") || part.startsWith("+")) {
                    part.first().toString()
                } else ""
                
                val numberWithoutSign = part.removePrefix(numberSign)
                
                editedInputString += if (numberSign == "-") {
                    "$numberSign$numberWithoutSign "
                } else "$numberWithoutSign "
                
            } else if (partIsSigns) {
                var sign: String
                var minus = 0
                
                sign = if (part.matches("([+|-])+".toRegex())) {
                    for (char in part) if (char == '-') minus++
                    if (minus > 0 && minus % 2 != 0) "-" else "+"
                } else part
                
                try {
                    if (editedInputString.last() == '+' || editedInputString.last() == '-') {
                        validInput = false
                        println("Invalid expression")
                        break@loop
                    } else editedInputString += "$sign "
                    
                } catch (e: NoSuchElementException) {
                    validInput = false
                    println("Invalid expression")
                    break@loop
                }
                
            } else if (partIsVariable) {  // if part is variable
                val variableSign = if (part.startsWith("-") || part.startsWith("+")) {
                    part.first().toString()
                } else ""
                
                val variableWithoutSign = part.removePrefix(variableSign)
                val existingVariable = inputMap.containsKey(variableWithoutSign)
                
                if (!existingVariable) {
                    validInput = false
                    println("Unknown variable")
                    break@loop
                    
                } else {
                    val fullVariable = when (variableSign) {
                        "-" -> "${-inputMap.getValue(variableWithoutSign)}"
                        "+" -> "${inputMap.getValue(variableWithoutSign)}"
                        else -> "${inputMap.getValue(part)}"
                    }
                    editedInputString += "$fullVariable "
                }
                
            } else {
                editedInputString += "$part "
            }
        }
        return validInput
    }
    private fun infixToPostfix(): String {
        val listFromString = editedInputString.split(" ").filterNot { it == "" }
        val stack = Stack<String>()
        var postfix = ""
        
        for (i in listFromString) {
            val part = i.trim()
            
            val partIsNumber = part.matches("([+|-])?(\\d+)".toRegex())
            val partIsVariable = part.matches("([+|-])?([a-zA-Z]+)".toRegex())
            val lowerSigns = part.matches("([-|+])".toRegex())
            val higherSigns = part.matches("([/|*])".toRegex())
            val signs = part.matches("([+|\\-*/])".toRegex())
            val openBracket = part.matches("\\(".toRegex())
            val closedBracket = part.matches("\\)".toRegex())
            
            if (partIsNumber || partIsVariable ) { // 2.
                postfix += "$part "
                
            } else if (openBracket) {  // 3.
                stack.push(part)
                
            } else if (closedBracket) {  // 4.
                while (stack.isNotEmpty() && stack.lastElement() != "(") {
                    val temp = stack.pop()
                    postfix += "$temp "
                }
                stack.pop()
                
            } else if (signs) {  // 5.
                if (lowerSigns) { // 5.1
                    while (stack.isNotEmpty() && stack.lastElement().matches("([+|\\-*/])".toRegex())) {
                        val temp = stack.pop()
                        postfix += "$temp "
                    }
                    stack.push(part)
                }
                
                if (higherSigns) { // 5.2
                    while (stack.isNotEmpty() && stack.lastElement().matches("([/|*])".toRegex())) {
                        val temp = stack.pop()
                        postfix = "$temp "
                    }
                    stack.push(part)
                }
                
                if (stack.lastElement() == "(") { // 5.3
                    stack.push(part)
                }
            }
        }
        
        while (stack.isNotEmpty()) {
            val temp = stack.pop()
            postfix += "$temp "
        }
        
        return postfix
    }
    private fun calculateExpression(postfix: String) {
        val listFromString = postfix.split(" ")
        val stack = Stack<Int>()
        
        loop@for (i in listFromString) {
            val part = i.trim()
            
            val partIsNumber = part.matches("([+|-])?(\\d+)".toRegex())
            val partIsVariable = part.matches("([+|-])?([a-zA-Z]+)".toRegex())
            val signs = part.matches("([+|\\-*/])".toRegex())
            
            if (partIsNumber) {
                stack.push(part.toInt())
                
            } else if (partIsVariable) {
                
                val variableSign = if (part.startsWith("-") || part.startsWith("+")) {
                    part.first().toString()
                } else ""
                
                val variableWithoutSign = part.removePrefix(variableSign)
                val existingVariable = inputMap.containsKey(variableWithoutSign)
                
                if (!existingVariable) {
                    println("Unknown variable")
                    break@loop
                    
                } else if (variableSign == "-") {
                    stack.push(-inputMap.getValue(variableWithoutSign))
                    
                } else {
                    stack.push(inputMap.getValue(variableWithoutSign))
                }
                
            } else if (signs) {
                val secondOperand = if (stack.isNotEmpty()) stack.pop() else 0
                val firstOperand = if (stack.isNotEmpty()) stack.pop() else 0
                
                when (part) {

                    "+" -> stack.push(firstOperand + secondOperand)
                    "-" -> stack.push(firstOperand - secondOperand)
                    "*" -> stack.push(firstOperand * secondOperand)
                    "/" -> stack.push(firstOperand / secondOperand)
                }
            }
        }
        println(stack.lastElement())
    }
}