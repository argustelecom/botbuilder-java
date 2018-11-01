package com.microsoft.bot.builder.ai.luis;


import java.util.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Strongly typed LUIS builtin_dimension.
*/
public class Dimension extends NumberWithUnits
{
	/** 
	 Initializes a new instance of the <see cref="Dimension"/> class.
	 
	 @param number Number.
	 @param units Units for number.
	*/
	public Dimension(double number, String units)
	{
		super(Optional.of(number), units);
	}

	/** <inheritdoc/>
	*/
	@Override
	public String toString()
	{
		return String.format("Dimension(%1$s %2$s)", getNumber(), getUnits());
	}
}