package Microsoft.Bot.Builder;

/** 
 A method that can participate in update activity events for the current turn.
 
 @param turnContext The context object for the turn.
 @param activity The replacement activity.
 @param next The delegate to call to continue event processing.
 @return A task that represents the work queued to execute.
 A handler calls the <paramref name="next"/> delegate to pass control to
 the next registered handler. If a handler doesn’t call the next delegate,
 the adapter does not call any of the subsequent handlers and does not update the
 activity.
 <p>The activity's <see cref="IActivity.Id"/> indicates the activity in the
 conversation to replace.</p>
 <p>If the activity is successfully sent, the <paramref name="next"/> delegater returns
 a <see cref="ResourceResponse"/> object containing the ID that the receiving
 channel assigned to the activity. Use this response object as the return value of this handler.</p>
 
 {@link BotAdapter}
 {@link SendActivitiesHandler}
 {@link DeleteActivityHandler}
 {@link ITurnContext.OnUpdateActivity(UpdateActivityHandler)}
*/
@FunctionalInterface
public interface UpdateActivityHandler
{
	Task<ResourceResponse> invoke(ITurnContext turnContext, Activity activity, Func<Task<ResourceResponse>> next);
}