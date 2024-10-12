package com.dicoding.learn

import androidx.lifecycle.ViewModel

class MainModelView : ViewModel() {
    var result = 0

    fun calculate(width: String, height: String, length: String) {
    result = width.toInt() * height.toInt() * length.toInt()
    }
}