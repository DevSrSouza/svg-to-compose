package br.com.devsrsouza.svg2compose

import java.util.*

fun <T> Stack<T>.peekOrNull(): T? = runCatching { peek() }.getOrNull()