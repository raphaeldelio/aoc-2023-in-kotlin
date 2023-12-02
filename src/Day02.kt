data class Game(
    val gameId: Int,
    val gameSets: List<GameSet>
) {
    fun canBePlayed(maxCubes: Cubes) = gameSets.all { set ->
        set.cubes.red <= maxCubes.red &&
            set.cubes.green <= maxCubes.green &&
            set.cubes.blue <= maxCubes.blue
    }

    fun minCubes() = minBlues() * minGreens() * minReds()

    private fun minBlues() = gameSets.maxOf { it.cubes.blue }
    private fun minGreens() = gameSets.maxOf { it.cubes.green }
    private fun minReds() = gameSets.maxOf { it.cubes.red }
}

data class GameSet(val cubes: Cubes)
data class Cubes(val red: Int, val green: Int, val blue: Int) {
    fun addCubes(color: String, count: Int) = when (color) {
        "red" -> copy(red = red + count)
        "green" -> copy(green = green + count)
        "blue" -> copy(blue = blue + count)
        else -> this
    }
}

fun parseGame(line: String) = line.split(": ")
    .fold(Game(0, emptyList())) { game, gameIdOrSets ->
        if (game.gameId == 0)
            game.copy(gameId = gameIdOrSets.asGameId)
        else
            game.copy(gameSets = parseGameSets(gameIdOrSets))
    }

fun parseGameSets(sets: String) = sets.split("; ")
    .map { set ->
        set.split(", ")
            .fold(Cubes(0, 0, 0)) { cubes, countColor ->
                with(parseColorCount(countColor)) {
                    val count = first
                    val color = second
                    cubes.addCubes(color, count)
                }
            }.let { cubes -> GameSet(cubes) }
    }

fun parseColorCount(countColor: String) = countColor.split(" ")
    .let { it.first().toInt() to it.last().lowercase() }

val String.asGameId: Int get() = split(" ").last().toInt()

fun main() {
    fun part1(input: List<String>): Int {
        val maxCubes = Cubes(12, 13, 14)

        return input.sumOf { line ->
            parseGame(line).takeIf {
                it.canBePlayed(maxCubes)
            }?.gameId ?: 0
        }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { line -> parseGame(line).minCubes() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
