package org.balancemirror

import java.awt.image.BufferedImage

import scala.swing.Button
import scala.swing.CheckBox
import scala.swing.Dimension
import scala.swing.FlowPanel
import scala.swing.Graphics2D
import scala.swing.MainFrame
import scala.swing.Panel
import scala.swing.SimpleSwingApplication
import scala.swing.Swing
import scala.swing.event.ButtonClicked
import scala.swing.event.WindowActivated
import scala.swing.event.WindowDeactivated

object BalanceMirrorApp extends SimpleSwingApplication {

  def top = new MainFrame() {
    title = "Balance Mirror"
    var image: Option[BufferedImage] = None
    var faceFind = false

    def captureAndPaint() = {
      image = VideoCapture.getImage(faceFind)
      if (image.nonEmpty) panel.repaint
    }

    object startButton extends Button {
      text = "Start Capture"
      reactions += {
        case ButtonClicked(_) => { VideoCapture.startCapture(); captureAndPaint }
      }
    }

    object stopButton extends Button {
      text = "Stop Capture"
      reactions += {
        case ButtonClicked(_) => { VideoCapture.stopCapture() }
      }
    }

    object checkBox extends CheckBox {
      text = "Centre on face"
      reactions += {
        case ButtonClicked(v) => { println("changed"); faceFind = v.selected }
      }
    }

    object panel extends Panel {
      override def paint(g: Graphics2D) = {
        image.map { g.drawImage(_, 0, 0, null) }
        if (VideoCapture.isCapturing) captureAndPaint
      }
      val s = new Dimension(640 * 2, 480)
      minimumSize = s
      maximumSize = s
      preferredSize = s
    }
    
    contents = new FlowPanel {
      contents.append(startButton, stopButton, checkBox, panel)
      border = Swing.EmptyBorder(5, 5, 5, 5)
    }
    
    reactions += {
      case WindowDeactivated(window) => VideoCapture.closeCamera()
      case WindowActivated(window) => VideoCapture.openCamera()
    }
  }
}