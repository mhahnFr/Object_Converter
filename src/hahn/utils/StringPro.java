package hahn.utils;

/**
 * Diese Klasse liefert ein paar zusätzliche Funktionen für {@link String} nach.
 * 
 * @author Manuel Hahn
 * @since 26.05.2017
 */
public class StringPro {
	private final String string;
	
	/**
	 * Erzeugt einen neuen StringPro.
	 * 
	 * @param string der String, aus welchem ein StringPro werden soll
	 */
	public StringPro(String string) {
		this.string = string;
	}

	/**
	 * Entfernt eine Zeichenkette ab dem angegebenen Index (inklusive) bis zum toIndex (exklusive).
	 * 
	 * @param fromIndex der Anfangsindex, ab welchem der Teil entfernt werden soll
	 * @param toIndex der Endindex, bis zu welchem der Teil entfernt wird
	 * @return einen String, bei dem der angegebene Teil entfernt wurde
	 */
	public String delete(int fromIndex, int toIndex) {
		String toReturn = "";
		toReturn += string.substring(0, fromIndex);
		toReturn += string.substring(toIndex);
		return toReturn;
	}
	
	/**
	 * Überprüft, ob sich das angegebene Wort in diesem String befindet.
	 * 
	 * @param word das Wort, nach dem gesucht werden soll
	 * @return ob das Wort gefunden wurde oder nicht
	 */
	public boolean containsWord(String word) {
		if(string.contains(word)) {
			int index = string.indexOf(word);
			char ca = string.charAt(index + word.length());
			if((index == 0 || string.charAt(index - 1) == ' ')
					&& (ca == ' ' || ca == ';' || ca == '.' || ca == '\n' || ca == ':' || ca == ',')) {
				return true;
			}
		}
		return false;
	}
	
	public String toString() {
		return string;
	}
}