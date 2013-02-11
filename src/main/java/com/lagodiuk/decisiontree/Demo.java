package com.lagodiuk.decisiontree;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public class Demo {

	public static void main(String[] args) {
		Map<String, List<? extends Predicate>> attributeConditions = new HashMap<String, List<? extends Predicate>>();
		attributeConditions.put("Соперник", Arrays.asList(BasicPredicates.EQUAL));
		attributeConditions.put("Играем", Arrays.asList(BasicPredicates.EQUAL));
		attributeConditions.put("Лидеры", Arrays.asList(BasicPredicates.EQUAL));
		attributeConditions.put("Дождь", Arrays.asList(BasicPredicates.EQUAL));

		List<Item> items = Arrays.asList(
				makeItem("Выше", "Дома", "На месте", "Да", "Loose"),
				makeItem("Выше", "Дома", "На месте", "Нет", "Win"),
				makeItem("Выше", "Дома", "Пропускают", "Нет", "Loose"),
				makeItem("Ниже", "Дома", "Пропускают", "Нет", "Win"),
				makeItem("Ниже", "В гостях", "Пропускают", "Нет", "Loose"),
				makeItem("Ниже", "Дома", "Пропускают", "Да", "Win"),
				makeItem("Выше", "В гостях", "На месте", "Да", "Loose"));

		DecisionTree classifier = DecisionTree.build(items, attributeConditions);

		display(classifier.getTree(), 300, 300);

		System.out.println(classifier.classify(makeItem("Ниже", "Дома", "На месте", "Нет", null)));
	}

	private static Item makeItem(String sopernik, String igraem, String lideru, String dojd, String win) {
		Item item = new Item();

		item.setCategory(win);

		item.setAttribute("Соперник", sopernik);
		item.setAttribute("Играем", igraem);
		item.setAttribute("Лидеры", lideru);
		item.setAttribute("Дождь", dojd);

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