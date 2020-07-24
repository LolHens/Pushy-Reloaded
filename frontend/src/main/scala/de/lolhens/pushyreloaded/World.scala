package de.lolhens.pushyreloaded

import de.lolhens.pushyreloaded.World.WorldTile
import de.lolhens.pushyreloaded.tile._
import org.scalajs.dom
import org.scalajs.dom.html.Canvas

class World private(size: Vec2i,
                    private var worldTiles: Seq[WorldTile[TileInstance]]) {
  ((0 until size.x).map(Vec2i(_, 0)) ++
    (0 until size.y).map(Vec2i(0, _)) ++
    (0 until size.x).map(Vec2i(_, size.y - 1)) ++
    (0 until size.y).map(Vec2i(size.x - 1, _))).foreach(add(_, Wall))
  add(Vec2i(1, 1), Player(Direction.Up))
  add(Vec2i(2, 2), Ball(Ball.Color.Red))
  add(Vec2i(3, 2), Ball(Ball.Color.Green))
  add(Vec2i(5, 4), BallHole(Ball.Color.Red))
  add(Vec2i(7, 4), BallHole(Ball.Color.Green))
  add(Vec2i(8, 6), Box)
  add(Vec2i(10, 6), BoxTarget)
  add(size.map(_ - 2, _ - 2), House)

  def list: Seq[WorldTile[TileInstance]] =
    worldTiles.filterNot(_.instance == Background)

  def list(pos: Vec2i): Seq[WorldTile[TileInstance]] =
    worldTiles.iterator.filter(_.pos == pos).filterNot(_.instance == Background).toSeq

  def get[Instance <: TileInstance](pos: Vec2i, factory: TileFactory[Instance]): Seq[WorldTile[Instance]] =
    worldTiles.collect {
      case tile: WorldTile[Instance]@unchecked if tile.pos == pos && tile.instance.factory == factory =>
        tile
    }

  def get[Instance <: TileInstance](factory: TileFactory[Instance]): Seq[WorldTile[Instance]] =
    worldTiles.flatMap(_.as(factory))

  def add(pos: Vec2i, tile: TileInstance): Unit = {
    worldTiles = new WorldTile(tile, pos) +: worldTiles
  }

  def remove(tile: WorldTile[_]): Unit =
    worldTiles = worldTiles.filterNot(_ == tile)

  def playerMove(direction: Direction): Unit = {
    get(Player).foreach { tile =>
      /*val pos = tile.pos
      val newPos = pos.offset(direction)
      val newPos2 = newPos.offset(direction)
      val newPosTiles = list(newPos)
      val pushable = newPosTiles.exists { e =>
        val physics = e.instance.pushable
        physics == Pushable.Solid || (physics == Pushable.Pushable && list(newPos2).exists(_.instance.pushable != Pushable.Empty))
      }
      newPosTiles.foreach(e => if (e.instance.pushable == Pushable.Pushable) e.moveTo(newPos2))
      tile.moveTo(newPos)
      tile.instance.direction = direction*/
      tile.instance.move(this, tile.pos, direction)
    }
  }

  def render(canvas: Canvas): Unit = {
    canvas.width = size.x * TileInstance.size.x
    canvas.height = size.y * TileInstance.size.y

    val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

    for {
      y <- 0 until size.y
      x <- 0 until size.x
      pos = Vec2i(x, y)
    } {
      val renderPos = pos.map(_ * TileInstance.size.x, _ * TileInstance.size.y)
      val sortedTiles: Seq[TileInstance] = (Background +: list(pos).map(_.instance)).sortBy(_.zIndex)
      sortedTiles.foreach(e => e.render(ctx, renderPos))
    }
  }
}

object World {
  def apply(size: Vec2i): World =
    new World(size, Seq.empty)

  class WorldTile[Instance <: TileInstance](val instance: Instance,
                                            var pos: Vec2i) {
    def moveTo(pos: Vec2i): Unit = this.pos = pos

    def move(f: Vec2i => Vec2i): Unit = moveTo(f(pos))

    final def as[NewInstance <: TileInstance](factory: TileFactory[NewInstance]): Option[WorldTile[NewInstance]] =
      if (instance.is(factory)) Some(this.asInstanceOf[WorldTile[NewInstance]]) else None
  }

}
