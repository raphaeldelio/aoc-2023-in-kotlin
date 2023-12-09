import java.util.*

fun processWinningGroups(line: String): Map<String, List<String>> {
    return line.split(": ")
        .last()
        .replace("|", "")
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .groupBy { it }
        .filter { it.value.size > 1 }
}

fun totalWinningNumbersDoubled(line: String): Int {
    return processWinningGroups(line)
        .keys
        .fold(0) { points, _ ->
            if (points == 0) 1 else points * 2
        }
}

fun totalWinningCards(line: String): Int {
    return processWinningGroups(line).size
}

fun processCard(queue: LinkedList<Pair<Int, String>>, cardIndex: Int, cardLine: String, input: List<String>) {
    val winningCards = totalWinningCards(cardLine)
    for (i in 1..winningCards) {
        if (cardIndex + i < input.size) {
            queue.add(Pair(cardIndex + i, input[cardIndex + i]))
        }
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            totalWinningNumbersDoubled(line)
        }
    }

    fun initializeQueue(input: List<String>): LinkedList<Pair<Int, String>> {
        val queue = LinkedList<Pair<Int, String>>()
        input.forEachIndexed { index, line ->
            queue.add(Pair(index, line))
        }
        return queue
    }


    fun part2(input: List<String>): Int {
        val queue = initializeQueue(input)

        var totalCards = 0

        while (queue.isNotEmpty()) {
            val (index, card) = queue.remove()
            totalCards++
            processCard(queue, index, card, input)
        }

        return totalCards
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
