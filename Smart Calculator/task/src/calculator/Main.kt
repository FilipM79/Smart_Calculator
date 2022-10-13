package calculator

fun main() {
    
    print("For calculating sum, input two integer numbers in one line, separated by space:\n> ")
    val inputString = readln()
    val inputContainsEmptySpace = inputString.contains(" ")

    try {
        if (inputContainsEmptySpace) {
            val x = inputString.substringBefore(" ").toInt()
            val y = inputString.substringAfter(" ").toInt()
            println(x + y)
        } else {
            println("There is no space between inputs.")
        }
    } catch (e: NumberFormatException) {
        println("Invalid input. It works only with two integer numbers.")
    }
}
