package calculator

import kotlin.system.exitProcess

fun main() {
    
    val inputMap = mutableMapOf<String, Int>()
    
    while (true) { // looping until '/exit' command
        val inputString = readln().trim() // reading input string
        
        if (inputString.isEmpty()) {
            println("Starting main - string is empty.")
            continue
            
        } else if (inputString.startsWith("/")) {
            println("Starting main - check commands")
            checkCommands(inputString)
            
        } else {
    
            val input = ValidateInput(inputString, inputMap) // checking input
    
            val setVariable = input.validationMap["setVariable"] ?: false
            val getVariable = input.validationMap["getVariable"] ?: false
            val invalidExpression = input.validationMap["invalidExpression"] ?: false
            val invalidSecondVariable = input.validationMap["invalidSecondVariable"] ?: false
    
            if (setVariable) {
                println("Starting main - setVariable is true")
                inputMap.putAll(setVariable(inputString, inputMap))
        
            } else if (getVariable) {
                println("Starting main - getVariable is true")
                getVariable(inputMap, inputString, true, invalidSecondVariable)
        
            } else {
                println("Starting main else - both getVariable and SetVariable are false.")
                processingInput(inputString, inputMap, invalidExpression)
            }
        }
    }
}

data class ValidateInput(private val inputString: String, val inputMap: MutableMap<String, Int>) {
    var validationMap = mapOf<String, Boolean>()
    init {
        println("Class validate start")
        var validGetVariable = false
        var validSetVariable = false
        var checkCommands = false
        var validEqual = false
        var invalidIdentifier = false
        var invalidAssignment = false
        var stringForProcessing = false
        var invalidExpression = false
        var invalidSecondVariable = false
    
        if (inputString.startsWith("/")) { // if there is a command character
//            println("Validating: Input string starts with a command sign.")
            checkCommands = true
        
        } else if (inputString.contains("=")) { // if there is '=' in a string
            println("Validating: Input string contains an equal sign.")
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
            println("Validating: Input string contains a possible variable.")
            validGetVariable = true
    
        } else if ( inputString.endsWith("-")
            || inputString.endsWith("+")
            || inputString.contains("(-|\\++){2,}(\\s*)?\\S".toRegex()) // double sign with a digit
            || inputString.contains("(\\d+)([+|-]+)(\\d+)".toRegex()) // digits and sign with no space
            || inputString.contains("(\\w+)(\\s+)([+|-]+)?(\\w+)".toRegex()) // something with space but no sign
            || inputString.contains("(\\w+)([+|-]+)(\\s+)?(\\w+)?".toRegex()) // something with space but no sign
            || inputString.contains("[!-']".toRegex()) // input string contains special characters
            
        ) {
            println("Validating: Input string invalid expression is set to true.")
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
            println("Validating: Input string is for further processing.")
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
        println("Check for input errors started.")
        val validEqual = validationMap["validEqual"] ?: false
        val invalidIdentifier = validationMap["invalidIdentifier"] ?: false
        val invalidAssignment = validationMap["invalidAssignment"] ?: false
        val invalidExpression = validationMap["invalidExpression"] ?: false
        val invalidSecondVariable = validationMap["invalidSecondVariable"] ?: false
        
        if (invalidExpression) {
            println("Invalid expression")
            
        } else if (validEqual) {
            if (invalidIdentifier) {
                println("Invalid identifier")
                
            } else if (invalidAssignment) {
                println("Invalid assignment")
                
            } else if (invalidSecondVariable) {
                println("prc")
            }
        } else {
            println("No input errors found.")
        }
    }
}
fun checkCommands(inputString: String) {
    
    println("Check commands started")
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
    
    println("Get variable started.")
    if (!invalidSecondVariable) {
        if (inputString.startsWith("-")) {
            if (inputMap.containsKey(inputString.substringAfter("-"))) {
                if (getVariable) {
                    println("Returning a variable with opposite sign, because it has an additional minus.")
                    println(-inputMap.getValue(inputString.substringAfter("-")))
                }
                existingVariable = true
            
            } else {
                println("Unknown variable 1")
            }
        
        } else if (inputString.startsWith("+")) {
            if (inputMap.containsKey(inputString.substringAfter("+"))) {
                if (getVariable) {
                    println("Returning a variable with the same sign, because it has an additional plus.")
                    println(inputMap.getValue(inputString.substringAfter("+")))
                }
                existingVariable = true
            
            } else {
                println("Unknown variable 2")
            }
        
        } else {
            if (inputMap.containsKey(inputString)) {
                if (getVariable) {
                    println("Returning a variable.")
                    println(inputMap[inputString])
                }
                existingVariable = true
            
            } else {
                println("Unknown variable 3")
            }
        }
    }
    return existingVariable
}
fun setVariable(inputString: String, inputMap: MutableMap<String, Int>): MutableMap<String, Int> {
    println("Set variable started.")
    val key = inputString.substringBefore("=").trim()
    val value = inputString.substringAfter("=").trim()
    val valueIsNotDigit = !value.matches("\\d+".toRegex())
    var valueIsExistingVariable = false
    
    if (valueIsNotDigit && getVariable(inputMap, value)) {
        println("Setting a variable to an existing variable value")
        inputMap[key] = inputMap.getValue(value)
    } else {
        inputMap[key] = value.toInt()
        println("Setting a variable to a number")
    }
    
    return inputMap
}
fun processingInput(inputString: String, inputMap: MutableMap<String, Int>, invalidExpression: Boolean) {
    println("Processing input started.")
    val inputParts: List<String>
    val sumString = mutableListOf<String>()
    var sum = 0
    var variableInExpression = false
    var existingVariable = false
    
    fun checkingPartForMinus(part: String) {
        println("Part: $part")
        println("Checking part for minus")
        
        if (part.contains("-")) {
            println("Part: $part")
            println("Part contains minus")
            
            var minus = 0
            part.forEach { char ->
                if (char == '-') {
                    minus++
                }
            }
            
            println("Adding a minus to a string, because there is an odd number of minuses.")
            if (minus > 0 && minus % 2 != 0) {
                sumString.add("-")
            }
            
        } else {
            println("Part: $part")
            println("Part - else -> doesn't contain minus")
            sumString.add("+")
        }
    }
    fun makingSumFromString(sumString: MutableList<String>) {
        println("Making sum from string")
        val sumNum = mutableListOf<Int>() // making a Math expression from string ...
        var j = 0
        
        for (i in 0 until sumString.size) {
            if (j < sumString.size && sumString[j] != "-" && sumString[j] != "+") {
                println("converting a ${sumString[j]} to number, because it has no additional sign.")
                sumNum.add(sumString[j].toInt())
                j++
                
            } else if (j < sumString.size && sumString[j] == "-") {
                println("converting a ${sumString[j]} to opposite number, because it has a minus sign.")
                sumNum.add(-sumString[j + 1].toInt())
                j += 2
                
            } else if (j < sumString.size && sumString[j] == "+") {
                println("converting a ${sumString[j]} to the same number, because it has a plus sign.")
                sumNum.add(sumString[j + 1].toInt())
                j += 2
            }
        }
        println("Printing the sum ...")
        println(sumNum.sum())
    }
    
    if (!invalidExpression) {
        println("Processing - expression is not invalid.")
        
        if (inputString.contains(" ") && !inputString.contains("=")) { // if a part has no '=', but has ' '
            println("String doesn't contain equal sign, but contains spaces.")
            
            inputParts = inputString.split(" ")
            println("Input parts: $inputParts")
            
            loop@ for (part in inputParts) {
                
                if (part.matches("([-+])?([a-zA-Z]+)".toRegex())) { // if a part is a variable name
                    println("Part: $part")
                    println("Checking a part for variable 1")
                    existingVariable = getVariable(inputMap, part)
                    variableInExpression = true
                    
                    if (existingVariable) {
                        println("Part: $part")
                        println("Part is an existing variable")
                        
                        if (part.contains("-")) {
                            println("... that contains minus")
                            sum += -inputMap.getValue(part.substringAfter("-"))
                            sumString.add((-inputMap.getValue(part.substringAfter("-"))).toString())
                            
                        } else if (part.contains("+")) {
                            println("... that contains plus")
                            sum += inputMap.getValue(part.substringAfter("+"))
                            sumString.add((inputMap.getValue(part.substringAfter("+"))).toString())
                            
                        } else {
                            println("... without additional sign")
                            sum += inputMap.getValue(part)
                            sumString.add((inputMap.getValue(part).toString()))
                        }
                        
                    } else {
                        println("Part is a variable that doesn't exist")
                        break@loop
                    }
                    
                } else if (part.matches("([.?\\-+])+".toRegex())) { // if a part consists of only signs
                    println("Part: $part")
                    println("Part is a sign")
                    checkingPartForMinus(part)
                    
                } else if (part.matches("([-+])?(\\d+)".toRegex())) { // if a part is digits with or without a sign.
                    println("Part: $part")
                    println("Part is a digit -> adding it to sum ...")
                    sum += part.toInt()
                    sumString.add(part)
                }
            }
            
            if (!variableInExpression || existingVariable) {
                println("Making sum from string ...")
                makingSumFromString(sumString)
            }
            
        } else if (!inputString.contains(" ")) {
            println("String doesn't contain a space.")
            if (inputString.matches("([-+])?([a-zA-Z]+)".toRegex())) { // if a part is a variable name
                println("... and it is a variable")
                getVariable(inputMap, inputString)
            
            } else if (inputString.matches("([-+])?(\\d+)".toRegex())) { // if a part is digits with or without a sign.
                println("... and it is a digit")
                println(inputString)
            }
        }
    } else {
        println("Processing: expression boolean is invalid - aborting processing.")
    }
}


