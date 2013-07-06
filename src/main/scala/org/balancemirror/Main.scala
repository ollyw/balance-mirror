package org.balancemirror

import org.opencv.core.Core

object Main {

  def main(args: Array[String]): Unit = {
    println(System.getProperty("java.library.path"))

    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

    BalanceMirrorApp.main(args)
  }
}