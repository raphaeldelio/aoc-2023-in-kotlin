data class Game(
    val gameId: Int,
    val gameSets: List<GameSet>
) {
    fun canBePlayed(maxCubes: Cubes) = gameSets.all { set ->
        set.cubes.red <= maxCubes.red &&
            set.cubes.green <= maxCubes.green &&
            set.cubes.blue <= maxCubes.blue
    }
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

fun main() {
    fun part1(input: List<String>): Int {
        val maxCubes = Cubes(12, 13, 14)

        return input.sumOf { line ->
            line.replace(",", "")
                .split(":")
                .fold(Game(0, emptyList())) { game, sets ->
                    if (game.gameId == 0) {
                        val gameId = sets.split(" ").last().toInt()
                        game.copy(gameId = gameId)
                    } else {
                        val gameSets = sets.split(";")
                            .map{ set ->
                                set.split(" ")
                                    .filter { it.isNotBlank() }
                                    .chunked(2)
                                    .fold(Cubes(0, 0, 0)) { cubes, pair ->
                                        val color = pair.last().lowercase()
                                        val count = pair.first().toInt()

                                        cubes.addCubes(color, count)
                                    }.let { cubes -> GameSet(cubes) }
                            }

                        game.copy(gameSets = gameSets)
                    }
                }.takeIf {
                    it.canBePlayed(maxCubes)
                }?.gameId ?: 0
        }
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
