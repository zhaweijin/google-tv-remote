package com.reveive.protocol;

import java.util.HashMap;

import android.view.KeyEvent;

public class KeycodeMap {



	
	private HashMap<String, Integer> keycodeHashMap = new HashMap<String, Integer>();
	private static final int SHIFT_FWCH = 8;
	
	public void initKeycodeMap(){
		//number
		keycodeHashMap.put("0", KeyEvent.KEYCODE_0);
		keycodeHashMap.put("1", KeyEvent.KEYCODE_1);
		keycodeHashMap.put("2", KeyEvent.KEYCODE_2);
		keycodeHashMap.put("3", KeyEvent.KEYCODE_3);
		keycodeHashMap.put("4", KeyEvent.KEYCODE_4);
		keycodeHashMap.put("5", KeyEvent.KEYCODE_5);
		keycodeHashMap.put("6", KeyEvent.KEYCODE_6);
		keycodeHashMap.put("7", KeyEvent.KEYCODE_7);
		keycodeHashMap.put("8", KeyEvent.KEYCODE_8);
		keycodeHashMap.put("9", KeyEvent.KEYCODE_9);
		
		//a-z
		keycodeHashMap.put("A", KeyEvent.KEYCODE_A);
		keycodeHashMap.put("B", KeyEvent.KEYCODE_B);
		keycodeHashMap.put("C", KeyEvent.KEYCODE_C);
		keycodeHashMap.put("D", KeyEvent.KEYCODE_D);
		keycodeHashMap.put("E", KeyEvent.KEYCODE_E);
		keycodeHashMap.put("F", KeyEvent.KEYCODE_F);
		keycodeHashMap.put("G", KeyEvent.KEYCODE_G);
		keycodeHashMap.put("H", KeyEvent.KEYCODE_H);
		keycodeHashMap.put("I", KeyEvent.KEYCODE_I);
		keycodeHashMap.put("J", KeyEvent.KEYCODE_J);
		keycodeHashMap.put("K", KeyEvent.KEYCODE_K);
		keycodeHashMap.put("L", KeyEvent.KEYCODE_L);
		keycodeHashMap.put("M", KeyEvent.KEYCODE_M);
		keycodeHashMap.put("N", KeyEvent.KEYCODE_N);
		keycodeHashMap.put("O", KeyEvent.KEYCODE_O);
		keycodeHashMap.put("P", KeyEvent.KEYCODE_P);
		keycodeHashMap.put("Q", KeyEvent.KEYCODE_Q);
		keycodeHashMap.put("R", KeyEvent.KEYCODE_R);
		keycodeHashMap.put("S", KeyEvent.KEYCODE_S);
		keycodeHashMap.put("T", KeyEvent.KEYCODE_T);
		keycodeHashMap.put("U", KeyEvent.KEYCODE_U);
		keycodeHashMap.put("V", KeyEvent.KEYCODE_V);
		keycodeHashMap.put("W", KeyEvent.KEYCODE_W);
		keycodeHashMap.put("X", KeyEvent.KEYCODE_X);
		keycodeHashMap.put("Y", KeyEvent.KEYCODE_Y);
		keycodeHashMap.put("Z", KeyEvent.KEYCODE_Z);
		

		
		
		keycodeHashMap.put("@", KeyEvent.KEYCODE_AT);
		keycodeHashMap.put("#", KeyEvent.KEYCODE_POUND);
		keycodeHashMap.put("&", KeyEvent.KEYCODE_7 | ('&' << 8));
		keycodeHashMap.put("%", KeyEvent.KEYCODE_5 | ('%' << 8));
		keycodeHashMap.put("\"", KeyEvent.KEYCODE_BACKSLASH);
		keycodeHashMap.put("*", KeyEvent.KEYCODE_STAR);
		keycodeHashMap.put("(", 162);
		keycodeHashMap.put(")", 163);
		keycodeHashMap.put("!", KeyEvent.KEYCODE_1 | ('!' << 8));
		keycodeHashMap.put(":", KeyEvent.KEYCODE_SEMICOLON  | (':' << 8));
		keycodeHashMap.put(";", KeyEvent.KEYCODE_SEMICOLON);
		keycodeHashMap.put(",", KeyEvent.KEYCODE_COMMA);
		keycodeHashMap.put(".", KeyEvent.KEYCODE_PERIOD);
		
		
		keycodeHashMap.put("~", KeyEvent.KEYCODE_GRAVE  | ('~' << 8));
		keycodeHashMap.put("+", KeyEvent.KEYCODE_PLUS);
		keycodeHashMap.put("-", KeyEvent.KEYCODE_MINUS);
//		keycodeHashMap.put("×", );
//		keycodeHashMap.put("÷", );
//		keycodeHashMap.put("°", );
		keycodeHashMap.put("<", KeyEvent.KEYCODE_M | ('\u300b' << SHIFT_FWCH));
		keycodeHashMap.put(">", KeyEvent.KEYCODE_N | ('\u300a' << SHIFT_FWCH));
		keycodeHashMap.put("{", KeyEvent.KEYCODE_LEFT_BRACKET  | ('{' << SHIFT_FWCH));
		keycodeHashMap.put("}", KeyEvent.KEYCODE_RIGHT_BRACKET | ('}' << SHIFT_FWCH));
		
//		keycodeHashMap.put("©", );
//		keycodeHashMap.put("£", );
		keycodeHashMap.put("^", KeyEvent.KEYCODE_6 | ('^' << SHIFT_FWCH));
//		keycodeHashMap.put("®", );
		keycodeHashMap.put("_", KeyEvent.KEYCODE_MINUS  | ('_' << SHIFT_FWCH));
		keycodeHashMap.put("=", KeyEvent.KEYCODE_EQUALS);
		keycodeHashMap.put("|", KeyEvent.KEYCODE_BACKSLASH  | ('|' << SHIFT_FWCH));
		keycodeHashMap.put(" ", KeyEvent.KEYCODE_SPACE);

		
		
		

		keycodeHashMap.put("[", KeyEvent.KEYCODE_LEFT_BRACKET);
		keycodeHashMap.put("]", KeyEvent.KEYCODE_RIGHT_BRACKET);
		keycodeHashMap.put("'", KeyEvent.KEYCODE_APOSTROPHE);
		keycodeHashMap.put("/", KeyEvent.KEYCODE_SLASH);
		
	}
	
	
	public HashMap<String, Integer> getKeycodeMap()
	{
		return keycodeHashMap;
	}

}
