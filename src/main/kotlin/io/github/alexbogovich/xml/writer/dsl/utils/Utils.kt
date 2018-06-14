package io.github.alexbogovich.xml.writer.dsl.utils

fun String.isContainExternalPrefix() =
    this.contains(":") && !this.toLowerCase().startsWith("xml:")
