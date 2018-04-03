package org.ice1000.devkt.ui

import org.ice1000.devkt.*
import org.jetbrains.kotlin.com.intellij.psi.tree.TokenSet
import org.jetbrains.kotlin.lexer.KtTokens
import java.awt.Desktop
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.io.File
import java.net.URL
import javax.swing.*
import javax.swing.text.AttributeSet
import javax.swing.text.DefaultStyledDocument
import javax.swing.undo.UndoManager

fun JFrame.TODO() {
	JOptionPane.showMessageDialog(this, "This feature is TODO.",
			"Unfinished", 1, AllIcons.KOTLIN)
}

/**
 * @author ice1000
 * @since v0.0.1
 */
class UIImpl(private val frame: `{-# LANGUAGE DevKt #-}`) : UI() {
	private val settings = frame.globalSettings
	private val undoManager = UndoManager()
	private var file: File? = null
	internal lateinit var undoMenuItem: JMenuItem
	internal lateinit var redoMenuItem: JMenuItem

	private inner class KtDocument : DefaultStyledDocument() {
		init {
			addUndoableEditListener {
				undoManager.addEdit(it.edit)
				updateUndoMenuItems()
			}
		}

		private val stringTokens = TokenSet.create(
				KtTokens.OPEN_QUOTE,
				KtTokens.CLOSING_QUOTE,
				KtTokens.REGULAR_STRING_PART,
				KtTokens.CHARACTER_LITERAL)

		override fun insertString(offs: Int, str: String, a: AttributeSet) {
			super.insertString(offs, str, a)
			reparse()
		}

		override fun remove(offs: Int, len: Int) {
			super.remove(offs, len)
			reparse()
		}

		private fun reparse() {
			val tokens = Kotlin.lex(editor.text)
			for ((start, end, text, type) in tokens) when {
				stringTokens.contains(type) -> highlight(start, end, STRING)
				KtTokens.COMMENTS.contains(type) -> highlight(start, end, COMMENTS)
				KtTokens.KEYWORDS.contains(type) -> highlight(start, end, KEYWORDS)
				else -> highlight(start, end, OTHERS)
			}
		}

		private fun highlight(tokenStart: Int, tokenEnd: Int, attributeSet: AttributeSet) {
			setCharacterAttributes(tokenStart, tokenEnd - tokenStart, attributeSet, false)
		}
	}

	init {
		frame.jMenuBar = menuBar
		editor.document = KtDocument()
		mainMenu(menuBar, frame)
		val lastOpenedFile = File(settings.lastOpenedFile)
		if (lastOpenedFile.canRead()) loadFile(lastOpenedFile)
		updateUndoMenuItems()
		editor.addKeyListener(object : KeyAdapter() {
			override fun keyPressed(e: KeyEvent) {
				if (e.isControlDown && !e.isAltDown && !e.isShiftDown && e.keyCode == KeyEvent.VK_Z) undo()
				if (e.isControlDown && !e.isAltDown && e.isShiftDown && e.keyCode == KeyEvent.VK_Z) redo()
			}
		})
	}

	fun open() {
		JFileChooser().apply {
			// dialogTitle = "Choose a Kotlin file"
			fileFilter = kotlinFileFilter
			file?.let { currentDirectory = it.parentFile }
			showOpenDialog(mainPanel)
		}.selectedFile?.let {
			loadFile(it)
		} ?: JOptionPane.showMessageDialog(mainPanel, "No file selected")
	}

	private fun loadFile(it: File) {
		if (it.canRead() and !checkFileSaved()) {
			file = it
			val path = it.absolutePath.orEmpty()
			frame.title = "$path - ${`{-# LANGUAGE DevKt #-}`.defaultTitle}"
			editor.text = it.readText()
			settings.lastOpenedFile = path
		}
	}

	fun idea() = browse("https://www.jetbrains.com/idea/download/")
	fun clion() = browse("https://www.jetbrains.com/clion/download/")
	fun eclipse() = browse("http://marketplace.eclipse.org/content/kotlin-plugin-eclipse")
	fun emacs() = browse("https://melpa.org/#/kotlin-mode")

	private fun browse(url: String) = try {
		Desktop.getDesktop().browse(URL(url).toURI())
	} catch (e: Exception) {
		JOptionPane.showMessageDialog(mainPanel, "Error when browsing $url:\n${e.message}")
	}

	fun buildAsClasses() {
		Kotlin.parse(editor.text)?.let(Kotlin::compile)
	}

	private fun checkFileSaved(): Boolean {
		frame.TODO()
		return false
	}

	fun buildAndRun() {
		buildAsClasses()
		justRun()
		frame.TODO()
	}

	fun justRun() {
		frame.TODO()
	}

	fun buildAsJar() {
		frame.TODO()
	}

	fun exit() {
		frame.dispose()
		settings.save()
		if (!checkFileSaved()) System.exit(0)
	}

	fun updateUndoMenuItems() {
		undoMenuItem.isEnabled = undoManager.canUndo()
		redoMenuItem.isEnabled = undoManager.canRedo()
	}

	fun undo() {
		if (undoManager.canUndo()) undoManager.undo()
	}

	fun redo() {
		if (undoManager.canRedo()) undoManager.redo()
	}

	fun selectAll() = editor.selectAll()
	fun cut() = editor.cut()
	fun copy() = editor.copy()
	fun paste() = editor.paste()
}
