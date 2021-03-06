/*******************************************************************************
 * The MIT License (MIT)
 * 
 * Copyright (c) 2013 Yurii Lahodiuk (yura_lagodiuk@ukr.net)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.lagodiuk.decisiontree.demo;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.lagodiuk.decisiontree.DecisionTree;
import com.lagodiuk.decisiontree.DecisionTreeBuilder;
import com.lagodiuk.decisiontree.Item;
import com.lagodiuk.decisiontree.Predicate;
import com.lagodiuk.decisiontree.RandomForest;

public class MainDemo {

	private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private static final int TRAINING_SET_SIZE = 2000;

	private static final int WIDTH = 150;

	private static final int HEIGHT = 150;

	private static final int RADIUS = 40;

	private static Random random = new Random();

	private static enum Type {
		RED, GREEN
	}

	public static void main(String[] args) throws Exception {
		List<Item> trainingSet = createTrainingSet();
		log.info("Training set created");

		DecisionTreeBuilder tbuilder =
				DecisionTree
						.createBuilder()
						.setTrainingSet(trainingSet)
						.setDefaultPredicates(Predicate.GTE, Predicate.LTE);

		DecisionTree tree = tbuilder.createDecisionTree();
		log.info("Decision tree created");

		RandomForest forest = tbuilder.createRandomForest(7);
		log.info("Random forest created");

		displayResults(args, trainingSet, tree, forest);
	}

	private static List<Item> createTrainingSet() {
		List<Item> items = new ArrayList<Item>(TRAINING_SET_SIZE);

		for (int i = 0; i < TRAINING_SET_SIZE; i++) {
			int x = random.nextInt(WIDTH);
			int y = random.nextInt(HEIGHT);

			Item item = newItem(x, y);

			if (isRed(x, y)) {
				item.setCategory(Type.RED);
			} else {
				item.setCategory(Type.GREEN);
			}

			items.add(item);
		}
		return items;
	}

	private static Item newItem(int x, int y) {
		return new Item()
				.setAttribute("x", x)
				.setAttribute("y", y);
	}

	private static boolean isRed(int x, int y) {
		x = x - (WIDTH / 2);
		y = y - (HEIGHT / 2);
		return ((x * x) + (y * y)) <= (RADIUS * RADIUS);
	}

	private static void displayResults(String[] args, List<Item> trainingSet, DecisionTree tree, RandomForest forest) throws IOException {
		BufferedImage bi = createBufferedImage();

		displayTrainingSet(trainingSet, bi);

		displayDecisionTree(tree, bi);

		displayForest(forest, bi);

		saveToFile(args, bi);
	}

	private static BufferedImage createBufferedImage() {
		return new BufferedImage((WIDTH * 3) + 2, HEIGHT, BufferedImage.TYPE_INT_RGB);
	}

	private static void displayTrainingSet(List<Item> trainingSet, BufferedImage bi) {
		for (Item item : trainingSet) {
			int x = (Integer) item.getFieldValue("x");
			int y = (Integer) item.getFieldValue("y");

			switch (((Type) item.getCategory())) {
				case RED:
					bi.setRGB(x, y, Color.RED.getRGB());
					break;
				case GREEN:
					bi.setRGB(x, y, Color.GREEN.getRGB());
					break;
			}
		}
	}

	private static void displayDecisionTree(DecisionTree tree, BufferedImage bi) {
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {

				Item item = newItem(x, y);

				switch ((Type) tree.classify(item)) {
					case RED:
						bi.setRGB(x + WIDTH + 1, y, Color.RED.getRGB());
						break;
					case GREEN:
						bi.setRGB(x + WIDTH + 1, y, Color.GREEN.getRGB());
						break;
				}
			}
		}
	}

	private static void displayForest(RandomForest forest, BufferedImage bi) {
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {

				Item item = newItem(x, y);

				Map<Object, Integer> result = forest.classify(item);

				int red = 0;
				int green = 0;

				for (Object color : result.keySet()) {
					if (Type.RED.equals(color)) {
						red = result.get(color);
					} else if (Type.GREEN.equals(color)) {
						green = result.get(color);
					}
				}

				float fRed = ((float) red) / (red + green);
				float fGreen = ((float) green) / (red + green);

				bi.setRGB(x + (WIDTH * 2) + 2, y, new Color(fRed, fGreen, 0).getRGB());
			}
		}
	}

	private static void saveToFile(String[] args, BufferedImage bi) throws IOException {
		String outPath = "./img/test.png";
		if (args.length > 0) {
			outPath = args[0];
		}
		ImageIO.write(bi, "png", new File(outPath));
	}
}
