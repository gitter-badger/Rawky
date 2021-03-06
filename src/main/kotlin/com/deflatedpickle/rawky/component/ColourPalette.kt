/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.api.annotations.RedrawActive
import com.deflatedpickle.rawky.api.component.Component
import com.deflatedpickle.rawky.transfer.ColourTransfer
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.Icons
import java.awt.BasicStroke
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.Collections
import javax.swing.JButton
import javax.swing.JMenuItem
import javax.swing.JPopupMenu
import javax.swing.JSlider
import javax.swing.TransferHandler
import javax.swing.UIManager

@RedrawActive
class ColourPalette : Component() {
    val slider = JSlider(1, 100).apply {
        this.value = 10
        addChangeListener {
            Components.colourPalette.scale = this.value / 10.0

            with(this@ColourPalette.colourList.size * (this@ColourPalette.cellSize * this.value / 100)) {
                this@ColourPalette.preferredSize = java.awt.Dimension(this, this)
            }

            this@ColourPalette.repaintWithChildren()
        }
    }

    val buttonZoomOut = JButton(Icons.zoomOut).apply {
        toolTipText = "Zoom Out"
        addActionListener {
            slider.value--
        }
    }

    val buttonZoomIn = JButton(Icons.zoomIn).apply {
        toolTipText = "Zoom In"
        addActionListener {
            slider.value++
        }
    }

    class ColourSwatch(var x: Int, var y: Int, val colour: Color) {
        init {
            Components.colourPalette.colourList.add(this)
        }
    }

    var cellSize = 20
    val colourList = mutableListOf<ColourSwatch>()

    var mouseX = 0
    var mouseY = 0
    var mouseToggled = false

    var selectedColour: ColourSwatch? = null
    var hoverColour: ColourSwatch? = null

    var scale = 1.0

    init {
        toolbarWidgets[BorderLayout.PAGE_END] = listOf(
                Pair(buttonZoomOut, null),
                Pair(slider, fillX),
                Pair(buttonZoomIn, null)
        )

        transferHandler = ColourTransfer.Import

        addMouseMotionListener(object : MouseAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                mouseX = e.x
                mouseY = e.y

                for (i in colourList) {
                    if (e.x > i.x && e.x < i.x + cellSize &&
                            e.y > i.y && e.y < i.y + cellSize) {
                        hoverColour = i
                        break
                    } else {
                        hoverColour = null
                    }
                }
            }

            override fun mousePressed(e: MouseEvent) {
                mouseToggled = true

                transferHandler = hoverColour?.colour?.let { ColourTransfer.ExportImport(it) }
                transferHandler?.let { (e.source as ColourPalette).transferHandler.exportAsDrag(e.source as ColourPalette, e, TransferHandler.MOVE) }

                if (e.button == MouseEvent.BUTTON1) {
                    if (e.clickCount == 2) {
                        if (selectedColour != null) {
                            Components.colourPicker.color = selectedColour!!.colour
                        }
                    }
                }
            }

            override fun mouseReleased(e: MouseEvent) {
                mouseToggled = false

                for (i in colourList) {
                    if (e.x > i.x && e.x < i.x + cellSize &&
                            e.y > i.y && e.y < i.y + cellSize) {
                        selectedColour = i
                        Collections.swap(colourList, colourList.indexOf(i), colourList.size - 1)
                        break
                    } else {
                        selectedColour = null
                    }
                }
            }

            override fun mouseDragged(e: MouseEvent) {
                if (mouseToggled) {
                    mouseReleased(e)
                }
            }
        }.apply { addMouseListener(this) })

        componentPopupMenu = JPopupMenu().apply {
            add(JMenuItem("Add").apply {
                addActionListener {
                    colourList.add(ColourSwatch(mouseX, mouseY, Components.colourPicker.color))
                }
            })

            add(JMenuItem("Delete").apply {
                addActionListener {
                    for (i in colourList) {
                        if (mouseX > i.x && mouseX < i.x + cellSize &&
                                mouseY > i.y && mouseY < i.y + cellSize) {
                            colourList.remove(i)
                            break
                        }
                    }
                }
            })
        }
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2D = g as Graphics2D

        g2D.scale(scale, scale)

        for (i in this.colourList) {
            g2D.color = i.colour
            g2D.fillRect(i.x, i.y, cellSize, cellSize)

            g2D.color = UIManager.getColor("List.selectionBackground")
            val strokeThickness = if (i == selectedColour) {
                3f
            } else if (mouseX > i.x && mouseX < i.x + cellSize &&
                    mouseY > i.y && mouseY < i.y + cellSize) {
                2f
            } else {
                0f
            }

            if (strokeThickness > 0) {
                g2D.stroke = BasicStroke(strokeThickness)
                g2D.drawRect(i.x, i.y, cellSize, cellSize)
            }
        }
    }
}
