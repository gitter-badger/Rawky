package com.deflatedpickle.rawky.dialogue

import com.deflatedpickle.rawky.api.DoubleRange
import com.deflatedpickle.rawky.api.IntRange
import com.deflatedpickle.rawky.api.Options
import com.deflatedpickle.rawky.api.Tooltip
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.widget.DoubleSlider
import com.deflatedpickle.rawky.widget.Slider
import org.reflections.Reflections
import java.awt.*
import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

class Settings : JDialog(Components.frame, "Settings") {
    companion object {
        val reflections = Reflections("com.deflatedpickle.rawky.component")
    }

    val panel = JPanel().apply {
        layout = GridBagLayout()
    }

    val labelConstraints = GridBagConstraints().apply {
        anchor = GridBagConstraints.EAST
    }

    val lineEnd = GridBagConstraints().apply {
        gridwidth = GridBagConstraints.REMAINDER
        weightx = 1.0
        fill = GridBagConstraints.BOTH
    }

    // TODO: Replace with an annotation system to define categories and pages
    val node = DefaultMutableTreeNode()

    val tree = JTree(node).apply {
        isRootVisible = false

        expandRow(1)

        addTreeSelectionListener {
            panel.removeAll()

            for (i in reflections.getSubTypesOf(JPanel::class.java)) {
                for (clazz in i.declaredClasses) {
                    if (clazz.annotations.map { it.annotationClass == Options::class }.contains(true)) {
                        for (field in clazz.fields) {
                            if (field.name != "INSTANCE") {
                                Components.processAnnotations(panel, field)
                            }
                        }
                    }
                }
            }

            panel.revalidate()
            panel.repaint()
        }
    }

    init {
        layout = BorderLayout()
        size = Dimension(600, 400)

        add(JSplitPane(JSplitPane.HORIZONTAL_SPLIT, JScrollPane(tree), JScrollPane(panel)).apply {
            dividerLocation = 150
        })
    }

    fun relayout() {
        this.panel.removeAll()

        for (i in reflections.getSubTypesOf(JPanel::class.java)) {
            for (clazz in i.declaredClasses) {
                if (clazz.annotations.map { it.annotationClass == Options::class }.contains(true)) {
                    with(tree.model as DefaultTreeModel) {
                        with(tree.model.root as DefaultMutableTreeNode) {
                            insertNodeInto(DefaultMutableTreeNode(clazz.simpleName.capitalize()), this, this.childCount)
                        }
                        reload()
                    }
                }
            }
        }

        this.invalidate()
        this.revalidate()
        this.repaint()
    }

    override fun setVisible(b: Boolean) {
        this.relayout()
        super.setVisible(b)
    }
}