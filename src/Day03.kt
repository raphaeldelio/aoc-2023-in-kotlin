enum class Direction{
    DTL, T, DTR, R, DBR, B, DBL, L
}

fun List<List<String>>.getElementInThe(direction: Direction, i: Int, j: Int): Char? {
    val iMinus1 = (i - 1).positiveOrZero()
    val jMinus1 = (j - 1).positiveOrZero()
    val iPlus1 = (i + 1).lessThanOrNull(this.size - 1)
    val jPlus1 = (j + 1).lessThanOrNull(this[i].size - 1)

    return when (direction) {
        Direction.DTL -> this[iMinus1][jMinus1][0]
        Direction.T -> this[iMinus1][j][0]
        Direction.DTR -> if (jPlus1 != null) this[iMinus1][jPlus1][0] else null
        Direction.R -> if (jPlus1 != null) this[i][jPlus1][0] else null
        Direction.DBR -> if (jPlus1 != null && iPlus1 != null) this[iPlus1][jPlus1][0] else null
        Direction.B -> if (iPlus1 != null) this[iPlus1][j][0] else null
        Direction.DBL -> if (iPlus1 != null) this[iPlus1][jMinus1][0] else null
        Direction.L -> this[i][jMinus1][0]
    }
}

fun Int.positiveOrZero() = if (this < 0) 0 else this
fun Int.lessThanOrNull(max: Int) = if (this <= max) this else null

fun Char.isSymbol() = !isDigit() && this != '.'

fun List<String>.toArray(): List<List<String>> {
    return fold(listOf()) { row, line ->
        row + listOf(line.map { it.toString() })
    }
}

fun getSurroundingElements(array: List<List<String>>, i: Int, j: Int
) = mapOf(
    Direction.DTL to array.getElementInThe(Direction.DTL, i, j),
    Direction.T to array.getElementInThe(Direction.T, i, j),
    Direction.DTR to array.getElementInThe(Direction.DTR, i, j),
    Direction.R to array.getElementInThe(Direction.R, i, j),
    Direction.DBR to array.getElementInThe(Direction.DBR, i, j),
    Direction.B to array.getElementInThe(Direction.B, i, j),
    Direction.DBL to array.getElementInThe(Direction.DBL, i, j),
    Direction.L to array.getElementInThe(Direction.L, i, j),
)

fun isAdjacentToASymbol(surroundingElements: Map<Direction, Char?>) =
    surroundingElements.any { (_, element) -> element != null && element.isSymbol() }

fun isANumberToTheRight(surroundingElements: Map<Direction, Char?>): Boolean {
    if (surroundingElements[Direction.R] == null) return false
    return surroundingElements[Direction.R]?.isDigit() == true
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.toArray().let {array ->
            var sum = 0
            var isAdjacentToASymbol = false
            var numberString = ""

            array.forEachIndexed { i, row ->
                row.forEachIndexed { j, _ ->
                    val char = array[i][j]

                    val surroundingElements = getSurroundingElements(array, i, j)

                    if (char[0].isDigit()) {
                        if (surroundingElements[Direction.L]?.isDigit() == false) {
                            numberString = ""
                            isAdjacentToASymbol = false
                        }

                        numberString += char
                        if (isAdjacentToASymbol(surroundingElements)) {
                            isAdjacentToASymbol = true
                        }

                        if (numberString.isNotBlank() && isAdjacentToASymbol && !isANumberToTheRight(surroundingElements)) {
                            sum += numberString.toInt()
                            numberString = ""
                            isAdjacentToASymbol = false
                        }
                    }

                }
            }

            sum
        }
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 10)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
