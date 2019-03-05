import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class FileConverter {
	public static void main(String[] args) {
		if(args.length <= 1) {
			System.out.println("Instructions: ");
			System.out.println("You need at least 2 arguments!");
			System.out.println("Format: 0/1 filepath1 filepath2 filepath3 ...");
			System.out.println("Input 1 if you'd like to delete all of the original PNG files! Input 0 otherwise.");
			System.out.println("If you'd like to do all files in a folder, use the format: folderpath/*");
			System.out.println("If you'd like images in folders to be done recursively, let -r be the next argument!");
			return;
		}
		for(int i = 0; i < args.length; i++) {
			System.out.print(args[i] + " ");
		}
		boolean deleteOriginals = args[0].contains("1");
		File imageFile;
		for(int i = 1; i < args.length; i++) {
			//Folder & recursive option.
			if(args[i].endsWith("*")) {
				boolean recursive = i != args.length - 1 && args[i + 1].trim().equals("-r");
				if(recursive) {
					i++;
				}
				doFolder(new File(args[i].substring(0, args[i].length() - 1)), recursive, deleteOriginals);
			} else {
				imageFile = new File(args[i].trim());
				if(!imageFile.exists()) {
					System.err.println("[Error] Nonexistent PNG image given!");
				} else if(!imageFile.getPath().endsWith(".png")) {
					System.err.println("[Error] Please only enter PNG files or directories!");
				} else {
					createJPG(imageFile, deleteOriginals);
				}
			}
		}
	}
	
	/**
	 * @param folder The folder to be scanned for PNG files.
	 * @param recursive Whether we should scan recursively (and do all files in all subdirectories).
	 * @param deleteOriginals Whether we should delete the original files.
	 * @throws IOException
	 */
	private static void doFolder(File folder, boolean recursive, boolean deleteOriginals) {
		if(!folder.exists()) {
			System.err.println("[Error] Folder: " + folder.getPath() + " does not exist!");
			return;
		}
		//Find all corresponding directory files.
		File[] directoryFiles = folder.listFiles(f -> (recursive && f.isDirectory()) || f.getName().endsWith(".png"));
		for(int j = 0; j < directoryFiles.length; j++) {
			if(recursive && directoryFiles[j].isDirectory()) {
				//Recurse on the directory in order to complete the subdirectory.
				doFolder(directoryFiles[j], recursive, deleteOriginals);
			} else {
				//Create the JPG of the image.
				createJPG(directoryFiles[j], deleteOriginals);
			}
		}
	}
	
	private static void createJPG(File imageFile, boolean deleteOriginals) {
		if(!imageFile.exists()) {
			System.err.println("[Error] File: " + imageFile.getName() + " does not exist!");
			return;
		}
		try {
			BufferedImage bufferedImage = ImageIO.read(imageFile); //Read the image file.

			BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(), 
				bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB); //Create new BufferedImage and draw accordingly
			
			newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);

			String oldPath = imageFile.getPath().trim();
			String newPath = oldPath.substring(0, oldPath.indexOf(".")) + ".jpg";
			ImageIO.write(newBufferedImage, "jpg", new File(newPath));
			System.out.println("[Output] JPG Successfully Created: " + newPath); //Make it into a JPG
		
			if(deleteOriginals) { //delete image if desired.
				imageFile.delete();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}