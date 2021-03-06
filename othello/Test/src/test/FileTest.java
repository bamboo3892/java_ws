package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class FileTest implements Serializable {

	private static final long serialVersionUID = 1268300345088988379L;

	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		File file1 = new File(System.getProperty("user.dir"), "file2");
		File file2 = new File(System.getProperty("user.dir"), "file2");
		System.out.println(file1);
//		file1.createNewFile();
		ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file1));
//		output.writeObject(1000);
		ObjectInputStream input = new ObjectInputStream(new FileInputStream(file2));
		Object o = input.readObject();
		System.out.println(o.getClass());
		System.out.println(o);
		input.close();
		output.close();
	}

	private int n;
	private int m;
	private String k;

	public FileTest set(int n, int m, String k) {
		this.n = n;
		this.m = m;
		this.k = k;
		return this;
	}

	public String toString() {
		return n + ", " + m + ", " + k;
	}

}
