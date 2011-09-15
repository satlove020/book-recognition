package org.k53ca;

import java.io.IOException;
import java.util.Scanner;

public class Test {
	public static native String nativeMatch(String image, String data);
	public static void main(String args[]) {
		Process p[] = new Process[15];
		for(int i = 0; i < 15; ++i) {
			try {
				p[i] = Runtime.getRuntime().exec("java -classpath bin org.k53ca.Client " + i);
				Scanner input = new Scanner(p[i].getInputStream());
				System.out.println("I " + i);
				//while(input.hasNext()) {
					System.out.println(input.nextLine());
					System.out.println(input.nextLine());
					System.out.println(input.nextLine());
				//}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for(int i = 0; i < 15; ++i) {
			Scanner input = new Scanner(p[i].getInputStream());
			System.out.println("I " + i);
			//while(input.hasNext()) {
				System.out.println(input.nextLine());
				System.out.println(input.nextLine());
				System.out.println(input.nextLine());
			//}
		}
	}
}
