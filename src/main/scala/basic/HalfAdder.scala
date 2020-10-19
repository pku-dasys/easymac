package basic

import chisel3.iotesters.PeekPokeTester
import chisel3.util._
import chisel3.{Bundle, Input, Module, Output, UInt, _}

class HalfAdder extends Module {
  val io  = IO(new Bundle {
    val A = Input(UInt(1.W))
    val B = Input(UInt(1.W))
    val Sum = Output(UInt(1.W))
    val Cout = Output(UInt(1.W))
    })

  io.Sum := io.A ^ io.B
  io.Cout := io.A & io.B
}
