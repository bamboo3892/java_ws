package main;

import java.util.LinkedList;
import java.util.List;

public class Test {

	public static void main(String args[]) {
		int[][] box = new int[8][8];
		a(box);
		int[][] clone = box.clone();
		System.out.println(box == clone);
		System.out.println(box.equals(clone));
		print(box);
		print(clone);

		System.out.println("\n////////////////////////////////////////////////////////////////////////////////////////////////////\n");

		List<Integer> list1 = new LinkedList<Integer>();
		for (int i = 0; i < 10; i++){
			list1.add(i);
		}
		List<Integer> list2 = list1.subList(0, 5);
		print(list1);
		print(list2);
	}

	public static void a(int[][] box) {
		box[4][4] = 1;
	}

	public static void print(int[][] box) {
		for (int x = 0; x < 8; x++){
			for (int y = 0; y < 8; y++){
				System.out.print(box[x][y]);
			}
			System.out.println("");
		}
	}

	public static <T> void print(List<T> list) {
		for (T t : list){
			System.out.println(t);
		}
	}

}
