package Microsoft.Bot.Builder;

// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.


/** 
  Middleware to automatically call .SaveChanges() at the end of the turn for all BotState class it is managing.
*/
public class AutoSaveStateMiddleware implements IMiddleware
{
	/** 
	 Initializes a new instance of the <see cref="AutoSaveStateMiddleware"/> class.
	 
	 @param botStates initial list of <see cref="BotState"/> objects to manage.
	*/
	public AutoSaveStateMiddleware(BotState... botStates)
	{
		setBotStateSet(new BotStateSet(botStates));
	}

	public AutoSaveStateMiddleware(BotStateSet botStateSet)
	{
		setBotStateSet(botStateSet);
	}

	/** 
	 Gets or sets the list of state management objects managed by this object.
	 
	 <value>The state management objects managed by this object.</value>
	*/
	private BotStateSet BotStateSet;
	public final BotStateSet getBotStateSet()
	{
		return BotStateSet;
	}
	public final void setBotStateSet(BotStateSet value)
	{
		BotStateSet = value;
	}

	/** 
	 Add a BotState to the list of sources to load.
	 
	 @param botState botState to manage.
	 @return botstateset for chaining more .Use().
	*/
	public final AutoSaveStateMiddleware Add(BotState botState)
	{
		if (botState == null)
		{
			throw new NullPointerException("botState");
		}

		this.getBotStateSet().Add(botState);
		return this;
	}

	/** 
	 Middleware implementation which calls savesChanges automatically at the end of the turn.
	 
	 @param turnContext turn context.
	 @param next next middlware.
	 @param cancellationToken cancellationToken.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	*/

	public final Task OnTurnAsync(ITurnContext turnContext, NextDelegate next)
	{
		return OnTurnAsync(turnContext, next, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task OnTurnAsync(ITurnContext turnContext, NextDelegate next, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	public final Task OnTurnAsync(ITurnContext turnContext, NextDelegate next, CancellationToken cancellationToken)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await next.invoke(cancellationToken).ConfigureAwait(false);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await this.getBotStateSet().SaveAllChangesAsync(turnContext, false, cancellationToken).ConfigureAwait(false);
	}
}