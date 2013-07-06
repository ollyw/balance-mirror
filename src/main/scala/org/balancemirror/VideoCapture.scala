package org.balancemirror

import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream

import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.core.MatOfRect
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.highgui.Highgui

import javax.imageio.ImageIO

import scala.util.Failure
import scala.util.Success
import scala.util.Try

object VideoCapture {
  private lazy val capture = new org.opencv.highgui.VideoCapture()
  private var capturing = false
  private var closingCamera = false

  def getImage(findFace: Boolean) = {
    if (closingCamera) {
      capture.release()
      closingCamera = false
      capturing = false
      None
    } else if (capturing) {
      run(findFace)
    } else None
  }

  def stopCapture() = {
    capturing = false
  }

  def isCapturing = capturing
  def startCapture() = capturing = true

  def closeCamera() = closingCamera = true

  def openCamera() = {

    println(s"opened? ${capture.open(0)}")

    // Capturing from the camera before it has initialised causes errors
    Thread.sleep(2000);

    if (!capture.isOpened()) {
      println("Camera Error")
    } else {
      println("Camera Opened?")
    }
  }

  def offsetImage(image: Mat, rect: Rect) = {
    // TODO Implement with centering
    image.submat(rect)
  }

  def run(findFace: Boolean): Option[BufferedImage] = {

    val rawImage = new Mat()
    if (capture.read(rawImage)) {

	    val frame = if (!findFace) rawImage
	    else FaceDetector.findFace(rawImage) match {
	      case Some(rect) => offsetImage(rawImage, rect)
	      case None => rawImage
	    }
	
	    val outputFrame = new MatOfRect()
	    outputFrame.create(rawImage.rows, rawImage.cols * 2, rawImage.`type`)
	
	    outputFrame.setTo(new Scalar(0, 0, 0))
	
	    // draw left side
	    for {
	      row <- 0 until frame.rows
	      col <- 0 until (frame.cols / 2)
	    } yield {
	      val pixel = frame.get(row, col)
	      outputFrame.put(row, col, pixel: _*)
	      outputFrame.put(row, frame.cols - 1 - col, pixel: _*)
	    }
	
	    // draw right side
	    for {
	      row <- 0 until frame.rows
	      col <- (frame.cols / 2) until frame.cols
	    } yield {
	      val pixel = frame.get(row, col)
	      outputFrame.put(row, (outputFrame.cols / 2) - 1 + col, pixel: _*)
	      outputFrame.put(row, (outputFrame.cols / 2) + frame.cols - 1 - col, pixel: _*)
	    }
	
	    //Highgui.imwrite(s"camera${x}.jpg", outputFrame);
	
	    val matOfByte = new MatOfByte()
	
	    Highgui.imencode(".bmp", outputFrame, matOfByte);
	
	    val byteArray = matOfByte.toArray();
	
	    Try({
	      val in = new ByteArrayInputStream(byteArray);
	      ImageIO.read(in);
	    }) match {
	      case Success(img) => Some(img)
	      case Failure(e) => { e.printStackTrace(); None }
	    }
	} else None
  }
}