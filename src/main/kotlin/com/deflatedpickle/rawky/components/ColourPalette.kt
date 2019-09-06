package com.deflatedpickle.rawky.components

import java.awt.Color
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.*
import javax.swing.JMenuItem
import javax.swing.JPanel
import javax.swing.JPopupMenu

class ColourPalette : JPanel() {
    class ColourSwatch(var x: Int, var y: Int, val colour: Color)

    var cellSize = 40
    val colourList = mutableListOf<ColourSwatch>()

    var mouseX = 0
    var mouseY = 0
    var mouseToggled = false
    var mouseOffsetX = 0
    var mouseOffsetY = 0

    var selectedColour: ColourSwatch? = null

    init {
        addMouseMotionListener(object : MouseAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                mouseX = e.x
                mouseY = e.y
            }

            override fun mousePressed(e: MouseEvent) {
                mouseToggled = true

                if (e.button == MouseEvent.BUTTON1) {
                    if (e.clickCount == 2) {
                        Components.colourPicker.color = selectedColour?.colour
                    }
                }
            }

            override fun mouseReleased(e: MouseEvent) {
                mouseToggled = false

                for (i in colourList) {
                    if (e.x > i.x && e.x < i.x + cellSize
                            && e.y > i.y && e.y < i.y + cellSize) {
                        selectedColour = i
                        Collections.swap(colourList, colourList.indexOf(i), colourList.size - 1)
                        mouseOffsetX = e.x - i.x
                        mouseOffsetY = e.y - i.y
                        break
                    } else {
                        selectedColour = null
                        mouseOffsetX = 0
                        mouseOffsetY = 0
                    }
                }
            }

            override fun mouseDragged(e: MouseEvent) {
                if (mouseToggled) {
                    mouseReleased(e)
                }

                if (selectedColour != null) {
                    selectedColour!!.x = e.x - mouseOffsetX
                    selectedColour!!.y = e.y - mouseOffsetY
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
                        if (mouseX > i.x && mouseX < i.x + cellSize
                                && mouseY > i.y && mouseY < i.y + cellSize) {
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

        for (i in this.colourList) {
            g.color = i.colour
            g.fillRect(i.x, i.y, cellSize, cellSize)

            if (i == selectedColour) {
                g.color = Color.GREEN
                g.drawRect(i.x, i.y, cellSize, cellSize)
            } else if (mouseX > i.x && mouseX < i.x + cellSize
                    && mouseY > i.y && mouseY < i.y + cellSize) {
                g.color = Color.CYAN
                g.drawRect(i.x, i.y, cellSize, cellSize)
            }
        }
    }
}