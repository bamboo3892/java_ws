package com.test.main;

public class A {

	public static A a = new A();

	public static void main(String[] args) {
		double f1 = 1F;
		String str = Long.toBinaryString(Double.doubleToLongBits(f1));
		System.out.println(str);
		double f2 = Double.longBitsToDouble(Long.parseLong("11111111101111111111111111111111111111111111111111111111111111", 2));
		System.out.println(f2);

		System.out.println(null == null);

		int n1 = 0 % 1;
		//int n2 = 1 % 0;

		Super aaa = a.new Super();
		Sub bbb = a.new Sub();
		aaa.f();
		bbb.f();

	}

	public class Super {

		public String name = "super";

		public void f() {
			m();
		}

		public void m() {
			System.out.println(name);
		}

	}

	public class Sub extends Super {

		public String name = "sub";

		@Override
		public void m() {
			System.out.println(name);
		}

	}

}
