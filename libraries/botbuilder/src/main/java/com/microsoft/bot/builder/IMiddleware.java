package Microsoft.Bot.Builder;

/** 
 Represents middleware that can operate on incoming activities.
 
 A <see cref="BotAdapter"/> passes incoming activities from the user's
 channel to the middleware's <see cref="OnTurnAsync(ITurnContext, NextDelegate, CancellationToken)"/>
 method.
 <p>You can add middleware objects to your adapter’s middleware collection. The
 adapter processes and directs incoming activities in through the bot middleware
 pipeline to your bot’s logic and then back out again. As each activity flows in
 and out of the bot, each piece of middleware can inspect or act upon the activity,
 both before and after the bot logic runs.</p>
 <p>For each activity, the adapter calls middleware in the order in which you
 added it.</p>
 
 {@link IBot}
*/
public interface IMiddleware
{
	/** 
	 When implemented in middleware, processess an incoming activity.
	 
	 @param turnContext The context object for this turn.
	 @param next The delegate to call to continue the bot middleware pipeline.
	 @param cancellationToken A cancellation token that can be used by other objects
	 or threads to receive notice of cancellation.
	 @return A task that represents the work queued to execute.
	 Middleware calls the <paramref name="next"/> delegate to pass control to
	 the next middleware in the pipeline. If middleware doesn’t call the next delegate,
	 the adapter does not call any of the subsequent middleware’s request handlers or the
	 bot’s receive handler, and the pipeline short circuits.
	 <p>The <paramref name="turnContext"/> provides information about the
	 incoming activity, and other data needed to process the activity.</p>
	 
	 {@link ITurnContext}
	 {@link Bot.Schema.IActivity}
	*/

	Task OnTurnAsync(ITurnContext turnContext, NextDelegate next);
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: Task OnTurnAsync(ITurnContext turnContext, NextDelegate next, CancellationToken cancellationToken = default(CancellationToken));
	Task OnTurnAsync(ITurnContext turnContext, NextDelegate next, CancellationToken cancellationToken);
}