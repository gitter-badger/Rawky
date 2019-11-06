package com.deflatedpickle.rawky

import bibliothek.gui.dock.common.CControl
import bibliothek.gui.dock.common.CGrid
import bibliothek.gui.dock.common.DefaultSingleCDockable
import com.deflatedpickle.rawky.component.PixelGrid
import com.deflatedpickle.rawky.dialogue.New
import com.deflatedpickle.rawky.menu.Edit
import com.deflatedpickle.rawky.menu.File
import com.deflatedpickle.rawky.menu.Help
import com.deflatedpickle.rawky.menu.Program
import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.Commands
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.Icons
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridBagLayout
import javax.swing.*

fun main() {
    SwingUtilities.invokeLater {
        Components.frame.jMenuBar = JMenuBar().apply {
            add(File())
            add(Edit())
            add(Program())
            add(Help())
        }

        Components.frame.layout = BorderLayout()

        Components.frame.add(JToolBar().apply {
            add(JButton(Icons.createNew).apply {
                toolTipText = "New File"
                addActionListener { New().isVisible = true }
            })
            add(JButton(Icons.openedFolder).apply {
                toolTipText = "Open File"
                addActionListener { Commands.open() }
            })
            add(JButton(Icons.picture).apply {
                toolTipText = "Save File As"
                addActionListener { Commands.save() }
            })
            addSeparator()
            add(JButton(Icons.undo).apply {
                toolTipText = "Undo"
                addActionListener { ActionStack.undo() }
            })
            add(JButton(Icons.redo).apply {
                toolTipText = "Redo"
                addActionListener { ActionStack.redo() }
            })
        }, BorderLayout.PAGE_START)

        val cControl = CControl(Components.frame)
        cControl.contentArea.isOpaque = false
        Components.frame.add(cControl.contentArea)

        val grid = CGrid(cControl)

        val toolbox = DefaultSingleCDockable("toolbox", "Toolbox", JScrollPane(Components.toolbox))
        cControl.addDockable(toolbox)
        toolbox.isVisible = true
        grid.add(0.0, 0.2, 0.2, 1.8, toolbox)

        val tiledView = DefaultSingleCDockable("tiledView", "Tiled View", Components.tiledView)
        cControl.addDockable(tiledView)
        tiledView.isVisible = true

        val animationPreview = DefaultSingleCDockable("animationPreview", "Animation Preview", JPanel().apply {
            isOpaque = false
            layout = BorderLayout()

            add(JScrollPane(Components.animationPreview))

            add(JToolBar().apply {
                add(JSpinner(SpinnerNumberModel(1, 1, 240, 1)).apply {
                    val timer = Timer(1000 / this.value as Int) {
                        if (Components.animationPreview.frame < Components.animationTimeline.listModel.size() - 1) {
                            Components.animationPreview.frame++
                        }
                        else {
                            Components.animationPreview.frame = 0
                        }
                    }.apply { start() }

                    addChangeListener {
                        timer.delay = 1000 / this.value as Int
                    }
                })
            }, BorderLayout.PAGE_END)
        })
        cControl.addDockable(animationPreview)
        animationPreview.isVisible = true
        grid.add(0.0, 0.3, 0.6, 1.0, tiledView, animationPreview)

        val layerList = DefaultSingleCDockable("layerList", "Layer List", JPanel().apply {
            isOpaque = false
            layout = BorderLayout()

            add(JScrollPane(Components.layerList))

            add(JToolBar().apply {
                add(JButton(Icons.createNew).apply {
                    toolTipText = "New Layer"
                    addActionListener {
                        Components.layerList.addLayer()
                    }
                })
                add(JButton(Icons.trash).apply {
                    toolTipText = "Delete Layer"
                    addActionListener {
                        Components.layerList.removeLayer()
                    }
                })
            }, BorderLayout.PAGE_END)
        })
        cControl.addDockable(layerList)
        layerList.isVisible = true
        grid.add(0.0, 1.0, 0.6, 1.0, layerList)

        val pixelGrid = DefaultSingleCDockable("pixelGrid", "Pixel Grid", JPanel().apply {
            isOpaque = false
            layout = BorderLayout()

            add(PixelGrid.SCROLLABLE_INSTANCE)

            add(JToolBar().apply {
                val slider = JSlider(25, 300).apply {
                    this.value = 50
                    addChangeListener {
                        Components.pixelGrid.scale = this.value / 50.0
                    }
                }
                add(JButton(Icons.zoomOut).apply {
                    toolTipText = "Zoom Out"
                    addActionListener {
                        slider.value--
                    }
                })
                add(slider)
                add(JButton(Icons.zoomIn).apply {
                    toolTipText = "Zoom In"
                    addActionListener {
                        slider.value++
                    }
                })
            }, BorderLayout.PAGE_END)
        })
        cControl.addDockable(pixelGrid)
        pixelGrid.isVisible = true
        grid.add(0.6, 0.3, 0.6, 1.4, pixelGrid)

        val animationTimeline = DefaultSingleCDockable("animationTimeline", "Animation Timeline", JPanel().apply {
            layout = BorderLayout()

            add(JToolBar().apply {
                layout = GridBagLayout()

                add(Components.animationTimeline.pastColour)
                add(Components.animationTimeline.slider)
                add(Components.animationTimeline.postColour)
            }, BorderLayout.PAGE_START)

            add(JScrollPane(Components.animationTimeline))

            add(JToolBar().apply {
                add(JButton(Icons.createNew).apply {
                    toolTipText = "New Frame"
                    addActionListener {
                        Components.animationTimeline.addFrame()
                    }
                })
                add(JButton(Icons.trash).apply {
                    toolTipText = "Delete Frame"
                    addActionListener {
                        Components.animationTimeline.removeFrame()
                    }
                })
            }, BorderLayout.PAGE_END)
        })
        cControl.addDockable(animationTimeline)
        animationTimeline.isVisible = true
        grid.add(0.6, 1.4, 0.6, 0.6, animationTimeline)

        val miniMap = DefaultSingleCDockable("miniMap", "Mini-Map", JScrollPane(Components.miniMap))
        cControl.addDockable(miniMap)
        miniMap.isVisible = true
        grid.add(1.0, 0.0, 0.4, 0.6, miniMap)

        val colourPicker = DefaultSingleCDockable("colourPicker", "Colour Picker", JPanel().apply {
            isOpaque = false
            layout = BorderLayout()

            add(Components.colourPicker)

            add(JToolBar().apply {
                add(JCheckBox("Expert Controls").apply {
                    addActionListener {
                        Components.colourPicker.setExpertControlsVisible(this.isSelected)
                    }
                })
            }, BorderLayout.PAGE_END)
        })
        cControl.addDockable(colourPicker)
        colourPicker.isVisible = true
        grid.add(1.0, 0.3, 0.4, 0.4, colourPicker)

        val toolOptions = DefaultSingleCDockable("toolOptions", "Tool Options", JScrollPane(Components.toolOptions))
        cControl.addDockable(toolOptions)
        toolOptions.isVisible = true
        grid.add(1.0, 0.4, 0.4, 0.6, toolOptions)

        val colourShades = DefaultSingleCDockable("colourShades", "Colour Shades", JPanel().apply {
            isOpaque = false
            layout = BorderLayout()

            add(JScrollPane(Components.colourShades))

            add(JToolBar().apply {
                add(JSlider(3, 3 * 31).apply {
                    value = Components.colourShades.amount

                    addChangeListener {
                        Components.colourShades.amount = this.value
                        Components.colourShades.createShades()
                        Components.colourShades.updateShades()
                        Components.colourShades.repaint()
                    }
                })
            }, BorderLayout.PAGE_END)
        })
        cControl.addDockable(colourShades)
        colourShades.isVisible = true
        grid.add(1.2, 0.4, 0.4, 0.2, colourShades)

        val colourPalette = DefaultSingleCDockable("colourPalette", "Colour Palette", JPanel().apply {
            isOpaque = false
            layout = BorderLayout()

            val widget = Components.colourPalette
            JScrollPane(widget).also { add(it) }

            // TODO: Move toolbars to an object, so this may be referred to when importing a colour palette and changed
            add(JToolBar().apply {
                val slider = JSlider(1, 100).apply {
                    this.value = 10
                    addChangeListener {
                        Components.colourPalette.scale = this.value / 10.0

                        with(widget.colourList.size * (widget.cellSize * this.value / 100)) {
                            widget.preferredSize = Dimension(this, this)
                        }
                        widget.revalidate()
                    }
                }
                add(JButton(Icons.zoomOut).apply {
                    toolTipText = "Zoom Out"
                    addActionListener {
                        slider.value--
                    }
                })
                add(slider)
                add(JButton(Icons.zoomIn).apply {
                    toolTipText = "Zoom In"
                    addActionListener {
                        slider.value++
                    }
                })
            }, BorderLayout.PAGE_END)
        })
        cControl.addDockable(colourPalette)
        colourPalette.isVisible = true

        val colourLibrary = DefaultSingleCDockable("colourLibrary", "Colour Library", JScrollPane(Components.colourLibrary))
        cControl.addDockable(colourLibrary)
        colourLibrary.isVisible = true
        grid.add(1.2, 0.5, 0.4, 0.2, colourPalette, colourLibrary)

        val actionHistory = DefaultSingleCDockable("actionHistory", "Action History", JPanel().apply {
            isOpaque = false
            layout = BorderLayout()

            add(JScrollPane(Components.actionHistory))

            add(JToolBar().apply {
                add(JButton(Icons.undo).apply {
                    toolTipText = "Undo Action"
                    addActionListener { ActionStack.undo() }
                })
                add(JButton(Icons.redo).apply {
                    toolTipText = "Redo Action"
                    addActionListener { ActionStack.redo() }
                })
                addSeparator()
                add(JButton(Icons.trash).apply {
                    toolTipText = "Delete Action"
                    addActionListener { ActionStack.pop(Components.actionHistory.list.selectedIndex) }
                })
            }, BorderLayout.PAGE_START)
        })
        cControl.addDockable(actionHistory)
        actionHistory.isVisible = true
        grid.add(1.2, 0.0, 0.4, 0.4, actionHistory)

        cControl.contentArea.deploy(grid)

        // TODO: Add a setting for the refresh interval
        // TODO: Only re-draw when an event is performed, and draw onto a buffered image
        Timer(1000 / 60) {
            Components.pixelGrid.repaint()
            // TODO: Change all but the PixelGrid to redraw when the tool is performed
            Components.tiledView.repaint()
            Components.colourPalette.repaint()
            Components.layerList.repaint()
            Components.animationPreview.repaint()
            Components.animationTimeline.repaint()
            Components.miniMap.repaint()
        }.start()
    }

    Components.frame.isVisible = true
    Components.frame.setLocationRelativeTo(null)
}