package calculator

import kotlin.system.exitProcess

fun main() {
    
    val inputMap = mutableMapOf<String, Int>()
    
    while (true) { // looping until '/exit' command
        
        val inputString = readln().trim() // reading input string
        
        if (inputString.isEmpty()) continue
        inputChecker(inputString, inputMap)
        
//
//        if (inputString.isEmpty()) {
////            println("Starting main - string is empty.")
//            continue
//
//        } else if (inputString.startsWith("/")) {
////            println("Starting main - check commands")
//            checkCommands(inputString)
//
//        } else {
//
//            val input = ValidateInput(inputString, inputMap) // checking input
//
//            val setVariable = input.validationMap["setVariable"] ?: false
//            val getVariable = input.validationMap["getVariable"] ?: false
//            val invalidExpression = input.validationMap["invalidExpression"] ?: false
//            val invalidSecondVariable = input.validationMap["invalidSecondVariable"] ?: false
//
//            if (setVariable) {
////                println("Starting main - setVariable is true")
//                inputMap.putAll(setVariable(inputString, inputMap))
//
//            } else if (getVariable) {
////                println("Starting main - getVariable is true")
//                getVariable(inputMap, inputString, true, invalidSecondVariable)
//
//            } else {
////                println("Starting main else - both getVariable and SetVariable are false.")
//                processingInput(inputString, inputMap, invalidExpression)
//            }
//        }
    }
}

fun inputChecker(inputString: String, inputMap: MutableMap<String, Int>) {
    if (inputString.startsWith("/")) {
        checkCommands(inputString)
        
    } else if (inputString.contains("=")) {
        decoupleEquation(inputString, inputMap)
        
    } else if (!inputString.contains(" ")) {
        checkSoloInput(inputString, inputMap)
        
    } else if (inputString.contains(" ")) {
        inputContainsSpaces(inputString, inputMap)
    }
    
}

