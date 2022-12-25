import java.io.File;

class Driver {

	public static void main(String[] args) {
		
		File file = new File("Makima.png");
		String signature = "Aneeta";
		Steganography steganography = new Steganography();
		
		try {
			// Signing the image
			Photograph photograph = steganography.signPhotograph(file, signature);
			
			// Exporting the signed photograph
			if (steganography.exportPhotograph(photograph)) {
				System.out.println("Photograph “" + photograph.getFile().getName() + "” was exported.");
			} else {
				System.out.println("Failed to export “" + photograph.getFile().getName() + "”.");
			}
			
			// Reading the sign from photograph object
			System.out.println(photograph.getFile().getName() + ": " + steganography.readSign(photograph));
			
			// Reading the sign from file object
			File output = steganography.getOutputFile(file.getName());
			System.out.println(output.getName() + ": " + steganography.readSign(output));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}