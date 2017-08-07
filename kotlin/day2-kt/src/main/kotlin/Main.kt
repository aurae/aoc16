/* Types */

typealias Keypad = Array<Array<String>>

enum class Move(val char: Char, val moveCol: Int, val moveRow: Int) {
    UP(char = 'U', moveCol = 0, moveRow = -1),
    RIGHT(char = 'R', moveCol = 1, moveRow = 0),
    DOWN(char = 'D', moveCol = 0, moveRow = 1),
    LEFT(char = 'L', moveCol = -1, moveRow = 0);

    companion object {
        fun fromChar(char: Char): Move =
                values()
                        .filter { it.char == char }
                        .firstOrNull()
                        ?: throw IllegalArgumentException("Invalid Move char: '$char'")
    }
}

data class Pos(val col: Int, val row: Int) {

    fun execute(move: Move, keypad: Keypad): Pos {
        val rowCount = keypad.size
        val colCount = keypad[0].size

        // Determine if illegal move by checking the new coordinates
        // & the Keypad value beneath the new position as well
        val newRow = row + move.moveRow
        val newCol = col + move.moveCol

        if (newRow in 0..(rowCount - 1) && newCol in 0..(colCount - 1)) {
            // Valid keypad access
            val keypadVal = keypad[newRow][newCol]
            if (keypadVal.trim().isNotEmpty()) {
                // Valid keypad button
                return Pos(col = newCol, row = newRow)

            } else {
                // Invalid keypad button, return no new position
                return this
            }

        } else {
            // Invalid keypad access, return no new position
            return this
        }
    }
}

fun main(args: Array<String>) {
    // Read instructions
    val instructions: List<List<Move>> = String::class.java.getResource("/input.txt").readText()
            .lines()
            .filter { it.isNotEmpty() }
            .map {
                it.toCharArray()
                        .map { Move.fromChar(it) }
                        .toList()
            }
            .toList()

    // Part 1: Start on the center button & execute each instruction one by one
    val part1Keypad: Keypad = arrayOf(
            arrayOf("1", "2", "3"),
            arrayOf("4", "5", "6"),
            arrayOf("7", "8", "9")
    )

    var part1Position = Pos(1, 1)
    val part1Pass = mutableListOf<String>()

    instructions.forEach { instruction ->
        instruction.forEach { part1Position = part1Position.execute(it, part1Keypad) }
        part1Pass.add(part1Keypad[part1Position.row][part1Position.col])
    }

    println("Part 1: The password is '${part1Pass.joinToString(separator = "")}'.")

    // Part 2: Do it again with a different keypad
    val part2Keypad: Keypad = arrayOf(
            arrayOf(" ", " ", "1", " ", " "),
            arrayOf(" ", "2", "3", "4", " "),
            arrayOf("5", "6", "7", "8", "9"),
            arrayOf(" ", "A", "B", "C", " "),
            arrayOf(" ", " ", "D", " ", " ")
    )

    // Start on "5" again
    var part2Position = Pos(0, 2)
    val part2Pass = mutableListOf<String>()

    instructions.forEach { instruction ->
        instruction.forEach { part2Position = part2Position.execute(it, part2Keypad) }
        part2Pass.add(part2Keypad[part2Position.row][part2Position.col])
    }

    println("Part 2: The password is '${part2Pass.joinToString(separator = "")}'.")
}
