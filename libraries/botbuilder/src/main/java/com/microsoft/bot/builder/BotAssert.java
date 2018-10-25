package com.microsoft.bot.builder;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


import com.microsoft.bot.schema.models.Activity;
import com.microsoft.bot.schema.models.ConversationReference;

/**
 Provides methods for debugging Bot Builder code.
*/
public class BotAssert
{
	/** 
	 Checks that an activity object is not <c>null</c>.
	 
	 @param activity The activity object.
	 @exception ArgumentNullException
	 <paramref name="activity"/> is <c>null</c>.
	*/
	public static void ActivityNotNull(Activity activity)
	{
		if (activity == null)
		{
			throw new NullPointerException("activity");
		}
	}

	/** 
	 Checks that a context object is not <c>null</c>.
	 
	 @param turnContext The context object.
	 @exception ArgumentNullException
	 <paramref name="turnContext"/> is <c>null</c>.
	*/
	public static void ContextNotNull(TurnContext turnContext)
	{
		if (turnContext == null)
		{
			throw new NullPointerException("turnContext");
		}
	}

	/** 
	 Checks that a conversation reference object is not <c>null</c>.
	 
	 @param reference The conversation reference object.
	 @exception ArgumentNullException
	 <paramref name="reference"/> is <c>null</c>.
	*/
	public static void ConversationReferenceNotNull(ConversationReference reference)
	{
		if (reference == null)
		{
			throw new NullPointerException("reference");
		}
	}

	/** 
	 Checks that an activity collection is not <c>null</c>.
	 
	 @param activities The activities.
	 @exception ArgumentNullException
	 <paramref name="activities"/> is <c>null</c>.
	*/
	public static void ActivityListNotNull(java.lang.Iterable<Activity> activities)
	{
		if (activities == null)
		{
			throw new NullPointerException("activities");
		}
	}

	/** 
	 Checks that a middleware object is not <c>null</c>.
	 
	 @param middleware The middleware object.
	 @exception ArgumentNullException
	 <paramref name="middleware"/> is <c>null</c>.
	*/
	public static void MiddlewareNotNull(Middleware middleware)
	{
		if (middleware == null)
		{
			throw new NullPointerException("middleware");
		}
	}

	/** 
	 Checks that a middleware collection is not <c>null</c>.
	 
	 @param middleware The middleware.
	 @exception ArgumentNullException
	 <paramref name="middleware"/> is <c>null</c>.
	*/
	public static void MiddlewareNotNull(java.lang.Iterable<Middleware> middleware)
	{
		if (middleware == null)
		{
			throw new NullPointerException("middleware");
		}
	}
}