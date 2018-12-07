package com.sun.utils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;

import com.google.common.collect.Lists;

public class Java7FileTest {

	@Test
	public void testWalk() {
		Path root = Paths.get("/Users/jerry/code");
		try {
			// Stream<Path> paths = Files.walk(root, 1);

			List<Path> result = Lists.newLinkedList();
			Files.walkFileTree(root, new FindJavaVisitor(result));
			// for(int i =0; i < 10; i++){
			// System.out.println(result.get(i));
			// }
			System.out.println("result.size()=" + result.size());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Java7FileTest test = new Java7FileTest();
		test.testWalk();
	}

	private static class FindJavaVisitor extends SimpleFileVisitor<Path> {
		private List<Path> result;

		public FindJavaVisitor(List<Path> result) {
			this.result = result;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
			if (file.toString().endsWith(".java")) {
				result.add(file.getFileName());
			}
			return FileVisitResult.CONTINUE;
		}
	}
}
