/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dia.tensors

/**
 * An abstract tensor
 */
trait AbstractTensor extends Serializable with SliceableArray {

  type T <: AbstractTensor
  val name: String
  val LOG = org.slf4j.LoggerFactory.getLogger(this.getClass)

  def reshape(shape: Array[Int]): T
  def broadcast(shape: Array[Int]): T
  def zeros(shape: Int*): T

  def map(f: Double => Double): AbstractTensor

  /**
   * Indexed Operations
   */

  def put(value: Double, shape: Int*): Unit

  /**
   * Element-wise Operations
   */

  def +(array: AbstractTensor): T
  def +(scalar: Double): T

  def -(array: AbstractTensor): T
  def -(scalar: Double): T

  def *(array: AbstractTensor): T
  def *(scalar: Double): T

  def /(array: AbstractTensor): T
  def /(scalar: Double): T

  def +=(array: AbstractTensor): T
  def +=(scalar: Double): T

  def -=(array: AbstractTensor): T
  def -=(scalar: Double): T

  def *=(array: AbstractTensor): T
  def *=(scalar: Double): T

  def /=(array: AbstractTensor): T
  def /=(scalar: Double): T

  /**
   * Linear Algebra Operations
   */

  def **(array: AbstractTensor): T

  def div(num: Double): T

  /**
   * Masking operations
   */
  def mask(f: Double => Boolean, mask: Double) : T
  def setMask(num: Double): T
  def <(num: Double): T
  def >(num: Double): T
  def <=(num: Double): T
  def >=(num: Double): T
  def :=(num: Double): T
  def !=(num: Double): T

  /**
   * Returns the data as a flattened array
   *
   */
  def data: Array[Double]

   /**
   * Returns the data dimensions
   *
   */
  def shape: Array[Int]

  /**
   * Utility Methods
   */

  def cumsum: Double
  def mean(axis : Int*) : T
  def detrend(axis: Int) : T
  def std(axis: Int*): T
  def skew(axis: Int*): T
  def assign(newTensor: AbstractTensor) : T
  def toString: String

  /**
   * Due to properties of Doubles, the equals method
   * utilizes the percent error rather than checking absolute equality.
   * The threshold for percent error is if it is greater than 0.5% or .005.
   * @param any
   * @return
   */
  override def equals(any: Any): Boolean = {
    val array = any.asInstanceOf[AbstractTensor]
    val shape = array.shape
    val thisShape = this.shape

    if(!shape.sameElements(thisShape)) return false

    val thisData = this.data
    val otherData = array.data
    for(index <- 0 to thisData.length - 1) {
      val left = thisData(index)
      val right = otherData(index)
      if(left != 0.0 && right == 0.0) {
        return false
      } else if (right == 0.0 && left != 0.0) {
        return false
      } else if ( right != 0.0 && left != 0.0) {
        val absoluteError = Math.abs(left - right)
        val percentageError = absoluteError / Math.max(left, right)
        if(percentageError > 5E-3) {
          return false
        }
      }

    }
    true
  }

  override def hashCode(): Int = super.hashCode()

  def copy: T

  def isZero: Boolean
  /**
   *  Shortcut test whether tensor is zero
   *  in case we know its entries are all non-negative.
   */
  def isZeroShortcut: Boolean
  def max: Double
  def min: Double

}