fun inputContainsSpaces (inputString: String, inputMap: MutableMap<String, Int>) {
    val listFromString = inputString.split(" ").filterNot { it -> it == "" }
    println(listFromString.joinToString())
    var sumString = ""
    val sumNum = mutableListOf<Int>()
    
    loop@for (i in listFromString) {
        val part = i.trim()
        val partIsNumber = part.matches("([+|-])?(\\d+)".toRegex())
        val partIsVariable = part.matches("([+|-])?([a-zA-Z]+)".toRegex())
        val partIsSigns = part.matches("([+|-]+)".toRegex())
        val invalidPart = !partIsNumber && !partIsVariable && !partIsSigns
        
        if (invalidPart) {
            println("Invalid expression")
            break@loop
            
        } else if (part.matches("\\s+".toRegex())) {
        
        } else if (partIsNumber) {
            val numberSign = if (part.startsWith("-") || part.startsWith("+")) {
                part.first().toString()
            } else ""
            
            val numberWithoutSign = part.removePrefix(numberSign)
            sumString += " $numberSign$numberWithoutSign "
            
//            sumNum.add("$numberSign$numberWithoutSign".toInt())
        
        } else if (partIsSigns) {
            var minus = 0
            for (char in part) {
                if (char == '-') {
                    minus ++
                }
            }
            val sign = if (minus > 0 && minus % 2 != 0) "-" else "+"
            sumString += sign
            
        } else {  // if part is variable
            val variableSign = if (part.startsWith("-") || part.startsWith("+")) {
                part.first().toString()
            } else ""
    
            val variableWithoutSign = part.removePrefix(variableSign)
            val existingVariable = inputMap.containsKey(variableWithoutSign)
            
            if (!existingVariable) {
                println("Unknown variable")
                break@loop
                
            } else {
                sumString += when (variableSign) {
                    "-" -> " ${-inputMap.getValue(variableWithoutSign)} "
                    "+" -> " ${inputMap.getValue(variableWithoutSign)} "
                    else -> " ${inputMap.getValue(part)} "
                }
            }
        }
    }
    println("SumString: $sumString")
}
fun checkSoloInput(inputString: String, inputMap: MutableMap<String, Int>) {
    
    val inputSign = if (inputString.startsWith("-") || inputString.startsWith("+")) {
        inputString.first().toString()
    } else ""
    
    val inputWithoutSign = inputString.removePrefix(inputSign)
    
    val inputIsNumber = inputWithoutSign.matches("(\\d+)".toRegex())
    val inputIsVariable = inputWithoutSign.matches("([a-zA-Z]+)".toRegex())
    val inputIsNumOrVar = inputIsNumber || inputIsVariable
    val inputIsExistingVar = inputIsVariable && inputMap.containsKey(inputWithoutSign)
    
    if (!inputIsNumOrVar) {    // if inputString is not a number or a variable
        println("Invalid expression")
        
    } else if (inputIsVariable && !inputIsExistingVar) {    // if inputString is a variable, but it's not in inputMap
        println("Unknown variable")
        
    } else {
        if (inputIsNumber) {
            println(inputString.toInt())
            
        } else {
            println("$inputSign${inputMap.getValue(inputWithoutSign)}".toInt())
        }
    }
}
fun decoupleEquation(inputString: String, inputMap: MutableMap<String, Int>) {
    
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
    
    if ( !leftIsVariable || !rightIsVariableOrNumber) {
        println("invalid expression")
        
    } else if (rightIsVariable && !inputMap.containsKey(rightWithoutSign)) {
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
data class ValidateInput(private val inputString: String, val inputMap: MutableMap<String, Int>) {
    var validationMap = mapOf<String, Boolean>()
    init {
//        println("Class validate start")
        var validGetVariable = false
        var validSetVariable = false
        var checkCommands = false
        var validEqual = false
        var invalidIdentifier = false
        var invalidAssignment = false
        var stringForProcessing = false
        var invalidExpression = false
        val invalidSecondVariable = false
    
        if (inputString.startsWith("/")) { // if there is a command character
//            println("Validating: Input string starts with a command sign.")
            checkCommands = true
        
        } else if (inputString.contains("=")) { // if there is '=' in a string
//            println("Validating: Input string contains an equal sign.")
            validEqual = true
            val key = inputString.substringBefore("=").trim()
            val value = inputString.substringAfter("=").trim()
            
            val valueIsLetters = value.matches("(\\s*)?([+|-])?[a-zA-Z]+(\\s*)?".toRegex())
            if (valueIsLetters) {
                invalidIdentifier = !key.matches("(\\s*)?[a-zA-Z]+(\\s*)?".toRegex()) // if left matches lett.
            }  else {
                invalidIdentifier = !key.matches("(\\s*)?[a-zA-Z]+(\\s*)?".toRegex()) // if left matches lett.
                invalidAssignment = !value.matches("(\\s*)?([+|-])?(\\d+)(\\s*)?".toRegex()) // right matches num
            }
            
            if (!invalidIdentifier && !invalidAssignment) {
                validSetVariable = true // if ok -> set variable
            }
            
            // checking for invalid input ...
        } else if (inputString.matches("(\\s*)?([+|-])?([a-zA-Z]+)(\\s*)?".toRegex())) { // if it resembles a variable
//            println("Validating: Input string contains a possible variable.")
            validGetVariable = true
    
        } else if ( inputString.endsWith("-")
            || inputString.endsWith("+")
            || inputString.contains("(\\d+)([+|-]+)(\\d+)".toRegex()) // digits and sign with no space
            || inputString.contains("(\\w+)(\\s+)([+|-]+)?(\\w+)".toRegex()) // something with space but no sign
            || inputString.contains("(\\w+)([+|-]+)(\\s+)?(\\w+)?".toRegex()) // something with space but no sign
            || inputString.contains("[!-']".toRegex()) // input string contains special characters
            
        ) {
//            println("Validating: Input string invalid expression is set to true.")
            invalidExpression = true
        
        } else if (inputString.contains(" ")) {
            val inputParts = inputString.split(" ")
            for (part in inputParts) {
                if (part.contains("\\d+".toRegex()) && part.contains("([a-zA-Z])".toRegex())) {
                    invalidExpression = true
                }
            }
    
        } else if (!inputString.contains(" ")) {
            if (inputString.contains("\\d+".toRegex()) && inputString.contains("([a-zA-Z])".toRegex())) {
                invalidExpression = true
            }
        
        } else {
//            println("Validating: Input string is for further processing.")
            stringForProcessing = true
        }
        
        validationMap = mapOf(
            "invalidIdentifier" to invalidIdentifier,
            "invalidAssignment" to invalidAssignment,
            "setVariable" to validSetVariable,
            "getVariable" to validGetVariable,
            "stringForDecomposing" to stringForProcessing,
            "checkCommands" to checkCommands,
            "validEqual" to validEqual,
            "invalidExpression" to invalidExpression,
            "invalidSecondVariable" to invalidSecondVariable
        )
        
        checkInputForErrors()
    }
    private fun checkInputForErrors() {
//        println("Check for input errors started.")
        val validEqual = validationMap["validEqual"] ?: false
        val invalidIdentifier = validationMap["invalidIdentifier"] ?: false
        val invalidAssignment = validationMap["invalidAssignment"] ?: false
        val invalidExpression = validationMap["invalidExpression"] ?: false
        
        if (invalidExpression) {
            println("Invalid expression")
            
        } else if (validEqual) {
            if (invalidIdentifier) {
                println("Invalid identifier")
                
            } else if (invalidAssignment) {
                println("Invalid assignment")
                
//            } else if (invalidSecondVariable) {
//                println("prc - a dje ti je druga!?")
            }
//        } else {
//            println("No input errors found.")
        }
    }
}
fun checkCommands(inputString: String) {
    
//    println("Check commands started")
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
} // checking for valid commands

fun getVariable(
    inputMap: MutableMap<String, Int>,
    inputString: String,
    getVariable: Boolean = false,
    invalidSecondVariable: Boolean = false
): Boolean {
    
    var existingVariable = false
    
//    println("Get variable started.")
    if (!invalidSecondVariable) {
        if (inputString.startsWith("-")) {
//            println("Ovde stize")
            if (inputMap.containsKey(inputString.substringAfter("-"))) {
//                println("sadrzi minus")
                if (getVariable) {
//                    println("Returning a variable with opposite sign, because it has an additional minus.")
                    println(-inputMap.getValue(inputString.substringAfter("-")))
                }
                existingVariable = true
            
            } else if (!inputString.trim().contains(" ")){
                println("Unknown variable")
                
            } else {
                println("Unknown variable")
            }
        
        } else if (inputString.startsWith("+")) {
            if (inputMap.containsKey(inputString.substringAfter("+"))) {
                if (getVariable) {
//                    println("Returning a variable with the same sign, because it has an additional plus.")
                    println(inputMap.getValue(inputString.substringAfter("+")))
                }
                existingVariable = true
            
            } else {
                println("Unknown variable")
            }
        
        } else {
            if (inputMap.containsKey(inputString)) {
                if (getVariable) {
//                    println("Returning a variable.")
                    println(inputMap[inputString])
                }
                existingVariable = true
            
            } else {
                println("Unknown variable")
            }
        }
    }
    return existingVariable
}
fun setVariable(inputString: String, inputMap: MutableMap<String, Int>): MutableMap<String, Int> {
//    println("Set variable started.")
    val key = inputString.substringBefore("=").trim()
    val value = inputString.substringAfter("=").trim()
    val valueIsNotDigit = !value.matches("([+|-])?\\d+".toRegex())
    
    if (valueIsNotDigit && getVariable(inputMap, value)) {
//        println("Setting a variable to an existing variable value")
        if (value.contains("-")) {
            inputMap[key] = -inputMap.getValue(value.substringAfter("-"))
        } else if (value.contains("+")) {
            inputMap[key] = inputMap.getValue(value.substringAfter("+"))
        } else {
            inputMap[key] = inputMap.getValue(value)
        }
        
    } else {
        try {
            inputMap[key] = value.toInt()
//        println("Setting a variable to a number")
        } catch (e: NumberFormatException) {
            print("")
        }
        
    }
    
    return inputMap
}
fun processingInput(inputString: String, inputMap: MutableMap<String, Int>, invalidExpression: Boolean) {
//    println("Processing input started.")
    val inputParts: List<String>
    val sumString = mutableListOf<String>()
    var sum = 0
    var variableInExpression = false
    var existingVariable = false
    
    fun checkingPartForMinus(part: String) {
//        println("Part: $part")
//        println("Checking part for minus")
        
        if (part.contains("-")) {
//            println("Part: $part")
//            println("Part contains minus")
            
            var minus = 0
            part.forEach { char ->
                if (char == '-') {
                    minus++
                }
            }
            
//            println("Adding a minus to a string, because there is an odd number of minuses.")
            if (minus > 0 && minus % 2 != 0) {
                sumString.add("-")
            }
            
        } else {
//            println("Part: $part")
//            println("Part - else -> doesn't contain minus")
            sumString.add("+")
        }
    }
    fun makingSumFromString(sumString: MutableList<String>) {
//        println("Making sum from string")
        val sumNum = mutableListOf<Int>() // making a Math expression from string ...
        var j = 0
        
        for (i in 0 until sumString.size) {
            if (j < sumString.size && sumString[j] != "-" && sumString[j] != "+") {
//                println("converting a ${sumString[j]} to number, because it has no additional sign.")
                sumNum.add(sumString[j].toInt())
                j++
                
            } else if (j < sumString.size && sumString[j] == "-") {
//                println("converting a ${sumString[j]} to opposite, because it has a minus sign.")
                sumNum.add(-sumString[j + 1].toInt())
                j += 2
                
            } else if (j < sumString.size && sumString[j] == "+") {
//                println("converting a ${sumString[j]} to the same, because it has a plus sign.")
                sumNum.add(sumString[j + 1].toInt())
                j += 2
            }
        }
//        println("Printing the sum ...")
        println(sumNum.sum())
    }
    
    if (!invalidExpression) {
//        println("Processing - expression is not invalid.")
        
        if (inputString.contains(" ") && !inputString.contains("=")) { // if a part has no '=', but has ' '
//            println("String doesn't contain equal sign, but contains spaces.")
            
            inputParts = inputString.split(" ")
//            println("Input parts: $inputParts")
            
            loop@ for (part in inputParts) {
                
                if (part.matches("([-+])?([a-zA-Z]+)".toRegex())) { // if a part is a variable name
//                    println("Part: $part")
//                    println("Checking a part for variable 1")
                    existingVariable = getVariable(inputMap, part)
                    variableInExpression = true
                    
                    if (existingVariable) {
//                        println("Part: $part")
//                        println("Part is an existing variable")
                        
                        if (part.contains("-")) {
//                            println("... that contains minus")
                            sum += -inputMap.getValue(part.substringAfter("-"))
                            sumString.add((-inputMap.getValue(part.substringAfter("-"))).toString())
                            
                        } else if (part.contains("+")) {
//                            println("... that contains plus")
                            sum += inputMap.getValue(part.substringAfter("+"))
                            sumString.add((inputMap.getValue(part.substringAfter("+"))).toString())
                            
                        } else {
//                            println("... without additional sign")
                            sum += inputMap.getValue(part)
                            sumString.add((inputMap.getValue(part).toString()))
                        }
                        
                    } else {
//                        println("Part is a variable that doesn't exist")
                        break@loop
                    }
                    
                } else if (part.matches("([.?\\-+])+".toRegex())) { // if a part consists of only signs
//                    println("Part: $part")
//                    println("Part is a sign")
                    checkingPartForMinus(part)
                    
                } else if (part.matches("([-+])?(\\d+)".toRegex())) { // if a part is digits with or without a sign.
//                    println("Part: $part")
//                    println("Part is a digit -> adding it to sum ...")
                    sum += part.toInt()
                    sumString.add(part)
                }
            }
            
            if (!variableInExpression || existingVariable) {
//                println("Making sum from string ...")
                makingSumFromString(sumString)
            }
            
        } else if (!inputString.contains(" ")) {
//            println("String doesn't contain a space.")
            if (inputString.matches("([-+])?([a-zA-Z]+)".toRegex())) { // if a part is a variable name
//                println("... and it is a variable")
                getVariable(inputMap, inputString)
            
            } else if (inputString.matches("([-+])?(\\d+)".toRegex())) { // if a part is digits with or without a sign.
//                println("... and it is a digit")
                println(inputString)
            }
        }
//    } else {
//        println("Processing: expression boolean is invalid - aborting processing.")
    }
}


