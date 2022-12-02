import java.util.regex.Matcher;

public class testi {

	public static void main(String[] args) {
		String mot ="/Hello/You/There";
		mot=mot.replace("/", ".");
		mot=mot.substring(0);
		System.out.println(mot);
	}

}
