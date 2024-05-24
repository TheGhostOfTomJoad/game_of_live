package org.example




data class V2 (val x: Int, val y: Int){

    operator fun plus(other: V2): V2 {
        return V2(x + other.x, y + other.y)
    }
    operator fun times(other: V2): Int {
        return x * other.x + y * other.y
    }

    fun orthogonal(other: V2): Boolean {
        return this * other == 0
    }

}