import java.util.regex.Matcher
import java.util.regex.Pattern

/* Extensions */

fun Matcher.groups(i: Int = 0): Sequence<String> = generateSequence {
    if (this.find()) {
        this.group(i)
    } else {
        null
    }
}

fun String.containsAbba(): Boolean {
    // Short strings can never contain the desired four-character sequence
    if (length < 4) {
        return false
    }

    // For longer strings, find two consecutive characters first, then look "around" them
    // Don't start at 0, and only go until "length - 3" to have room for look-aheads
    // if there *is* a potential ABBA at the boundaries of a string
    val chars = toCharArray()
    return (1..length - 3).any {
        // Given a character sequence "abcd":
        // 1. Letters b & c must be the same
        chars[it] == chars[it + 1]
                // 2. Letters a & d must be the same
                && chars[it - 1] == chars[it + 2]
                // 3. Letters a & b must not be the same
                && chars[it] != chars[it - 1]
    }
}

fun String.listOfAbas(): List<String> {
    // Short strings can never contain the desired three-character sequence
    if (length < 3) {
        return emptyList()
    }

    // For longer strings, find two occurrences of the same character
    // with exactly 1 differing one in-between them, and return that as a List
    val chars = toCharArray()
    return (1..length - 2)
            .filter { chars[it - 1] == chars[it + 1] && chars[it - 1] != chars[it] }
            .map { "${chars[it - 1]}${chars[it]}${chars[it + 1]}" }
}

fun String.containsBabForAba(aba: String): Boolean {
    if (aba.length != 3) {
        throw IllegalArgumentException("Invalid aba: '$aba'")
    }

    val bab = "${aba[1]}${aba[0]}${aba[1]}"
    return contains(bab, ignoreCase = true)
}

/* Types */

class IPv7(raw: String) {

    val hypernet: String
    val supernet: String

    init {
        // Initialize hypernet portions (contained within square brackets)
        val hypernetRegex = Pattern.compile("\\[(\\w+)\\]")
        val matcher = hypernetRegex.matcher(raw)
        val hypernetTokens = matcher.groups().toList()

        // Initialize members (cut off square brackets for hypernet, replace them for remainder)
        hypernet = hypernetTokens
                .map {
                    it.toCharArray()
                            .filter { it != '[' && it != ']' }
                            .joinToString(separator = "")
                }
                .joinToString(separator = " ")

        var supernetToken = raw
        hypernetTokens.forEach {
            // Keep a space to avoid accidental ABBAs between different parts
            // of the address, previously separated by a hypernet sequence
            supernetToken = supernetToken.replace(it, " ")
        }
        this.supernet = supernetToken
    }

    // If any hypernet token in the address contains an ABBA,
    // this address doesn't support TLS per se.
    // Otherwise, check if the remainder *does* contain one instead
    fun tlsSupported(): Boolean =
            !hypernet.containsAbba() && supernet.containsAbba()

    // If any ABA contained in the supernet portion has a corresponding BAB in the hypernet, all is good
    fun sslSupported(): Boolean =
            supernet.listOfAbas().any { hypernet.containsBabForAba(it) }
}

/* Main */

fun main(args: Array<String>) {
    // Read & process content
    val addresses = String::class.java.getResource("/input.txt")
            .readText()
            .lines()
            .filter { it.isNotEmpty() }
            .map { IPv7(it) }

    // Filter out only those addresses from the input which support TLS
    val tlsSupportedCount = addresses
            .filter { it.tlsSupported() }
            .count()
    println("Part 1: TLS is supported by $tlsSupportedCount IPv7 addresses.")

    // Do the same for SSL
    val sslSupportedCount = addresses
            .filter { it.sslSupported() }
            .count()
    println("Part 2: SSL is supported by $sslSupportedCount IPv7 addresses.")
}
