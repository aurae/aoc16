fun main(args: Array<String>) {
    // Read & process input (convert array of lines into columns)
    val lines = String::class.java.getResource("/input.txt")
            .readText()
            .lines()

    val columns = mutableMapOf<Int, MutableList<Char>>()
    lines
            .flatMap { it.toCharArray().asIterable().withIndex() }
            .forEach { (index, char) ->
                val column = columns.getOrElse(index, { mutableListOf() })
                column.add(char)
                columns.put(index, column)
            }

    // For each column (i.e. Map value), find the most common character & concatenate into a String
    val messagePart1 = columns.values
            // Indexed to allow for back-reference into the respective column by index
            .mapIndexed { index, chars ->
                // Count each character's occurrence & remove duplicates afterwards
                chars.map { Pair(it, columns.getValue(index).count { c -> c == it }) }
                        .distinct()
                        .sortedByDescending { it.second }
                        .take(1)
                        .map { it.first }
                        .first()
            }
            .joinToString(separator = "")
    println("Part 1: The message is '$messagePart1'")

    // Part 2 of the challenge is basically the exact same,
    // but we're inverting the sorting algorithm used to determine which character to pick for each column
    // (i.e. "sortedBy" instead of "sortedByDescending")
    val messagePart2 = columns.values
            // Indexed to allow for back-reference into the respective column by index
            .mapIndexed { index, chars ->
                // Count each character's occurrence & remove duplicates afterwards
                chars.map { Pair(it, columns.getValue(index).count { c -> c == it }) }
                        .distinct()
                        .sortedBy { it.second }
                        .take(1)
                        .map { it.first }
                        .first()
            }
            .joinToString(separator = "")
    println("Part 2: The message is '$messagePart2'")
}
