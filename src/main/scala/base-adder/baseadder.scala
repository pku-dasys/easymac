package adder

import chisel3.iotesters.PeekPokeTester
import chisel3.util._
import chisel3.{Bundle, Input, Module, Output, UInt, _}

class BasicAdder(m: Int) extends Module {
  val io = IO(new Bundle {
    val sumand = Input(UInt(m.W))
    val addend = Input(UInt(m.W))

    val res = Output(UInt(m.W))
  })
  io.res := io.sumand + io.addend
}