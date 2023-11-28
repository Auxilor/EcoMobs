package com.willfp.ecomobs.math

data class Int3(
    val x: Int,
    val y: Int,
    val z: Int
) {
    operator fun rangeTo(other: Int3): Int3Range {
        return Int3Range(this, other)
    }
}

data class Int3Range(
    val start: Int3,
    val end: Int3
) : Iterable<Int3> {
    override fun iterator(): Iterator<Int3> {
        return Int3Iterator(this)
    }

    fun randomIterator(): Iterator<Int3> {
        return RandomInt3Iterator(this)
    }
}

class Int3Iterator(
    private val range: Int3Range
) : Iterator<Int3> {
    private var current = range.start

    override fun hasNext(): Boolean {
        return current.x <= range.end.x &&
                current.y <= range.end.y &&
                current.z <= range.end.z
    }

    override fun next(): Int3 {
        val toReturn = current

        if (current.x < range.end.x) {
            current = current.copy(x = current.x + 1)
        } else if (current.y < range.end.y) {
            current = current.copy(x = range.start.x, y = current.y + 1)
        } else if (current.z < range.end.z) {
            current = current.copy(x = range.start.x, y = range.start.y, z = current.z + 1)
        }

        return toReturn
    }
}

class RandomInt3Iterator(
    private val range: Int3Range
) : Iterator<Int3> {
    override fun hasNext(): Boolean {
        return true
    }

    override fun next(): Int3 {
        // Generate a random Int3 within the bounds
        return Int3(
            range.start.x + ((range.end.x - range.start.x) * Math.random()).toInt(),
            range.start.y + ((range.end.y - range.start.y) * Math.random()).toInt(),
            range.start.z + ((range.end.z - range.start.z) * Math.random()).toInt()
        )
    }
}
