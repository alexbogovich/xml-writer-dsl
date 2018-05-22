package io.github.alexbogovich.xml.writer.dsl

typealias xmlStreamLambda = DslXMLStreamWriter.() -> Unit
typealias xmlStreamCoroutine = suspend CoroutineXMLStreamWriter.() -> Unit