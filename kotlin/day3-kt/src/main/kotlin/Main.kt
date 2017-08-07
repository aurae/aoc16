import java.util.regex.Pattern

/* Types */

class Triangle(sides: List<Int>) {
    val a: Int = sides[0]
    val b: Int = sides[1]
    val c: Int = sides[2]

    init {
        if (sides.size != 3) {
            throw IllegalArgumentException("Triangle requires 3 sides")
        }
    }

    fun valid(): Boolean = a + b > c && a + c > b && b + c > a
}

/* Main */

fun main(args: Array<String>) {
    // Read & prepare inputs (1 triangle per line)
    val content = String::class.java.getResource("/input.txt").readText()
            .lines()
            .filter { it.isNotEmpty() }
            .map { it.split(Pattern.compile("\\s+")).map { it.toInt() } }

    // Count the valid triangles,
    // i.e. those where each sum of two sides
    // exceed the length of the third
    val validCountP1 = content
            .map { Triangle(it) }
            .filter { it.valid() }
            .count()
    println("Part 1: The number of valid triangles is $validCountP1")

    // For Part 2, read in the content vertically
    val newTriangles = mutableListOf<List<Int>>()
    for (i in 0..content.size - 3 step 3) {
        val t1 = content[i]
        val t2 = content[i + 1]
        val t3 = content[i + 2]
        newTriangles += listOf(t1[0], t2[0], t3[0])
        newTriangles += listOf(t1[1], t2[1], t3[1])
        newTriangles += listOf(t1[2], t2[2], t3[2])
    }

    // Count the valid triangles again
    val validCountP2 = newTriangles
            .map { Triangle(it) }
            .filter { it.valid() }
            .count()
    println("Part 2: The number of valid triangles is $validCountP2")
}
