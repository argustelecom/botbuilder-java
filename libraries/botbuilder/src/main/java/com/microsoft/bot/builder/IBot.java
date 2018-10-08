package Microsoft.Bot.Builder;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Represents a bot that can operate on incoming activities.
 
 A <see cref="BotAdapter"/> passes incoming activities from the user's
 channel to the bot's <see cref="OnTurnAsync(ITurnContext, CancellationToken)"/> method.
 {@link IMiddleware}
*/
public interface IBot
{
	/** 
	 When implemented in a bot, handles an incoming activity.
	 
	 @param turnContext The context object for this turn.
	 @param cancellationToken The cancellation token.
	 @return A task that represents the work queued to execute.
	 The <paramref name="turnContext"/> provides information about the
	 incoming activity, and other data needed to process the activity.
	 {@link ITurnContext}
	 {@link Bot.Schema.IActivity}
	*/

	Task OnTurnAsync(ITurnContext turnContext);
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: Task OnTurnAsync(ITurnContext turnContext, CancellationToken cancellationToken = default(CancellationToken));
	Task OnTurnAsync(ITurnContext turnContext, CancellationToken cancellationToken);
}