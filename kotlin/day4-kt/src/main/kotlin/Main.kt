/* Extensions */

fun Char.rotateAlphabet(n: Int): Char {
    // Used for part 2 of this challenge.
    // Rotates a Char around the alphabet by n letters.
    // (A becomes B, Z becomes A).
    // Non-alphabetical characters aren't affected
    val range = if (this.isLowerCase()) {
        'a'.toInt()..'z'.toInt()
    } else {
        'A'.toInt()..'Z'.toInt()
    }

    val self = this.toInt()
    if (self in range) {
        // Rotate the character around the given ASCII range,
        // wrapping around to the start if necessary
        val rangeSpan = range.last - range.first + 1
        return ((self - range.first + n) % rangeSpan + range.first).toChar()

    } else {
        // Non-alphabetical char
        return this
    }
}

/* Types */

class Room(raw: String) {
    val checksum: String
    val sectorId: Int
    val encrypted: String

    init {
        // Parse Checksum
        val checksumStart = raw.indexOf('[') + 1
        val checksumEnd = raw.indexOf(']') - 1
        checksum = raw.substring(checksumStart..checksumEnd)

        // Parse Sector ID
        val sectorIdStart = raw.lastIndexOf('-') + 1
        val sectorIdEnd = checksumStart - 2
        sectorId = raw.substring(sectorIdStart..sectorIdEnd).toInt()

        // Parse Name
        encrypted = raw.substring(0..sectorIdStart - 2)
    }
}

fun main(args: Array<String>) {
    // Read & prepare inputs
    val rooms = String::class.java.getResource("/input.txt").readText()
            .lines()
            .filter { it.isNotEmpty() }
            .map { Room(it) }

    // Take each room's encrypted name & validate it using the checksum
    // (for this, sort each name & compare its most common letters)
    val validRooms = rooms
            .filter { room ->
                // Convert the room's name into individual characters
                // (this temporary reference is used to count each
                // characters' occurrences, in order to determine the most common ones)
                val chars = room.encrypted
                        .toCharArray()
                        .filter { it != '-' }

                // Apply a chain of commands to the character array:
                // * Assign the number of occurrences in the name to each character
                // * Remote duplicates
                // * Sort by occurrences
                // * Take a number of characters equal to the room's checksum
                // * Compare the joined string against that checksum
                chars
                        .map { c -> Pair(c, chars.count { it == c }) }
                        .distinct()
                        .sortedWith(Comparator { (c1, count1), (c2, count2) ->
                            // Compare counts first, then fall back to comparing the characters
                            val cmp = count2.compareTo(count1)
                            if (cmp == 0) {
                                c1.compareTo(c2)
                            } else {
                                cmp
                            }
                        })
                        .take(room.checksum.length)
                        .map { it.first }
                        .joinToString(separator = "") == room.checksum
            }

    val sumOfSectorIds = validRooms
            .map { it.sectorId }
            .sum()
    println("Part 1: The sum of all valid room's sector IDs is $sumOfSectorIds")

    // "Decipher" each room's name by applying
    // the shift cipher indicated by its sector ID
    val decipheredNames = rooms
            .map { room ->
                Pair(
                        room.sectorId,
                        room.encrypted.replace('-', ' ')
                                .toCharArray()
                                .map { it.rotateAlphabet(room.sectorId) }
                                .joinToString(separator = ""))
            }
            .toList()

    // Find the "North Pole Object Storage" room's ID
    val keywords = listOf("north", "pole")
    val candidates = decipheredNames
            .filter { pair -> keywords.any { kw -> pair.second.contains(kw) } }
            .toList()
    println("Part 2: Candidates for room search '${keywords.joinToString()}'")
    println("   Sector ID\t-> Room Name")
    candidates.forEach { println("   ${it.first}\t\t\t-> ${it.second}") }
}
