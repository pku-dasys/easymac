package booth

import chisel3.iotesters.PeekPokeTester
import chisel3.util._
import chisel3.{Bundle, Input, Module, Output, UInt, _}

class Radix4Enc extends Module {
  val io = IO(new Bundle {
    val I = Input(UInt(3.W))
    val Neg = Output(UInt(1.W))
    val One = Output(UInt(1.W))
    val Two = Output(UInt(1.W))
    val Zero = Output(UInt(1.W))
  })

  io.Neg := io.I(2)
  val con1 = (io.I === 0.U(3.W)) || (io.I === 5.U(3.W))
  val con2 = (io.I === 4.U(3.W)) || (io.I === 3.U(3.W))
  io.Zero := con1
  io.Two := con2
  io.One := !io.Zero & !io.Two

}

/** Radix-4 Partial Product Generator
 *
 * @param w the data width
 */
/*
class Radix4Gen(w: Int) extends Module {
 val io = IO(new Bundle {
   val inputs = Input(UInt(w.W))
   val neg = Input(UInt(1.W))
   val zero = Input(UInt(1.W))
   val one = Input(UInt(1.W))
   val two = Input(UInt(1.W))
   val outs = Output(MixedVec(Seq(UInt(w.W))))
 })
 */