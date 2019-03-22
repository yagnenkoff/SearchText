package com.yagnenkoff.prj1;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ControllerForm {

    final private DirectoryChooser directoryChooser = new DirectoryChooser();

    @FXML
    private TabPane textFiles;

    @FXML
    private TreeView<File> searchTree;

    @FXML
    private Button selectDirectory;

    @FXML
    private Button searchText;

    @FXML
    private TextField textForSearch;

    @FXML
    private TextField pathDirectory;

    @FXML
    private TextField choiceFormatFile;

    @FXML
    private ProgressIndicator progressIndicator;


    @FXML
    void initialize() {
        MultipleSelectionModel<TreeItem<File>> selectionModel = searchTree.getSelectionModel();
        //list open files
        List<File> listOpenFiles = new ArrayList<>();
        // listener file tab
        selectionModel.selectedItemProperty().addListener((changed, oldValue, newValue) -> {
            if (newValue!=null) {
                File file = newValue.getValue();
                if (newValue.getValue().isFile()) {
                    if (!listOpenFiles.contains(file)) {
                        Scanner sc = null;
                        try {
                            sc = new Scanner(file, "windows-1251");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        TextArea textArea = new TextArea();
                        assert sc != null;
                        while (sc.hasNextLine()) {
                            String s = sc.nextLine();
                            textArea.appendText(s);
                            textArea.appendText("\n");
                        }

                        Tab tab = new Tab(file.getName());
                        tab.setOnClosed(event -> listOpenFiles.remove(file));
                        tab.setContent(textArea);
                        textFiles.setTabMinHeight(20);
                        textFiles.setTabMinWidth(75);
                        textFiles.getTabs().add(tab);
                        listOpenFiles.add(file);
                    }
                }
            }
        });

        //configure root tree
        TreeItem<File> root = new TreeItem<>(new File("Files"));
        root.setExpanded(true);
        searchTree.setRoot(root);

        configuringDirectoryChooser(directoryChooser);
        choiceFormatFile.setText("log");

        //button click select dir
        selectDirectory.setOnAction(event -> {
            Node source = (Node) event.getSource();
            Window theStage = source.getScene().getWindow();
            File dir = directoryChooser.showDialog(theStage);
            if (dir != null) {
                pathDirectory.setText(dir.getAbsolutePath());
            } else {
                pathDirectory.setText(null);
            }
        });

        //button click search text
        searchText.setOnAction(event -> {
            root.getChildren().clear();
            textFiles.getTabs().clear();
            listOpenFiles.clear();

            //Valid
            boolean search = ValidateText.textFieldNotEmpty(textForSearch);
            boolean path = ValidateText.textFieldNotEmpty(pathDirectory);
            boolean format = ValidateText.textFieldNotEmpty(choiceFormatFile);
            boolean fileExists = ValidateText.fileExist(pathDirectory);
            if (search && path && format) {
                if (fileExists) {

                    Task<Void> task = new Task<>() {
                        @Override
                        public Void call() {
                            progressIndicator.setVisible(true);
                            searchFile(pathDirectory.getText(), root);
                            progressIndicator.setVisible(false);
                            return null;
                        }
                    };

                    new Thread(task).start();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText(null);
                    alert.setContentText("Введите правильный путь к файлу");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText(null);
                alert.setContentText("Заполните все поля");
                alert.showAndWait();
            }
        });
    }

    //configure tab select dir
    private void configuringDirectoryChooser(DirectoryChooser directoryChooser) {

        directoryChooser.setTitle("Select Some Directories");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }

    //method search files with text
    private void searchFile(String szDir, TreeItem<File> root) {
        int i;
        File f = new File(szDir);
        String[] sDirList = f.list();

        if (sDirList == null) {
            return;
        }
        for (i = 0; i < sDirList.length; i++) {
            File file = new File(szDir + File.separator + sDirList[i]);
            if (file.isFile()) {
                if (getFileExtension(file).equals(choiceFormatFile.getText())) {
                    if (containText(file, textForSearch.getText())) {
                        createTree(file, root);
                    }
                }
            } else {
                searchFile(szDir + File.separator + sDirList[i], root);
            }
        }
    }

    //method return file extension
    private String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }

    //whether file contains searchable text
    private boolean containText(File f, String str) {
        Scanner sc;
        try {
            sc = new Scanner(f, "windows-1251");
            while (sc.hasNextLine()) {
                if (sc.nextLine().toUpperCase().contains(str.toUpperCase())) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Нет доступа к файлу: " + f.getPath());
        }
        return false;
    }

    //build tree method
    private TreeItem<File> createTree(File file, TreeItem<File> root) {
        TreeItem<File> childItem = new TreeItem<>(file);
        TreeItem<File> rootItem = root;
        if (file.getParent() != null) rootItem = createTree(new File(file.getParent()), root);
        assert rootItem != null;
        if (!contain(rootItem, childItem)) {
            rootItem.getChildren().add(childItem);
            return childItem;
        }
        for (TreeItem<File> item : rootItem.getChildren()) {
            if (item.getValue().getPath().equals(childItem.getValue().getPath())) {
                return item;
            }
        }
        return null;
    }

    //whether tree contains item
    private boolean contain(TreeItem<File> tree, TreeItem<File> item) {
        String path = item.getValue().getPath();
        for (TreeItem<File> treeItem : tree.getChildren()) {
            if (treeItem.getValue().getPath().equals(path)) return true;
        }
        return false;
    }
}




