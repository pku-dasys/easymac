package ct

import ppa._

import chisel3.iotesters.PeekPokeTester
import chisel3.util._
import chisel3.{Bundle, Input, Module, Output, UInt, _}

class MyTrans(m: Int, n: Int, wire: Data) {

  //m columns and n rows
  val data = wire.asTypeOf(Vec(m*n, UInt(1.W)))
  
  def get(x: Int, y: Int, trans: Boolean): Data = {
    //从左往右，从上往下，x横坐标，y纵坐标，从1开始计数
    var index = 0
    if (x <= n + 1) {
      if (trans || x >= n) {
        index = x * (x - 1) / 2 + y
      } else {
        index = x * (x - 1) / 2 + y - (n - x)

        println(x, y, index)
      }
      data(index - 1)
    } else if (x <= m) {
      index = n * (n + 1) / 2 + n * (x - 1 - n) + y
      data(index - 1)

    } else {
      index = n * (n + 1) / 2 + n * (m - n) + (2 * n + m - x) * (x - 1 - m) / 2 + y
      data(index - 1)
    }
  }
}


class CT(m:Int, n:Int) extends Module{
  val io = IO(new Bundle{
    val inputs = Input(UInt((m*n).W))
    val outputs = Output(Vec(2, UInt((m+n).W)))
  })
}

