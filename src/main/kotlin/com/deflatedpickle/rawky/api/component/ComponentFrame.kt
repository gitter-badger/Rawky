/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.api.component

import java.awt.BorderLayout
import java.awt.GridBagLayout
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JToolBar

class ComponentFrame(val component: Component) : JPanel() {
    init {
        isOpaque = false
        layout = BorderLayout()

        component.componentFrame = this
        add(JScrollPane(component))

        component.afterInit()
    }

    fun makeToolbars() {
        for ((k, v) in component.toolbarWidgets) {
            val toolbar = JToolBar().apply {
                layout = GridBagLayout()
            }

            for ((comp, const) in v) {
                when (comp) {
                    is JComponent -> {
                        toolbar.add(comp, const)
                    }
                    "---" -> toolbar.addSeparator()
                }
            }

            this.add(toolbar, k)
        }
    }
}
