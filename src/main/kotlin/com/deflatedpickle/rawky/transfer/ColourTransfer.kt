package com.deflatedpickle.rawky.transfer

import com.bric.colorpicker.ColorPicker
import com.deflatedpickle.rawky.component.ColourLibrary
import com.deflatedpickle.rawky.component.ColourPalette
import java.awt.Color
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.dnd.DnDConstants
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.TransferHandler

class ColourTransfer(val colour: Color) : Transferable {
    companion object {
        val dataFlavor = DataFlavor(Color::class.java, Color::class.java.canonicalName)

        fun pressedExport(component: JComponent, colour: Color) {
            component.transferHandler = Export(colour)

            component.addMouseListener(object : MouseAdapter() {
                override fun mousePressed(e: MouseEvent) {
                    (e.source as JButton).transferHandler.exportAsDrag(e.source as JButton, e, TransferHandler.COPY)
                }
            })
        }
    }

    override fun getTransferData(flavor: DataFlavor?): Any = colour
    override fun isDataFlavorSupported(flavor: DataFlavor?): Boolean = flavor == dataFlavor
    override fun getTransferDataFlavors(): Array<DataFlavor> = arrayOf(dataFlavor)

    class Export(val colour: Color) : TransferHandler() {
        override fun getSourceActions(c: JComponent?): Int = DnDConstants.ACTION_COPY_OR_MOVE
        override fun createTransferable(c: JComponent?): Transferable? = ColourTransfer(colour)
    }

    class Import : TransferHandler() {
        override fun canImport(support: TransferSupport?): Boolean = support?.isDataFlavorSupported(dataFlavor) ?: false

        override fun importData(support: TransferSupport?): Boolean {
            var accept = false

            support?.let {
                if (canImport(support)) {
                    with(support.transferable.getTransferData(dataFlavor)) {
                        when (val component = support.component) {
                            is ColourPalette -> {
                                component.colourList.add(ColourPalette.ColourSwatch(support.dropLocation.dropPoint.x - component.cellSize / 2, support.dropLocation.dropPoint.y - component.cellSize / 2, this as Color))
                                accept = true
                            }
                            is ColourLibrary -> {
                                component.addButton(this as Color)
                            }
                            is ColorPicker -> {
                                component.color = this as Color
                            }
                            else -> {}
                        }
                    }
                }
            }

            return accept
        }
    }
}