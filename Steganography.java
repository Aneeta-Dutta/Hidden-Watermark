import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;

class RangeException extends Exception {
	RangeException(String error) {
		System.out.println("Error: " + error);
	}
}

class Photograph {
	
	private BufferedImage bufferedImage;
	private File file;
	private String extension;
	
	Photograph(File file) throws IOException, FileNotFoundException {
		if (file.exists() && file.isFile()) {
			this.file = file;
			this.bufferedImage = ImageIO.read(this.file);
			this.extension = this.file.getName().substring(this.file.getName().lastIndexOf(".") + 1);
		} else throw new FileNotFoundException(file.getPath());
	}
	
	BufferedImage get() {
		return this.bufferedImage;
	}
	
	File getFile() {
		return this.file;
	}
	
	String getExtension() {
		return this.extension;
	}
}

class Steganography {
	
	private File folder = new File("Steganography Output");
	private final int signatureLimit = 25;
	private final int bitMask = 0x00000001;
	
	public File getOutputFile(String name) {
		return new File(this.folder.getName(), name);
	}
	
	public boolean exportPhotograph(Photograph photograph) {
		try {
			if (!(this.folder.exists() && this.folder.isDirectory())) this.folder.mkdir();
			File output = new File(this.folder.getName(), photograph.getFile().getName());
			ImageIO.write(photograph.get(), photograph.getExtension(), output);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Photograph signPhotograph(File file, String signature) throws IOException, RangeException {
		int signatureLength = signature.length();
		if (signatureLength > 0 && signatureLength <= this.signatureLimit) {
			Photograph photograph = new Photograph(file);
			int bit, x = 0, y = 0;
			
			for (int foo = 0; foo < (this.signatureLimit - signatureLength); foo++)
				signature += " ";
			
			for (int i = 0; i < signature.length(); i++) {
				bit = (int) signature.charAt(i);
				for (int j = 0; j < 8; j++) {
					int flag = bit & this.bitMask;
					if (flag == 1) {
						if (x < photograph.get().getWidth()) {
							photograph.get().setRGB(x, y, photograph.get().getRGB(x, y) | 0x00000001);
							x++;
						} else {
							x = 0;
							y++;
							photograph.get().setRGB(x, y, photograph.get().getRGB(x, y) | 0x00000001);
						}
					} else {
						if (x < photograph.get().getWidth()) {
							photograph.get().setRGB(x, y, photograph.get().getRGB(x, y) & 0xFFFFFFFE);
							x++;
						} else {
							x = 0;
							y++;
							photograph.get().setRGB(x, y, photograph.get().getRGB(x, y) & 0xFFFFFFFE);
						}
					}
					bit = bit >> 1;
				}
			}
			return photograph;
		} else throw new RangeException("Make sure your signature length is within 1-" + this.signatureLimit + " characters.");
	}
	
	public String readSign(File file) throws IOException {
		return readSign(new Photograph(file));
	}
	
	public String readSign(Photograph photograph) {
		StringBuilder stringBuilder = new StringBuilder();
		int flag, x = 0, y = 0;
		char[] c = new char[this.signatureLimit];
		
		for (int i = 0; i < this.signatureLimit; i++) {
			int bit = 0;
			for (int j = 0; j < 8; j++) {
				if (x < photograph.get().getWidth()) {
					flag = photograph.get().getRGB(x, y) & this.bitMask;
					x++;
				} else {
					x = 0;
					y++;
					flag = photograph.get().getRGB(x, y) & this.bitMask;
				}
				
				if(flag == 1) {
					bit = bit >> 1;
					bit = bit | 0x80;
				} else bit = bit >> 1;
			}
			c[i] = (char) bit;
			stringBuilder.append(c[i]);
		}
		
		return stringBuilder.toString();
	}
}