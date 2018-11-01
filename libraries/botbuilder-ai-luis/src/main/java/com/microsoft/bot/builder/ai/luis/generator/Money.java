package Microsoft.Bot.Builder.AI.Luis;


import java.util.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Strongly typed LUIS builtin_money.
*/
public class Money extends NumberWithUnits
{
	/** 
	 Initializes a new instance of the <see cref="Money"/> class.
	 
	 @param money Money amount.
	 @param units Currency units.
	*/
	public Money(double money, String units)
	{
		super(Optional.of(money), units);
	}

	/** <inheritdoc/>
	*/
	@Override
	public String toString()
	{
		return String.format("Currency(%1$s %2$s)", getNumber(), getUnits());
	}
}