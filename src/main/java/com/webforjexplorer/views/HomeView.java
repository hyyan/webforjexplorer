package com.webforjexplorer.views;

import static com.webforj.component.tree.Tree.node;

import java.io.File;
import com.webforj.component.Composite;
import com.webforj.component.html.elements.H1;
import com.webforj.component.icons.TablerIcon;
import com.webforj.component.layout.applayout.AppDrawerToggle;
import com.webforj.component.layout.applayout.AppLayout;
import com.webforj.component.layout.toolbar.Toolbar;
import com.webforj.component.optiondialog.FileChooserDialog;
import com.webforj.component.tree.Tree;
import com.webforj.component.tree.TreeNode;
import com.webforj.component.tree.event.TreeExpandEvent;
import com.webforj.component.tree.event.TreeSelectEvent;
import com.webforj.router.annotation.Route;
import com.webforj.router.event.DidEnterEvent;
import com.webforj.router.history.ParametersBag;
import com.webforj.router.observer.DidEnterObserver;
import com.webforjexplorer.components.Editor;
import com.webforjexplorer.utils.Utility;

@Route("/")
public class HomeView extends Composite<AppLayout> implements DidEnterObserver {
  private static final String SPINNER = "<dwc-spinner></dwc-spinner>";
  private static final String EMPTY = "(empty)";
  private AppLayout self = getBoundComponent();
  private Tree tree = new Tree();
  private Editor editor = new Editor();

  public HomeView() {
    self.setHeaderOffscreen(false);

    Toolbar toolbar = new Toolbar();
    toolbar.addToStart(new AppDrawerToggle());
    toolbar.addToTitle(new H1("webforJ Explorer"));
    self.addToHeader(toolbar);

    self.addToDrawer(tree);
    self.add(editor);

    tree.addSelectListener(this::handleTreeSelect);
    tree.addExpandListener(this::handleTreeExpand);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onDidEnter(DidEnterEvent event, ParametersBag parameters) {
    promptForDirectory();
  }

  private void promptForDirectory() {
    var dialog = new FileChooserDialog("Choose a project to open");
    dialog.setSelectionMode(FileChooserDialog.SelectionMode.DIRECTORIES);
    String dir = dialog.show();
    if (dir != null) {
      initializeTree(dir);
    } else {
      promptForDirectory();
    }
  }

  private void initializeTree(String startPath) {
    File rootDirectory = new File(startPath);
    if (!rootDirectory.isDirectory()) {
      return;
    }

    Utility.processDirectoryContents(rootDirectory, child -> {
      TreeNode childNode = createNode(child);
      tree.add(childNode);
    });
  }

  private TreeNode createNode(File file) {
    String name = file.getName().isEmpty() ? file.getAbsolutePath() : file.getName();
    TreeNode node = node(name);
    node.setUserData("file", file);

    node.setTooltipText(Utility.getFileTooltip(file));

    if (file.isDirectory()) {
      node.add(node(SPINNER));
    } else if (file.isFile()) {
      Utility.setFileIcons(node, file);
    }

    return node;
  }

  private void addChildrenToNode(TreeNode parentNode, File directory) {
    if (directory.isDirectory()) {
      Utility.processDirectoryContents(directory, child -> {
        TreeNode childNode = createNode(child);
        parentNode.add(childNode);
      });
    } else {
      parentNode.add(node(EMPTY));
    }
  }

  private void handleTreeSelect(TreeSelectEvent event) {
    TreeNode selectedNode = event.getNode();
    Object data = selectedNode.getUserData("file");
    if (data instanceof File file && file.isFile()) {
      try {
        String content = Utility.getFileContent(file);
        editor.setValue(content);

        editor.setLanguage(Utility.getFileLanguage(file.getName()));
      } catch (java.io.IOException e) {
        editor.setValue("Error reading file: " + e.getMessage());
      }
    }
  }

  private void handleTreeExpand(TreeExpandEvent event) {
    TreeNode expandedNode = event.getNode();
    Object data = expandedNode.getUserData("file");
    if (!(data instanceof File file) || !file.isDirectory()) {
      return;
    }

    if (expandedNode.getChildren().size() == 1 &&
        SPINNER.equals(expandedNode.getChildren().get(0).getText())) {
      expandedNode.remove(expandedNode.getChildren().get(0));
      addChildrenToNode(expandedNode, file);
    }
  }
}
