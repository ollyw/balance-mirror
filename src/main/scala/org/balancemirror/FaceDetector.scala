package org.balancemirror

import scala.util.Failure
import scala.util.Success
import scala.util.Try

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfRect
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.objdetect.CascadeClassifier

object FaceDetector {

  // Create a face detector from the cascade file in the resources
  // directory.
  val resource = Option(getClass.getResource("/lbpcascade_frontalface.xml")) match {
    case Some(x) => x
    case None => throw new Exception("Resource not loaded")
  }

  val filterPath = Try(resource.getPath) match {
    case Failure(e) => { println(e.getMessage); throw e }
    case Success(f) => f
  }

  val faceDetector = new CascadeClassifier(filterPath)
      
  println(s"Filter file path is ${filterPath}")

  def run(image: Mat) = {

    val faceDetector = new CascadeClassifier(filterPath)

    // Detect faces in the image.
    // MatOfRect is a special container class for Rect.
    val faceDetections = new MatOfRect()
    faceDetector.detectMultiScale(image, faceDetections)

    val faceCount = faceDetections.toArray.length
    println(s"Detected ${faceCount} faces")

    // Draw a bounding box around each face.
    faceDetections.toArray().foreach { f =>
      Core.rectangle(image, new Point(f.x, f.y), new Point(f.x + f.width, f.y + f.height), new Scalar(0, 255, 0))
    }

    image
  }

  def findFace(image: Mat) = {

    // Detect faces in the image.
    // MatOfRect is a special container class for Rect.
    val faceDetections = new MatOfRect()
    faceDetector.detectMultiScale(image, faceDetections)

    val faceCount = faceDetections.toArray.length
    println(s"Detected ${faceCount} faces, and ignoring all but one")

    // Draw a bounding box around each face.
    faceDetections.toArray.sortBy(r => r.area).lastOption
  }
}
