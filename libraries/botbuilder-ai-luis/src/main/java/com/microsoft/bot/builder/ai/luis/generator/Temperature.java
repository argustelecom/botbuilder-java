package Microsoft.Bot.Builder.AI.Luis;


import java.util.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Strongly typed LUIS builtin_temperature.
*/
public class Temperature extends NumberWithUnits
{
	/** 
	 Initializes a new instance of the <see cref="Temperature"/> class.
	 
	 @param temperature Temperature.
	 @param units Units.
	*/
	public Temperature(double temperature, String units)
	{
		super(Optional.of(temperature), units);
	}

	/** <inheritdoc/>
	*/
	@Override
	public String toString()
	{
		return String.format("Temperature(%1$s %2$s)", getNumber(), getUnits());
	}
}