package org.ice1000.devkt.psi

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.tree.IElementType

val PsiElement.nodeType: IElementType get() = node.elementType