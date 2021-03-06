package application;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

import api.AISheet;
import api.OthelloAI;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;


public class Lancher extends Application {

	public static final String version = "1.0";
	public static final int panelSize = 604;

	protected static List<AIAttribute> AIList = new ArrayList<AIAttribute>();

	public static void main(String[] args) {
		try{
			AIList.add(AIAttribute.PLAYER);
			loadAIs();
		}catch (MalformedURLException e){
			e.printStackTrace();
			return;
		}
		System.gc();
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		TabPane root = (TabPane) FXMLLoader.load(getClass().getResource("Launcher.fxml"));
		Scene scene = new Scene(root, 600, 500);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setTitle("Launcher");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	private static void loadAIs() throws MalformedURLException {
		File dirAI = new File(System.getProperty("user.dir"), "bin" + File.separator + "ai");
		if(dirAI.exists() && dirAI.isDirectory()){
			for (File fileAI : dirAI.listFiles()){
				if(fileAI.isFile()){
					URL[] fileUrls = { fileAI.toURI().toURL() };
					ClassLoader fileLoader = URLClassLoader.newInstance(fileUrls);

					//class file
					if(fileAI.getName().endsWith(".class")){
						try{
							String className = fileAI.getName().replaceFirst(".class", "");
							className = className.replace("/", ".");
							Class<?> clazz = fileLoader.loadClass(className);
							AIAttribute aIAttribute = canLoadClass(clazz);
							if(aIAttribute != null){
								System.out.println("Detect ai class file: " + aIAttribute);
								if(!AIList.contains(aIAttribute)){
									AIList.add(aIAttribute);
									aIAttribute.saveFile = new File(System.getProperty("user.dir"), "bin" + File.separator + "ai" + File.separator + "metadata" + File.separator + aIAttribute.toString().replace(" ", "_"));
									System.out.println("Successfully load ai class: " + aIAttribute);
								}else{
									System.out.println("The same ai name is already loaded: " + aIAttribute);
								}
							}
						}catch (Exception e){
							System.out.println("Cannot load class: " + fileAI.getName());
							e.printStackTrace();
						}
					}

					//jar file
					else if(fileAI.getName().endsWith(".jar")){
						try (JarFile jarAI = new JarFile(fileAI)){
							System.out.println("Start to load file: " + fileAI.getName());
							jarAI.stream().forEach(entry -> {
								if(entry.getName().endsWith(".class")){
									try{
										String className = entry.getName().replaceFirst(".class", "");
										className = className.replace("/", ".");
										Class<?> clazz = fileLoader.loadClass(className);
										AIAttribute aIAttribute = canLoadClass(clazz);
										if(aIAttribute != null){
											System.out.println("Detect ai class: " + aIAttribute);
											if(!AIList.contains(aIAttribute)){
												AIList.add(aIAttribute);
												aIAttribute.saveFile = new File(System.getProperty("user.dir"), "bin" + File.separator + "ai" + File.separator + "metadata" + File.separator + fileAI.getName().replace(".jar", "") + File.separator + aIAttribute.toString().replace(" ", "_"));
												System.out.println("Successfully load ai class: " + aIAttribute);
											}else{
												System.out.println("The same ai name is already loaded: " + aIAttribute);
											}
										}
									}catch (Exception e){
										System.out.println("Cannot load ai class file: " + entry.getName());
										e.printStackTrace();
									}
								}
							});
						}catch (IOException e){
							System.out.println("Cannot open file: " + fileAI.getName());
							e.printStackTrace();
						}
					}
				}
			}
		}else{
			System.out.println("Cannot detect the directory \"bin/ai\".");
			if(!dirAI.exists()){
				dirAI.mkdir();
				System.out.println("Creeated the directory \"bin/ai\".");
			}
			if(new File(System.getProperty("user.dir"), "bin" + File.separator + "ai" + File.separator + "metadata").mkdir()){
				System.out.println("Creeated the directory \"bin/ai/metadata\".");
			}
			System.out.println("Put jar file that contains ai class in the directiory to load custom AIs.");
			System.out.println("Class file will also be loaded, but deprecated.");
		}
	}

	@SuppressWarnings("unchecked")
	private static AIAttribute canLoadClass(Class<?> clazz) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException {
		if(clazz != null){
			if(AISheet.class.isAssignableFrom(clazz) && clazz != AISheet.class){
				Annotation[] annotations = clazz.getAnnotations();
				if(annotations != null && annotations.length > 0){
					for (Annotation a : annotations){
						if(a.annotationType() == OthelloAI.class){
							String depend = ((OthelloAI) a).depend();
							if(depend.equals(version)){
								String name = (String) clazz.getMethod("getAIName").invoke(clazz.newInstance());
								String version = ((OthelloAI) a).version();
								String author = ((OthelloAI) a).author();
								if(!name.isEmpty() && !version.isEmpty() && !author.isEmpty()){
									return new AIAttribute(name, version, author, (Class<? extends AISheet>) clazz, null);
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	protected static AIAttribute findAttributeFromName(String name) {
		if(name == null || name.isEmpty()) return null;
		for (AIAttribute attribute : AIList){
			if(attribute.name.equals(name)) return attribute;
		}
		return null;
	}

	protected static File getSaveFileFromClass(Class<? extends AISheet> clazz) {
		if(clazz == null) return null;
		for (AIAttribute attribute : AIList){
			if(attribute.alClass == clazz) return attribute.saveFile;
		}
		return null;
	}

	protected static class AIAttribute {

		protected static final AIAttribute PLAYER = new AIAttribute("Player", "---", "You", null, null);

		private StringProperty name;
		private StringProperty version;
		private StringProperty author;
		protected Class<? extends AISheet> alClass;
		protected File saveFile;

		AIAttribute(String name, String version, String author, Class<? extends AISheet> alClass, File saveFile) {
			this.name = new SimpleStringProperty(name);
			this.version = new SimpleStringProperty(version);
			this.author = new SimpleStringProperty(author);
			this.alClass = alClass;
			this.saveFile = saveFile;
		}

		public StringProperty nameProperty() {
			return name;
		}

		public StringProperty versionProperty() {
			return version;
		}

		public StringProperty authorProperty() {
			return author;
		}

		@Override
		public String toString() {
			return name.get() + " " + version.get() + " by " + author.get();
		}

		@Override
		public boolean equals(Object o) {
			if(o instanceof AIAttribute){
				return ((AIAttribute) o).name.equals(name) && ((AIAttribute) o).version.equals(version) && ((AIAttribute) o).author.equals(author);
			}
			return false;
		}
	}

}
