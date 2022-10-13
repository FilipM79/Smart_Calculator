package calculator

import kotlin.system.exitProcess

fun main() {
    println("For calculating sum, input integer numbers in one line, separated by space:")
    
    while (true) {
//        print("> ")
        val inputString = readln()
    
        if (inputString == "/exit") {
            println("Bye!")
            exitProcess(0)
        
        } else if (inputString.isEmpty()) {
//            print("> ")
            continue
        
        } else if (inputString == "/help") {
            println("The program calculates the sum of numbers")
            continue
    
        } else {
            
            try {
                val listOfStrings = inputString.split(" ")
                val listOfNumsToSum = mutableListOf<Int>()
                
                for (string in listOfStrings) {
                    listOfNumsToSum.add(string.toInt())
                }
                println(listOfNumsToSum.sum())
        
            } catch (e: NumberFormatException) {
                println("Invalid input.")
            }
        }
    }
}
