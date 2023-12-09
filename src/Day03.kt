enum class Direction{
    DTL, T, DTR, R, DBR, B, DBL, L
}

data class Point(val i: Int, val j: Int, val value: String = "") {
    constructor(i: Int, j: Int, value: Char) : this(i, j, value.toString())

    fun isDigit() = value[0].isDigit()
    fun isSymbol() = !isDigit() && value != "."

    private fun getElementInThe(direction: Direction, array: List<List<Char>>): Point? {
        val iMinus1 = (i - 1).positiveOrNull()
        val jMinus1 = (j - 1).positiveOrNull()
        val iPlus1 = (i + 1).lessThanOrNull(array.size - 1)
        val jPlus1 = (j + 1).lessThanOrNull(array[i].size - 1)

        return when (direction) {
            Direction.DTL -> if (iMinus1 != null && jMinus1 != null) Point(iMinus1, jMinus1, array[iMinus1][jMinus1]) else null
            Direction.T -> if (iMinus1 != null) Point(iMinus1, j, array[iMinus1][j]) else null
            Direction.DTR -> if (iMinus1 != null && jPlus1 != null) Point(iMinus1, jPlus1, array[iMinus1][jPlus1]) else null
            Direction.R -> if (jPlus1 != null) Point(i, jPlus1, array[i][jPlus1]) else null
            Direction.DBR -> if (jPlus1 != null && iPlus1 != null) Point(iPlus1, jPlus1, array[iPlus1][jPlus1]) else null
            Direction.B -> if (iPlus1 != null) Point(iPlus1, j, array[iPlus1][j]) else null
            Direction.DBL -> if (iPlus1 != null && jMinus1 != null) Point(iPlus1, jMinus1, array[iPlus1][jMinus1]) else null
            Direction.L -> if (jMinus1 != null) Point(i, jMinus1, array[i][jMinus1]) else null
        }
    }

    fun getSurroundingElements(array: List<List<Char>>) = mapOf(
        Direction.DTL to getElementInThe(Direction.DTL, array),
        Direction.T to getElementInThe(Direction.T, array),
        Direction.DTR to getElementInThe(Direction.DTR, array),
        Direction.R to getElementInThe(Direction.R, array),
        Direction.DBR to getElementInThe(Direction.DBR, array),
        Direction.B to getElementInThe(Direction.B, array),
        Direction.DBL to getElementInThe(Direction.DBL, array),
        Direction.L to getElementInThe(Direction.L, array),
    )

    fun getAdjacentNumbers(array: List<List<Char>>): List<Point> {
        val surroundingElements = this.getSurroundingElements(array)
        val numberTop = surroundingElements[Direction.T]?.getNumbersToBothSides(array)
        val numberBottom = surroundingElements[Direction.B]?.getNumbersToBothSides(array)
        val numberLeft = surroundingElements[Direction.L]?.getNumbersToTheLeft(array)
        val numberRight = surroundingElements[Direction.R]?.getNumbersToTheRight(array)

        val numberTopLeft = if (numberTop == null) surroundingElements[Direction.DTL]?.getNumbersToTheLeft(array) else null
        val numberTopRight = if (numberTop == null) surroundingElements[Direction.DTR]?.getNumbersToTheRight(array) else null
        val numberBottomLeft = if (numberBottom == null) surroundingElements[Direction.DBL]?.getNumbersToTheLeft(array) else null
        val numberBottomRight = if (numberBottom == null) surroundingElements[Direction.DBR]?.getNumbersToTheRight(array) else null

        return listOfNotNull(
            numberTop,
            numberBottom,
            numberLeft,
            numberRight,
            numberTopLeft,
            numberTopRight,
            numberBottomLeft,
            numberBottomRight,
        )
    }

