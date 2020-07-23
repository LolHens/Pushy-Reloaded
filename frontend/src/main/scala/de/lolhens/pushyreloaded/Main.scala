package de.lolhens.pushyreloaded

import org.scalajs.dom
import org.scalajs.dom.html.Canvas

import scala.scalajs.js

object Main {
  def main(args: Array[String]): Unit = {
    val canvas = dom.document.getElementById("canvas").asInstanceOf[Canvas]
    //val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

    //canvas.width = (0.95 * dom.window.innerWidth).toInt
    //canvas.height = (0.95 * dom.window.innerHeight).toInt
    //dom.document.body.appendChild(canvas)

    val level = World(Vec2i(20, 12))

    dom.window.addEventListener("keydown", (e: dom.KeyboardEvent) => {
      Option(e.key match {
        case "w" => Direction.Up
        case "a" => Direction.Left
        case "s" => Direction.Down
        case "d" => Direction.Right
        case _ => null
      }).foreach(level.playerMove)
    }, useCapture = false)

    def update(d: Double): Unit = {
      level.render(canvas)
      //if (bgImage.isReady) {
      //  ctx.drawImage(bgImage.element, 0, 0, bgImage.element.width, bgImage.element.height)
      //}
    }

    var prev = js.Date.now()
    // The main game loop
    val gameLoop = () => {
      val now = js.Date.now()
      val delta = now - prev

      update(delta / 1000)
      //render()

      prev = now
    }

    dom.window.setInterval(gameLoop, 1)
  }
}