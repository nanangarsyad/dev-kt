package org.ice1000.devkt.config

import java.awt.Rectangle
import java.io.File
import java.util.*
import javax.imageio.ImageIO

/**
 * @author ice1000
 * @since v0.0.1
 */
object GlobalSettings {
	private val configFile = File("config.properties").absoluteFile
	private val properties = Properties()
	var lastOpenedFile: String by properties
	var tabSize: Int = 2
	var fontSize: Float = 16F
	var windowBounds = Rectangle(200, 100, 800, 600)
	var windowIcon = "" to ImageIO.read(javaClass.getResourceAsStream("/icon/kotlin24@2x.png"))
	var useTab: Boolean = true
	var highlightTokenBased: Boolean = true
	var highlightSemanticBased: Boolean = true
	var recentFiles = hashSetOf<File>()

	var appName: String by properties
	var monoFontName: String by properties
	var gothicFontName: String by properties
	var colorKeywords: String by properties
	var colorString: String by properties
	var colorTemplateEntries: String by properties
	var colorCharLiteral: String by properties
	var colorLineComments: String by properties
	var colorBlockComments: String by properties
	var colorDocComments: String by properties
	var colorOperators: String by properties
	var colorParentheses: String by properties
	var colorBraces: String by properties
	var colorBrackets: String by properties
	var colorSemicolon: String by properties
	var colorNumbers: String by properties
	var colorIdentifiers: String by properties
	var colorAnnotations: String by properties
	var colorColon: String by properties
	var colorComma: String by properties
	var colorVariable: String by properties
	var colorFunction: String by properties
	var colorTypeParam: String by properties
	var colorUserTypeRef: String by properties

	fun defaultOf(name: String, value: String) {
		if (!properties.containsKey(name)) properties[name] = value
	}

	fun load() {
		if (!configFile.exists()) configFile.createNewFile()
		else properties.load(configFile.inputStream())
		defaultOf(::lastOpenedFile.name, "")
		defaultOf(::appName.name, "Dev Kt")
		defaultOf(::monoFontName.name, "")
		defaultOf(::gothicFontName.name, "")
		defaultOf(::colorKeywords.name, "#CC7832")
		defaultOf(::colorString.name, "#6A8759")
		defaultOf(::colorTemplateEntries.name, "#CC7832")
		defaultOf(::colorCharLiteral.name, "#6A8759")
		defaultOf(::colorLineComments.name, "#808080")
		defaultOf(::colorBlockComments.name, "#808080")
		defaultOf(::colorDocComments.name, "#629755")
		defaultOf(::colorOperators.name, "#A9B7C6")
		defaultOf(::colorParentheses.name, "#A9B7C6")
		defaultOf(::colorBraces.name, "#A9B7C6")
		defaultOf(::colorBrackets.name, "#A9B7C6")
		defaultOf(::colorSemicolon.name, "#CC7832")
		defaultOf(::colorNumbers.name, "#6897BB")
		defaultOf(::colorIdentifiers.name, "#A9B7C6")
		defaultOf(::colorAnnotations.name, "#BBB529")
		defaultOf(::colorColon.name, "#A9B7C6")
		defaultOf(::colorComma.name, "#CC7832")
		defaultOf(::colorVariable.name, "#BCA5C4")
		defaultOf(::colorFunction.name, "#FFC66D")
		defaultOf(::colorTypeParam.name, "#6897BB")
		defaultOf(::colorUserTypeRef.name, "#62ABF0")
		properties[::windowIcon.name]
				?.toString()
				?.also {
					try {
						windowIcon = it to ImageIO.read(File(it))
					} catch (ignored: Exception) {
					}
				}
		properties[::windowBounds.name]
				?.toString()
				?.split(',', limit = 4)
				?.also { (x, y, width, height) ->
					x.toIntOrNull()?.let { windowBounds.x = it }
					y.toIntOrNull()?.let { windowBounds.y = it }
					width.toIntOrNull()?.let { windowBounds.width = it }
					height.toIntOrNull()?.let { windowBounds.height = it }
				}
		properties[::tabSize.name]?.toString()?.toIntOrNull()?.let { tabSize = it }
		properties[::fontSize.name]?.toString()?.toFloatOrNull()?.let { fontSize = it }
		properties[::useTab.name]?.let { useTab = it.toString() == "true" }
		properties[::highlightTokenBased.name]?.let { highlightTokenBased = it.toString() == "true" }
		properties[::highlightSemanticBased.name]?.let { highlightSemanticBased = it.toString() == "true" }
		properties[::recentFiles.name]?.run {
			toString()
					.split(File.pathSeparatorChar)
					.mapNotNullTo(recentFiles) { File(it).takeIf { it.exists() } }
		}
	}

	fun save() {
		properties[::recentFiles.name] = recentFiles.joinToString(File.pathSeparator)
		properties[::useTab.name] = useTab.toString()
		properties[::fontSize.name] = fontSize.toString()
		properties[::highlightTokenBased.name] = highlightTokenBased.toString()
		properties[::highlightSemanticBased.name] = highlightSemanticBased.toString()
		properties[::tabSize.name] = tabSize.toString()
		properties[::windowBounds.name] = "${windowBounds.x},${windowBounds.y},${windowBounds.width},${windowBounds.height}"
		properties[::windowIcon.name] = windowIcon.first
		properties.store(configFile.outputStream(), null)
	}
}
