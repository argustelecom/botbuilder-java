package Microsoft.Bot.Builder.Adapters;

import Microsoft.Bot.Builder.*;
import java.time.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.



/** 
 Represents a method the <see cref="TestFlow"/> can call to validate an activity.
 
 @param expected The expected activity from the bot or adapter.
 @param actual The actual activity from the bot or adapter.
*/
@FunctionalInterface
public interface ValidateReply
{
	void invoke(IActivity expected, IActivity actual);
}