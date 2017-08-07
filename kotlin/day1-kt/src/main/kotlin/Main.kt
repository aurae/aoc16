/* Extensions */

typealias Coords = Pair<Int, Int>

operator fun Coords.plus(other: Coords): Coords =
        Coords(this.first + other.first, this.second + other.second)

operator fun Coords.rangeTo(other: Coords): Sequence<Coords> {
    // Define positional ranges for each coordinate, if any
    val xRange: IntProgression? = if (first < other.first) {
        first..other.first
    } else if (first > other.first) {
        first downTo other.first
    } else {
        null
    }

    val yRange: Iterable<Int>? = if (second < other.second) {
        second..other.second
    } else if (second > other.second) {
        second downTo other.second
    } else {
        null
    }

    val xIter = xRange?.iterator()
    val yIter = yRange?.iterator()

    var currentX = this.first
    var currentY = this.second

    return generateSequence {
        // Null-check over let {} because of required return values here
        if (xIter != null && xIter.hasNext()) {
            currentX = xIter.next()
            Coords(currentX, currentY)

        } else if (yIter != null && yIter.hasNext()) {
            currentY = yIter.next()
            Coords(currentX, currentY)

        } else {
            null
        }
    }
}

fun Coords.manhattanDistanceTo(other: Coords = Coords(0, 0)): Int =
        Math.abs(this.first - other.first) + Math.abs(this.second - other.second)

/* Classes */

enum class Direction(
        val degrees: Int,
        private val moveX: Int,
        private val moveY: Int) {

    NORTH(degrees = 0, moveX = 0, moveY = -1),
    EAST(degrees = 90, moveX = 1, moveY = 0),
    SOUTH(degrees = 180, moveX = 0, moveY = 1),
    WEST(degrees = 270, moveX = -1, moveY = 0);

    fun turn(facing: Char): Direction =
            when (facing) {
                'R' -> Direction.fromDegrees((this.degrees + 90) % 360)
                'L' -> Direction.fromDegrees(((this.degrees - 90) + 360) % 360)
                else -> throw IllegalArgumentException("Unknown face value: '$facing'")
            }

    fun move(amount: Int): Coords =
            Pair(this.moveX * amount, this.moveY * amount)

    companion object {
        fun fromDegrees(degrees: Int): Direction =
                values().firstOrNull { it.degrees == degrees }
                        ?: throw IllegalArgumentException("No Direction available for $degrees°")
    }
}

data class Position(
        val direction: Direction,
        val coords: Coords) {

    fun execute(instruction: Pair<Char, Int>): Position {
        val (facing, amount) = instruction

        val newDirection = this.direction.turn(facing)
        val moveVector = newDirection.move(amount)

        return Position(
                direction = newDirection,
                coords = this.coords + moveVector)
    }
}

/* Main */

fun main(args: Array<String>) {
    // Read & process input:
    // Cut up comma-separated list into <Char, Int> tuples of instructions ("face value", "length")
    val instructions = String::class.java.getResource("/input.txt").readText()
            .split(',')
            .map { it.trim() }
            .map { Pair(it[0], it.substring(1).toInt()) }

    // Starting from an initial position, execute the instructions one by one
    val positions = mutableListOf(Position(direction = Direction.NORTH, coords = Pair(0, 0)))
    instructions.asSequence()
            .map { positions.last().execute(it) }
            .forEach { positions.add(it) }

    // Part 1: Find the distance from the start to the final position
    val lastCoords = positions.last().coords
    println("Part 1: Distance to the final position $lastCoords: ${lastCoords.manhattanDistanceTo()} blocks")

    // Part 2: Find the first position that's accessed twice
    val visited = mutableSetOf<Coords>()
    outer@ for (i in 1..positions.size) {
        val fromCoords = positions[i - 1].coords
        val toCoords = positions[i].coords

        // Skip the first item of each Coords range
        val coordRange = (fromCoords..toCoords).iterator()
        for (coords in coordRange) {
            if (!coordRange.hasNext()) {
                // Skip the last item in each range
                // to prevent accidental premature duplicates
                // because of Point boundaries:
                // If (P1..P2) generates Coords (C1, C2, C3),
                // (P2..P3) will include C3 again, since P2 == C3.
                break
            }

            if (!visited.add(coords)) {
                // Nothing was added, therefore
                // the element already existed in the Set.
                // Found it!
                println("Part 2: Distance to the first position visited twice $coords: ${coords.manhattanDistanceTo()} blocks")
                break@outer
            }
        }
    }
}
