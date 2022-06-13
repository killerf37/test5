package com.ginfon.scada.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

/**
 * 序列号对象。用来计算报文的序列号的，主要是处理边界问题，报文的序列号只有两个字节，最大数值是有上限的。 当序列号计算达到边界后就从0重新计算。<br>
 * 默认边界为{@link Integer#MAX_VALUE}，也可以手动指定。<br>
 * 这个类本质是封装了{@link AtomicInteger}，计算序列号的操作是原子性的。
 * 
 * @author Mark
 *
 */
public final class SerialNumber {
	
	/**
	 * 	工具类，本质。
	 */
	private AtomicInteger atomicInteger;
	
	/**
	 * 	越界处理器。
	 */
	private IntUnaryOperator intUnaryOperator;

	/**
	 * 	边界。
	 */
	private int maxNumber;
	
	/**
	 * 	初始值。
	 */
	private int initialValue;
	
	/**
	 * 	默认构造器，边界为{@link Integer#MAX_VALUE}。
	 */
	public SerialNumber() {
		this(Integer.MAX_VALUE);
	}

	/**
	 * 	指定边界的构造器，边界为负数时没有意义，因为序列号不可能小于0，请勿设置小于0的数字。
	 * @param maxNumber	边界数字。
	 */
	public SerialNumber(int maxNumber) {
		this(0, maxNumber);
	}
	
	/**
	 * 	指定边界的构造器，边界为负数时没有意义，因为序列号不可能小于0，请勿设置小于0的数字。
	 * @param initialValue	初始值
	 * @param maxNumber	边界数字。
	 */
	public SerialNumber(int initialValue, int maxNumber) {
		if(maxNumber < 0)
			throw new IllegalArgumentException("序列号边界不能小于0！");
		this.maxNumber = maxNumber;
		this.initialValue = initialValue;
		this.intUnaryOperator = new IntUnaryOperatorImple(this);
		this.atomicInteger = new AtomicInteger(initialValue);
	}
	/**
	 * 	取出一个序列号。
	 * @return	序列号
	 */
	public int get() {
		return this.atomicInteger.getAndUpdate(this.intUnaryOperator);
	}
	
	/**
	 * 	一个单纯的工具类。
	 * @author Mark
	 *
	 */
	private static class IntUnaryOperatorImple implements IntUnaryOperator {
		
		/**
		 * 	序列号对象。
		 */
		private SerialNumber serialNumber;
		
		IntUnaryOperatorImple(SerialNumber serialNumber) {
			this.serialNumber = serialNumber;
		}
		
		@Override
		public int applyAsInt(int current) {
			return current >= this.serialNumber.maxNumber ? this.serialNumber.initialValue : current + 1;
		}
	}
}
