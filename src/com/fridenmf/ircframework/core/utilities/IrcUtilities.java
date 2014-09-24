package com.fridenmf.ircframework.core.utilities;

public class IrcUtilities {
	
	public static final String color_white        = makeColor(0);
	public static final String color_black        = makeColor(1);
	public static final String color_blue         = makeColor(2);
	public static final String color_green        = makeColor(3);
	public static final String color_red_light    = makeColor(4);
	public static final String color_red          = makeColor(5);
	public static final String color_purple       = makeColor(6);
	public static final String color_yellow       = makeColor(7);
	public static final String color_yellow_light = makeColor(8);
	public static final String color_green_light  = makeColor(9);
	public static final String color_cyan         = makeColor(10);
	public static final String color_cyan_light   = makeColor(11);
	public static final String color_blue_light   = makeColor(12);
	public static final String color_purple_light = makeColor(13);
	public static final String color_gray         =	makeColor(14);
	public static final String color_gray_light   = makeColor(15);	
	
	public static String makeColor(int index){
		return ((char)3) + "" + index  + "" + ((char)2) + "" + ((char)2);
	}
	
	/** Starts with any of ~@%+ and is followed by whatever */
	public static boolean nickHasPrefix(String nick) {
		return nick.matches("(^[~@%+]).*");
	}
	
	/**
	 * Simply converts a string array to a string, with an offset and length, and a string to use between all elements in the array
	 */
	public static String stringArrayToString(String[] array, int off, int len, String inbetween){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
			if(i != array.length - 1){
				sb.append(inbetween);
			}
		}
		return sb.toString();
	}
	
	public static String stringArrayToString(String[] array, String inbetween) {
		return stringArrayToString(array, 0, array.length, inbetween);
	}
	
	/**
	 * @return null if (nicks == null || prefixes == null || nicks.length != prefixes.length), otherwise an array of all nicks, precatinated by the prefixes
	 */
	public static String[] getNicksWithPrefixes(String[] nicks, String[] prefixes){
		if(nicks == null || prefixes == null || nicks.length != prefixes.length){
			return null;
		}
		String[] result = new String[nicks.length];
		for (int i = 0; i < nicks.length; i++) {
			result[i] = prefixes[i] + nicks[i];
		}
		return result;
	}
	
	public static String deColor(String colorString){
		StringBuilder sb = new StringBuilder();
		char c;
		for (int i = 0, length = colorString.length(); i < length; i++) {
			c = colorString.charAt(i);
			if((int)c != 3){
				sb.append(c);
			}else{
				/* Skip "End of text" ASCII sign */
				i++;
				
				/* Continue while there is a number */
				while(i < (length - 1) && Character.isDigit(c = colorString.charAt(i)) ){
					i++;
				}
				
				/* No number, jump back a step cause for-loop will increment it by one */
				i--;
			}
		}
		return sb.toString();
	}
	
	public static String deInvert(String invertString){
		StringBuilder sb = new StringBuilder();
		char c;
		for (int i = 0, length = invertString.length(); i < length; i++) {
			c = invertString.charAt(i);
			if((int)c != 22){
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	public static String deBold(String boldString){
		StringBuilder sb = new StringBuilder();
		char c;
		for (int i = 0, length = boldString.length(); i < length; i++) {
			c = boldString.charAt(i);
			if((int)c != 2){
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	public static String deUnderline(String underlineString){
		StringBuilder sb = new StringBuilder();
		char c;
		for (int i = 0, length = underlineString.length(); i < length; i++) {
			c = underlineString.charAt(i);
			if((int)c != 31){
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	public static String cleanText(String text, boolean cleanBold, boolean cleanColor, boolean cleanUnderline, boolean cleanInvert){
		
		if(text == null){
			return null;
		}
		
		String cleanText = text; 
		
		cleanText = cleanUnderline ? IrcUtilities.deUnderline(cleanText) : cleanText;
		cleanText = cleanInvert ? IrcUtilities.deInvert(cleanText) : cleanText;
		cleanText = cleanColor ? IrcUtilities.deColor(cleanText) : cleanText;
		cleanText = cleanBold ? IrcUtilities.deBold(cleanText) : cleanText;
		
		return cleanText;
		
	}

}
