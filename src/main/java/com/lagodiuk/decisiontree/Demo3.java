package com.lagodiuk.decisiontree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public class Demo3 {

	public static void main(String[] args) {
		Map<String, List<? extends Predicate>> attributeConditions = new HashMap<String, List<? extends Predicate>>();
		attributeConditions.put("x", Arrays.asList(BasicPredicates.EQUAL, BasicPredicates.GTE, BasicPredicates.LTE));
		attributeConditions.put("y", Arrays.asList(BasicPredicates.EQUAL, BasicPredicates.GTE, BasicPredicates.LTE));

		Random random = new Random();

		List<Item> items = new LinkedList<Item>();
		for (double i = 0; i <= 400; i++) {
			items.add(makeItem((random.nextDouble() - random.nextDouble()) * 14, (random.nextDouble() - random.nextDouble()) * 14));
		}

		DecisionTree classifier = DecisionTree.build(items, attributeConditions);

		display(classifier.getTree(), 300, 300);

		JFrame f1 = new JFrame();
		f1.setSize(300, 300);
		f1.setLocationRelativeTo(null);
		JPanel p = new JPanel();
		f1.add(p);
		f1.setVisible(true);

		BufferedImage bi = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.getGraphics();

		for (int x = 0; x < 200; x++) {
			for (int y = 0; y < 200; y++) {
				Item item = makeEmptyItem((x - 100) / 5, (y - 100) / 5);

				if ("in a circle".equals(classifier.classify(item))) {
					g.setColor(Color.RED);
				} else {
					g.setColor(Color.GREEN);
				}

				g.drawOval(x, y, 2, 2);
			}
		}

		for (;;) {
			p.getGraphics().drawImage(bi, 0, 0, null);
		}
	}

	private static Item makeItem(double x, double y) {
		Item item = new Item();

		if (((x * x) + (y * y)) <= (10 * 10)) {
			item.setCategory("in a circle");
		} else {
			item.setCategory("out of a circle");
		}

		item.setAttribute("x", x);
		item.setAttribute("y", y);

		return item;
	}

	private static Item makeEmptyItem(double x, double y) {
		Item item = new Item();

		item.setAttribute("x", x);
		item.setAttribute("y", y);

		return item;
	}

	public static JFrame display(DefaultMutableTreeNode root, int width, int height) {
		JTree tree = new JTree(root);

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new JScrollPane(tree), BorderLayout.CENTER);
		for (int i = 0; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}
		frame.setSize(width, height);
		// put frame at center of screen
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		return frame;
	}

}