package io.github.alexbogovich.xml.writer.dsl

import com.sun.xml.txw2.output.IndentingXMLStreamWriter
import io.github.alexbogovich.xml.writer.dsl.utils.isContainExternalPrefix
import mu.KLogging
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamWriter

class DslXMLStreamWriter(writer: XMLStreamWriter?) : IndentingXMLStreamWriter(writer), EmptyElementDsl {

    constructor(path: Path) : this(Files.newOutputStream(path)) {
        this.path = path
    }

    constructor(outputStream: OutputStream) : this(XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, "UTF-8"))

    companion object : KLogging()

    private val namespaceMapping = HashMap<String, String>()
    lateinit var path: Path

    lateinit var schemaNamespace: String

    fun document(init: xmlStreamLambda): DslXMLStreamWriter {
        this.writeStartDocument()
        this.init()
        this.writeEndDocument()
        this.flush()
        return this
    }

    private fun element(name: String, init: xmlStreamLambda): DslXMLStreamWriter {
        this.writeStartElement(name)
        this.init()
        this.writeEndElement()
        return this
    }

    private fun element(namespace: String, tagName: String, init: xmlStreamLambda): DslXMLStreamWriter {
        this.writeStartElement(namespace, tagName)
        this.init()
        this.writeEndElement()
        return this
    }

    private fun emptyElement(name: String, init: EmptyElementDsl.() -> Unit): DslXMLStreamWriter {
        this.writeEmptyElement(name)
        this.init()
        return this
    }

    private fun emptyElement(namespace: String, tagName: String, init: xmlStreamLambda): DslXMLStreamWriter {
        this.writeEmptyElement(namespace, tagName)
        this.init()
        return this
    }

    fun defaultNamespace(namespace: String): DslXMLStreamWriter {
        this.writeDefaultNamespace(namespace)
        return this
    }

    fun namespace(prefix: String, namespace: String): DslXMLStreamWriter {
        namespaceMapping[prefix] = namespace
        this.writeNamespace(prefix, namespace)
        return this
    }

    private fun element(name: String, content: Any) {
        element(name) {
            writeCharacters(content.toString())
        }
    }

    private fun element(namespace: String, name: String, content: Any) {
        element(namespace, name) {
            writeCharacters(content.toString())
        }
    }

    private fun attribute(name: String, value: Any) = writeAttribute(name, value.toString())

    private fun attribute(namespace: String, name: String, value: Any) = writeAttribute(namespace, name, value.toString())

    infix fun String.tag(value: Any) {
        element(this, value)
    }

    infix operator fun String.invoke(value: Any) {
        if (this.isContainExternalPrefix()) {
            val tag = this.split(":")
            element(namespaceMapping[tag[0]]!!, tag[1], value)
        } else {
            element(this, value)
        }
    }

    infix operator fun String.invoke(lambda: xmlStreamLambda) {
        if (!namespaceMapping.isEmpty() && this.isContainExternalPrefix()) {
            val tag = this.split(":")
            if (!namespaceMapping.isEmpty() && !namespaceMapping.contains(tag[0])) {
                throw RuntimeException("Prefix ${tag[0]} not in $namespaceMapping")
            }
            element(namespaceMapping[tag[0]]!!, tag[1], lambda)
        } else {
            element(this, lambda)
        }
    }

    infix fun String.tag(lambda: xmlStreamLambda) {
        element(this, lambda)
    }

    infix fun Pair<String, String>.tag(value: Any) {
        element(this.first, this.second, value)
    }

    infix fun Pair<String, String>.tag(lambda: xmlStreamLambda) {
        element(this.first, this.second, lambda)
    }

    override infix fun String.attr(value: Any) {
        if (this.isContainExternalPrefix()) {
            val tag = this.split(":")
            if (!namespaceMapping.isEmpty() && !namespaceMapping.contains(tag[0])) {
                throw RuntimeException("Prefix ${tag[0]} not in $namespaceMapping")
            }
            attribute(namespaceMapping[tag[0]]!!, tag[1], value)
        } else {
            attribute(this, value)
        }
    }

    infix fun String.emptyElement(lambda: EmptyElementDsl.() -> Unit) {
        if (this.isContainExternalPrefix()) {
            val tag = this.split(":")
            if (!namespaceMapping.isEmpty() && !namespaceMapping.contains(tag[0])) {
                throw RuntimeException("Prefix ${tag[0]} not in $namespaceMapping")
            }
            emptyElement(namespaceMapping[tag[0]]!!, tag[1], lambda)
        } else {
            emptyElement(this, lambda)
        }
    }
}