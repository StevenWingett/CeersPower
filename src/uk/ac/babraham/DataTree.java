package uk.ac.babraham;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class DataTree extends JPanel {

	JTree tree;
	DefaultTreeModel treeModel;
	DefaultMutableTreeNode chicagoNode;
	DefaultMutableTreeNode geneListNode;
	DefaultMutableTreeNode fragmentListNode;

	public DataTree() {

		setLayout(new BorderLayout());

		// Build a bunch of TreeNodes We use DefaultMutableTreeNode because the
		// DefaultTreeModel use it to build a complete tree.
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Datasets");

		// create the child nodes
		chicagoNode = new DefaultMutableTreeNode("CHiCAGO");
		geneListNode = new DefaultMutableTreeNode("GeneLists");
		fragmentListNode = new DefaultMutableTreeNode("FragmentsLists");

		// Build our tree model starting at the root node, and then make a JTree
		// out
		// of it.
		treeModel = new DefaultTreeModel(root);
		tree = new JTree(treeModel);
		add(tree, BorderLayout.CENTER);

		// Build the tree up from the nodes we created.
		root.add(chicagoNode);
		root.add(geneListNode);
		root.add(fragmentListNode);

		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent tse) {

				TreePath[] paths = tree.getSelectionPaths();

				if (paths != null) {
					StringBuffer sb = new StringBuffer();
					sb.append("Selected");

					for (int t = 0; t < paths.length; t++) {
						if (((DefaultMutableTreeNode) paths[t].getLastPathComponent()).isLeaf()) {
							sb.append(" ");
							sb.append(paths[t].getLastPathComponent().toString());
						}
					}
					System.out.println("Selected: " + sb.toString());
				} else {
					System.out.println("Nothing selected in Tree");
				}

			}
		});
	}

	public void addNode(String dataType, String newNodeName) {
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newNodeName);
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		// DefaultMutableTreeNode root =
		// (DefaultMutableTreeNode)model.getRoot();

		if (dataType.equals("CHiCAGO")) {
			chicagoNode.add(newNode);
		} else if (dataType.equals("GeneLists")) {
			geneListNode.add(newNode);
		} else if (dataType.equals("FragmentsLists")) {
			fragmentListNode.add(newNode);
		}

		model.reload();
	}

}