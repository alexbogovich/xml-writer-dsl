package io.github.alexbogovich.xml.writer.dsl

interface EmptyElementDsl {
    infix fun String.attr(value: Any)
}