package de.lolhens.pushyreloaded.tile

import de.lolhens.pushyreloaded._

sealed trait BoxTarget extends SimpleTile[BoxTarget] {
  override val image: Image = Image("/assets/images/18.bmp")

  override val ids: List[Int] = List(18)

  override def fromId(id: Int): BoxTarget = this

  override def variants: Seq[BoxTarget] = Seq(this)

  override val pushable: Pushable = Pushable.Empty

  override def pushable(world: World, pos: Vec2i, direction: Direction, by: TileInstance, byPos: Vec2i): (Pushable, () => Unit) =
    by match {
      case Player(_) | Box(_) =>
        super.pushable(world, pos, direction, by, byPos)

      case _ => // TODO: correct?
        Pushable.Solid.withoutAction
    }
}

object BoxTarget extends BoxTarget
