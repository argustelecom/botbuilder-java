// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.adapters;

import com.microsoft.bot.schema.models.Activity;


@FunctionalInterface
public interface ValidateReply
{
	/**
	 Represents a method the <see cref="TestFlow"/> can call to validate an activity.

	 @param expected The expected activity from the bot or adapter.
	 @param actual The actual activity from the bot or adapter.
	 */
	void invoke(Activity expected, Activity actual);
}