    private fun getNumbersToTheRight(array: List<List<Char>>): Point? {
        if (value == ".") return null

        val pointToTheRight = this.getElementInThe(Direction.R, array)

        // Base case to stop recursion
        if (pointToTheRight == null || !pointToTheRight.isDigit()) {
            return this
        }

        return this.copy(
            i = pointToTheRight.i,
            j = pointToTheRight.j,
            value = value + pointToTheRight.getNumbersToTheRight(array)?.value
        )
    }

    private fun getNumbersToTheLeft(array: List<List<Char>>): Point? {
        if (value == ".") return null

        val pointToTheLeft = this.getElementInThe(Direction.L, array)

        if (pointToTheLeft == null || !pointToTheLeft.isDigit()) {
            return this
        }

        return this.copy(
            i = pointToTheLeft.i,
            j = pointToTheLeft.j,
            value = pointToTheLeft.getNumbersToTheLeft(array)?.value + value
        )
    }

    private fun getNumbersToBothSides(array: List<List<Char>>): Point? {
        if (value == ".") return null

        val numbersToTheLeft = this.getNumbersToTheLeft(array)?.value?.removeSuffix(this.value)
        val numbersToTheRight = this.getNumbersToTheRight(array)?.value?.removePrefix(this.value)
        return this.copy(value = numbersToTheLeft + value +  numbersToTheRight)
    }
}

fun Int.positiveOrNull() = if (this >= 0) this else null
fun Int.lessThanOrNull(max: Int) = if (this <= max) this else null

fun List<String>.toArray(): List<List<Char>> {
    return fold(listOf()) { row, line ->
        row + listOf(line.map { it })
    }
}

fun isAdjacentToASymbol(surroundingElements: Map<Direction, Point?>) =
    surroundingElements.any { (_, element) -> element != null && element.isSymbol() }

fun isANumberToTheRight(surroundingElements: Map<Direction, Point?>): Boolean {
    if (surroundingElements[Direction.R] == null) return false
    return surroundingElements[Direction.R]?.isDigit() == true
}

data class Part1AccValue(
    val sum: Int = 0,
    val isAdjacentToASymbol: Boolean = false,
    val numberString: String = "",
)

fun main() {
    fun part1(input: List<String>): Int {
        return input.toArray().let { array ->
            input.foldIndexed(0) { i, acc, row ->
                row.foldIndexed(Part1AccValue()) innerFold@{ j, rowAcc, _ ->
                    val point = Point(i, j, array[i][j].toString())

                    val surroundingElements = point.getSurroundingElements(array)

                    if (!point.isDigit()) return@innerFold rowAcc

                    val acc1 = if (surroundingElements[Direction.L]?.isDigit() == false)
                        rowAcc.copy(numberString = "", isAdjacentToASymbol = false)
                    else
                        rowAcc

                    val acc2 = acc1.copy(numberString = acc1.numberString + point.value)

                    val acc3 = if (isAdjacentToASymbol(surroundingElements))
                        acc2.copy(isAdjacentToASymbol = true)
                    else
                        acc2


                    val acc4 = if (acc3.numberString.isNotBlank() && acc3.isAdjacentToASymbol && !isANumberToTheRight(surroundingElements))
                        acc3.copy(
                            sum = acc3.sum + acc3.numberString.toInt(),
                            numberString = "",
                            isAdjacentToASymbol = false
                        )
                    else
                        acc3

                    acc4
                }.sum + acc
            }
        }
    }

    fun part2(input: List<String>): Int {
        return input.toArray().let {array ->
            array.foldIndexed(0) { i, sum, row ->
                row.foldIndexed(sum) innerFold@{ j, innerSum, _ ->
                    val char = array[i][j]
                    val point = Point(i, j, char)

                    if (char == '*') {
                        val adjacentNumbers = point.getAdjacentNumbers(array)
                        if (adjacentNumbers.size == 2) {
                            return@innerFold innerSum + adjacentNumbers.first().value.toInt() * adjacentNumbers.last().value.toInt()
                        }
                    }

                    innerSum
                }
            }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
