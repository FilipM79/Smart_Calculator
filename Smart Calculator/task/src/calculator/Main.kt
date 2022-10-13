package calculator

import kotlin.system.exitProcess

fun main() {
    println("For calculating sum, input two integer numbers in one line, separated by space:")
    
    while (true) {
        print("> ")
        val inputString = readln()
        
        if (inputString == "/exit") {
            println("Bye!")
            exitProcess(0)
            
        } else if (inputString.isEmpty()) {
            print("> ")
            continue
            
        } else {
            val inputContainsEmptySpace = inputString.contains(" ")
    
            try {
                val x = inputString.substringBefore(" ").toInt()
                val y = inputString.substringAfter(" ").toInt()
        
                if (inputContainsEmptySpace) {
                    println(x + y)
                } else {
                    println(x)
                }
        
            } catch (e: NumberFormatException) {
                println("Invalid input. It works only with two integer numbers.")
            }
        }
    }
}
