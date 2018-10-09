package com.microsoft.bot.builder;

import java.util.*;

// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.


/** 
  Manages a collection of botState and provides ability to load and save in parallel.
*/
public class BotStateSet
{
	/** 
	 Initializes a new instance of the <see cref="BotStateSet"/> class.
	 
	 @param botStates initial list of <see cref="BotState"/> objects to manage.
	*/
	public BotStateSet(BotState... botStates)
	{
		this.getBotStates().addAll(Arrays.asList(botStates));
	}

	/** 
	 Gets or sets the BotStates list for the BotStateSet.
	 
	 <value>The BotState objects managed by this class.</value>
	*/
	private ArrayList<BotState> BotStates = new ArrayList<BotState> ();
	public final ArrayList<BotState> getBotStates()
	{
		return BotStates;
	}
	public final void setBotStates(ArrayList<BotState> value)
	{
		BotStates = value;
	}

	/** 
	 Add a BotState to the set.
	 
	 @param botState BotState object
	 @return BotStateSet so you can fluently call Add() multiple times.
	*/
	public final BotStateSet Add(BotState botState)
	{
		if (botState == null)
		{
			throw new NullPointerException("botState");
		}

		this.getBotStates().add(botState);
		return this;
	}

	/** 
	 Load all BotState records in parallel.
	 
	 @param turnContext turn context.
	 @param force should data be forced into cache.

	 @return A task that represents the work queued to execute.
	*/

	public final void LoadAllAsync(TurnContext turnContext, boolean force)
	{
		return LoadAllAsync(turnContext, force, null);
	}

	public final void LoadAllAsync(TurnContext turnContext)
	{
		return LoadAllAsync(turnContext, false, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async void LoadAllAsync(TurnContext turnContext, bool force = false, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	public final void LoadAllAsync(TurnContext turnContext, boolean force)
	{
		ArrayList<Object> tasks = this.getBotStates().Select(bs -> bs.LoadAsync(turnContext, force, cancellationToken)).ToList();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await Task.WhenAll(tasks);
	}

	/** 
	 Save All BotState changes in parallel.
	 
	 @param turnContext turn context.
	 @param force should data be forced to save even if no change were detected.

	 @return A task that represents the work queued to execute.
	*/

	public final void SaveAllChangesAsync(TurnContext turnContext, boolean force)
	{
		return SaveAllChangesAsync(turnContext, force, null);
	}

	public final void SaveAllChangesAsync(TurnContext turnContext)
	{
		return SaveAllChangesAsync(turnContext, false, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async void SaveAllChangesAsync(TurnContext turnContext, bool force = false, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	public final void SaveAllChangesAsync(TurnContext turnContext, boolean force)
	{
		ArrayList<Object> tasks = this.getBotStates().Select(bs -> bs.SaveChangesAsync(turnContext, force, cancellationToken)).ToList();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await Task.WhenAll(tasks);
	}
}