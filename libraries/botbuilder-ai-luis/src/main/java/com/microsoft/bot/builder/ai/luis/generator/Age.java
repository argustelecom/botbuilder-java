package Microsoft.Bot.Builder.AI.Luis;

import Newtonsoft.Json.*;
import java.util.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Strongly typed LUIS builtin_age.
*/
public class Age extends NumberWithUnits
{
	/** 
	 Initializes a new instance of the <see cref="Age"/> class.
	 
	 @param age Age.
	 @param units Units for age.
	*/
	public Age(double age, String units)
	{
		super(Optional.of(age), units);
	}

	/** <inheritdoc/>
	*/
	@Override
	public String toString()
	{
		return String.format("Age(%1$s %2$s)", getNumber(), getUnits());
	}
}