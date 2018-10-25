package Microsoft.Bot.Builder.Dialogs.Choices;

import Microsoft.Bot.Builder.Dialogs.*;
import java.util.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.



public delegate List<Token> TokenizerFunction(string text, string locale = null);

public class Tokenizer
{
	public static TokenizerFunction getDefaultTokenizer()
	{
		return DefaultTokenizerImpl;
	}

	/** 
	 Simple tokenizer that breaks on spaces and punctuation. The only normalization done is to lowercase.
	 This is an exact port of the JavaScript implementation of the algorithm except that here
	 the .NET library functions are used in place of the JavaScript string code point functions.
	 
	 @param text The text being tokenized.
	 @param locale The locale of the text.
	 @return A list of tokens.
	*/

	public static java.util.ArrayList<Token> DefaultTokenizerImpl(String text)
	{
		return DefaultTokenizerImpl(text, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static List<Token> DefaultTokenizerImpl(string text, string locale = null)
	public static ArrayList<Token> DefaultTokenizerImpl(String text, String locale)
	{
		ArrayList<Token> tokens = new ArrayList<Token>();
		Token token = null;

		// Parse text
		int length = text != null ? text.length() : 0;
		int i = 0;

		while (i < length)
		{
			// Get both the UNICODE value of the current character and the complete character itself
			// which can potentially be multiple segments.
			int codePoint = Character.IsSurrogatePair(text, i) ? text.codePointAt(i) : (int)text.charAt(i);

			String chr = Character.ConvertFromUtf32(codePoint);

			// Process current character
			if (IsBreakingChar(codePoint))
			{
				// Character is in Unicode Plane 0 and is in an excluded block
				AppendToken(tokens, token, i - 1);
				token = null;
			}
			else if (codePoint > 0xFFFF)
			{
				// Character is in a Supplementary Unicode Plane. This is where emoji live so
				// we're going to just break each character in this range out as its own token.
				AppendToken(tokens, token, i - 1);
				token = null;
				Token tempVar = new Token();
				tempVar.setStart(i);
				tempVar.setEnd(i + (chr.length() - 1));
				tempVar.setText(chr);
				tempVar.setNormalized(chr);
				tokens.add(tempVar);
			}
			else if (token == null)
			{
				// Start a new token
				token = new Token();
				token.setStart(i);
				token.setText(chr);
			}
			else
			{
				// Add on to current token
				token.setText(token.getText() + chr);
			}

			i += chr.length();
		}

		AppendToken(tokens, token, length - 1);
		return tokens;
	}

	private static void AppendToken(ArrayList<Token> tokens, Token token, int end)
	{
		if (token != null)
		{
			token.setEnd(end);
			token.setNormalized(token.getText().toLowerCase());
			tokens.add(token);
		}
	}

	private static boolean IsBreakingChar(int codePoint)
	{
		return IsBetween(codePoint, 0x0000, 0x002F) || IsBetween(codePoint, 0x003A, 0x0040) || IsBetween(codePoint, 0x005B, 0x0060) || IsBetween(codePoint, 0x007B, 0x00BF) || IsBetween(codePoint, 0x02B9, 0x036F) || IsBetween(codePoint, 0x2000, 0x2BFF) || IsBetween(codePoint, 0x2E00, 0x2E7F);
	}

	private static boolean IsBetween(int value, int from, int to)
	{
		return value >= from && value <= to;
	}
}