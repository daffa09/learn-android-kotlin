package com.dicoding.learn

import java.util.Random


class NumberGenerator {
    fun generate(max: Int): Int {
        val random = Random()
        return random.nextInt(max)
    }
}