import java.util.regex.Pattern

/* Types */

sealed class Instruction {
    data class Rect(val width: Int, val height: Int) : Instruction()
    data class Rotate(val direction: Direction, val index: Int, val amount: Int) : Instruction() {
        enum class Direction { HORIZONTAL, VERTICAL }
    }

    companion object {
        fun fromString(instruction: String): Instruction {
            // The first word of the string defines the instruction to return
            val split = instruction.split(delimiters = " ")

            return when (split[0]) {
                "rect" -> {
                    // Parse the coordinates from the remainder of the string
                    val matcher = Pattern.compile("(\\d+)x(\\d+)").matcher(split[1])
                    if (!matcher.find()) {
                        throw IllegalArgumentException("Illegal Rect instruction: '${split[1]}")
                    }
                    Rect(width = matcher.group(1).toInt(), height = matcher.group(2).toInt())
                }
                "rotate" -> {
                    // The second token determines the direction of the Rotate op
                    val direction = when (split[1]) {
                        "row" -> Rotate.Direction.HORIZONTAL
                        "column" -> Rotate.Direction.VERTICAL
                        else -> throw IllegalArgumentException("Illegal Rotate direction: '${split[1]}")
                    }

                    // Parse the remainder of the string into the index & amount portions
                    val matcher = Pattern.compile("[xy]=(\\d+)").matcher(split[2])
                    if (!matcher.find()) {
                        throw IllegalArgumentException("Illegal Rotate instruction: '${split[2]}")
                    }
                    val index = matcher.group(1).toInt()
                    val amount = split[4].toInt()
                    Rotate(direction, index, amount)
                }
                else -> throw IllegalArgumentException("Illegal instruction: '$instruction'")
            }
        }
    }
}

class Screen(width: Int, height: Int) {
    private val pixels: Array<Array<Boolean>> = Array(height, { Array(width, { false }) })

    fun process(instruction: Instruction) =
            when (instruction) {
                is Instruction.Rect -> processRect(instruction)
                is Instruction.Rotate -> processRotate(instruction)
            }

    fun render() {
        pixels.forEachIndexed { index, row ->
            print("$index\t")
            row.forEach { item ->
                print(if (item) "#" else " ")
            }
            println()
        }
    }

    private fun processRect(rect: Instruction.Rect) {
        // Turn on all pixels of the given Rect's dimensions in the top-left corner
        (0..rect.width - 1).forEach { x ->
            (0..rect.height - 1).forEach { y ->
                pixels[y][x] = true
            }
        }
    }

    private fun processRotate(rotate: Instruction.Rotate) {
        when (rotate.direction) {
            Instruction.Rotate.Direction.HORIZONTAL -> {
                // Move over the row's contents to the right; overflow is re-attached on the left
                val row = pixels[rotate.index]
                val sliceStart = row.size - rotate.amount

                // Simply cut the row in two & re-attach them differently
                val slice = row.sliceArray(0..sliceStart - 1)
                val remainder = row.sliceArray(sliceStart..row.size - 1)
                pixels[rotate.index] = remainder + slice
            }

            Instruction.Rotate.Direction.VERTICAL -> {
                // Move over the column's contents downward; overflow is re-attached on the top.
                // In this direction, every row in the given column index must be touched.
                // 1) Scan the contents of the column
                // 2) Slice it, similarly to HORIZONTAL rotation
                // 3) Finally, re-write the contents of the column according to the re-attached slices
                var column = pixels
                        .map { row -> row[rotate.index] }
                        .toTypedArray()
                val sliceStart = column.size - rotate.amount

                val slice = column.sliceArray(0..sliceStart - 1)
                val remainder = column.sliceArray(sliceStart..column.size - 1)
                column = remainder + slice

                pixels.forEachIndexed { index, row ->
                    row[rotate.index] = column[index]
                }
            }
        }
    }

    fun litPixelCount(): Int = pixels
            .flatMap { it.asIterable() }
            .filter { it }
            .count()
}

/* Functions */

fun main(args: Array<String>) {
    // Read & prepare content
    val instructions = String::class.java.getResource("/input.txt")
            .readText()
            .lines()
            .filter { it.isNotEmpty() }
            .map { Instruction.fromString(it) }

    // Prepare screen & process each instruction one by one
    val screen = Screen(50, 6)
    instructions.forEach { screen.process(it) }

    println("Part 1: There are ${screen.litPixelCount()} illuminated pixels on the screen.")

    println("Part 2:")
    screen.render()
}
