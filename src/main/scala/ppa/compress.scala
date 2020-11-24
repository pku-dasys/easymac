package ppa

import basic.FullAdder
import chisel3._
import chisel3.{Bundle, Input, Module, Output, UInt, Vec}

import scala.collection.mutable.ArrayBuffer

class compress extends Module {
  val inputWidth = 64
  val outputWidth = 24
  val io = IO(new Bundle {
//    val multiplicand = Input(UInt(m.W))
//    val multiplier = Input(UInt(n.W))
    val in = Output(Vec(inputWidth, UInt(1.W)))
    val out = Output(Vec(outputWidth, UInt(1.W)))
  })
  var wireMap = Map[Int, Data]()

  for(i <- 0 until 64){
    wireMap += i -> io.in(i)
  }

  while(val array = readline()){
    val size = array.size
    if(size == 5){
      val a = Module(new FullAdder)
      a.io.A := wireMap(array(0))
      a.io.B := wireMap(array(1))
      a.io.Cin := wireMap(array(2))
      wireMap += array(3) -> a.io.Sum
      wireMap += array(4) -> a.io.Cout
    }else{

    }
  }

  val pp = Array.ofDim(15,2)
  val outArray = ArrayBuffer[Int]()
  for(x <- 0 until 15){
    for (y <- 0 until 2){
      val a = pp(x)(y)
      if(a !=0){
        outArray.append(a)
      }
    }
  }

  for(i<-0 until 24){
    io.out(i) := wireMap(outArray(i))
  }

}
