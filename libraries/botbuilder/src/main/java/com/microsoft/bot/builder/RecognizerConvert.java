package com.microsoft.bot.builder;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

/** 
 Can convert from a generic recognizer result to a strongly typed one.
*/
public interface RecognizerConvert
{
	/** 
	 Convert recognizer result.
	 
	 @param result Result to convert.
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to the C# 'dynamic' keyword:
	void Convert(dynamic result);
}