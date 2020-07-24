package de.lolhens.pushyreloaded.tile

import de.lolhens.pushyreloaded._

sealed trait House extends SimpleTile[House] {
  override val ids: List[Int] = List(7)

  override def fromId(id: Int): House = this

  override val image: Image = Image("/assets/images/7.bmp")

  override val pushable: Pushable = Pushable.Solid

  override def variants: Seq[House] = Seq(this)

  override def pushable(world: World, pos: Vec2i, direction: Direction, by: TileInstance, byPos: Vec2i): (Pushable, () => Unit) =
    by.as(Player) match {
      case Some(_) =>
        if (world.list.forall(e => e._2.missionComplete(world, e._1))) {
          Pushable.Empty.withAction {
            Main.world = Main.testWorld
          }
        } else
          Pushable.Solid.withoutAction

      case _ =>
        super.pushable(world, pos, direction, by, byPos)
    }
}

object House extends House
