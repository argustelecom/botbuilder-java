// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.

package com.microsoft.bot.builder;

import java.util.*;
import java.util.concurrent.CompletableFuture;

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

	 @return A task that represents the work queued to execute.
	*/
	public final CompletableFuture LoadAllAsync(TurnContext turnContext)
	{
		return LoadAllAsync(turnContext, false);
	}

	/**
	 Load all BotState records in parallel.

	 @param turnContext turn context.
	 @param force should data be forced into cache.

	 @return A task that represents the work queued to execute.
	 */
	public final CompletableFuture LoadAllAsync(TurnContext turnContext, boolean force)
	{
		return CompletableFuture.runAsync(() -> {
			CompletableFuture[] tasks = this.getBotStates().stream().map(bs -> bs.LoadAsync(turnContext, force)).toArray(CompletableFuture[]::new);
			CompletableFuture.allOf(tasks).join();
		});
	}

	/** 
	 Save All BotState changes in parallel.
	 
	 @param turnContext turn context.

	 @return A task that represents the work queued to execute.
	*/
	public final CompletableFuture SaveAllChangesAsync(TurnContext turnContext)
	{
		return SaveAllChangesAsync(turnContext, false);
	}

	/**
	 Save All BotState changes in parallel.

	 @param turnContext turn context.
	 @param force should data be forced to save even if no change were detected.

	 @return A task that represents the work queued to execute.
	 */
	public final CompletableFuture SaveAllChangesAsync(TurnContext turnContext, boolean force)
	{
		return CompletableFuture.runAsync(() -> {
			CompletableFuture[] tasks = this.getBotStates().stream().map(bs -> bs.SaveChangesAsync(turnContext, force)).toArray(CompletableFuture[]::new);
			CompletableFuture.allOf(tasks).join();
		});
	}
}