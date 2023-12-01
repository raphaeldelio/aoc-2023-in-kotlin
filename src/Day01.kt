fun main() {
    fun part1(input: List<String>): Int {
        val numbers = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9")
        return input.sumOf { line ->
            val firstNumber = line.findAnyOf(numbers)?.second
            val lastNumber = line.findLastAnyOf(numbers)?.second
            val calibrationValue = firstNumber + lastNumber

            calibrationValue.toInt()
        }
    }

    fun part2(input: List<String>): Int {
        val digitsMap = mapOf(
            "one" to "1",
            "two" to "2",
            "three" to "3",
            "four" to "4",
            "five" to "5",
            "six" to "6",
            "seven" to "7",
            "eight" to "8",
            "nine" to "9",
        )
        val regexMap = Regex(digitsMap.keys.joinToString("|"))

        return input.sumOf { line ->
            val firstNumber = line.findAnyOf(digitsMap.keys + digitsMap.values)?.second
            val lastNumber = line.findLastAnyOf(digitsMap.keys + digitsMap.values)?.second
            val calibrationValue = (firstNumber + lastNumber).replace(regexMap) {
                digitsMap[it.value]!!
            }

            calibrationValue.toInt()
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 142)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
