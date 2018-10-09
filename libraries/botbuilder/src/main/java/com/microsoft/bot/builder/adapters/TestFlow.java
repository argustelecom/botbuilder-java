package com.microsoft.bot.builder.adapters;

import Microsoft.Bot.Builder.*;
import java.time.*;

/** 
 A mock channel that can be used for unit testing of bot logic.
 
 You can use this class to mimic input from a a user or a channel to validate
 that the bot or adapter responds as expected.
 {@link TestAdapter}
*/
public class TestFlow
{
	private TestAdapter _adapter;
	private void _testTask;
	private BotCallbackHandler _callback;

	/** 
	 Initializes a new instance of the <see cref="TestFlow"/> class.
	 
	 @param adapter The test adapter to use.
	 @param callback The bot turn processing logic to test.
	*/

	public TestFlow(TestAdapter adapter)
	{
		this(adapter, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public TestFlow(TestAdapter adapter, BotCallbackHandler callback = null)
	public TestFlow(TestAdapter adapter, BotCallbackHandler callback)
	{
		_adapter = adapter;
		_callback = (TurnContext turnContext) -> callback.invoke(turnContext, cancellationToken);
		_testTask = (_testTask != null) ? _testTask : Task.CompletedTask;
	}

	/** 
	 Initializes a new instance of the <see cref="TestFlow"/> class from an existing flow.
	 
	 @param testTask The exchange to add to the exchanges in the existing flow.
	 @param flow The flow to build up from. This provides the test adapter to use,
	 the bot turn processing locig to test, and a set of exchanges to model and test.
	*/
	public TestFlow(Task testTask, TestFlow flow)
	{
		_testTask = (testTask != null) ? testTask : Task.CompletedTask;
		_callback = (TurnContext turnContext) -> flow._callback.invoke(turnContext, cancellationToken);
		_adapter = flow._adapter;
	}

	/** 
	 Initializes a new instance of the <see cref="TestFlow"/> class.
	 
	 @param adapter The test adapter to use.
	 @param bot The bot containing the turn processing logic to test.
	*/
	public TestFlow(TestAdapter adapter, IBot bot)
	{
		this(adapter, bot.OnTurnAsync);
	}

	/** 
	 Starts the execution of the test flow.
	 
	 @return Runs the exchange between the user and the bot.
	 This methods sends the activities from the user to the bot and
	 checks the responses from the bot based on the activiies described in the
	 current test flow.
	*/
	public final void StartTestAsync()
	{
		return _testTask;
	}

	/** 
	 Adds a message activity from the user to the bot.
	 
	 @param userSays The text of the message to send.
	 @return A new <see cref="TestFlow"/> object that appends a new message activity from the user to the modeled exchange.
	 This method does not modify the original <see cref="TestFlow"/> object.
	*/
	public final TestFlow Send(String userSays)
	{
		if (userSays == null)
		{
			throw new NullPointerException("You have to pass a userSays parameter");
		}

		return new TestFlow(_testTask.ContinueWith((task) ->
		{
					// NOTE: we need to .Wait() on the original void to properly observe any exceptions that might have occurred
					// and to have them propagate correctly up through the chain to whomever is waiting on the parent task
					// The following StackOverflow answer provides some more details on why you want to do this:
					// https://stackoverflow.com/questions/11904821/proper-way-to-use-continuewith-for-tasks/11906865#11906865
					//
					// From the Docs:
					//  https://docs.microsoft.com/en-us/dotnet/standard/parallel-programming/exception-handling-task-parallel-library
					//  Exceptions are propagated when you use one of the static or instance Task.Wait or Wait
					//  methods, and you handle them by enclosing the call in a try/catch statement. If a task is the
					//  parent of attached child tasks, or if you are waiting on multiple tasks, multiple exceptions
					//  could be thrown.
					task.Wait();

					return _adapter.SendTextToBotAsync(userSays, _callback, new CancellationToken());
		}).Unwrap(), this);
	}

	/** 
	 Adds an activity from the user to the bot.
	 
	 @param userActivity The activity to send.
	 @return A new <see cref="TestFlow"/> object that appends a new activity from the user to the modeled exchange.
	 This method does not modify the original <see cref="TestFlow"/> object.
	*/
	public final TestFlow Send(IActivity userActivity)
	{
		if (userActivity == null)
		{
			throw new NullPointerException("You have to pass an Activity");
		}

		return new TestFlow(_testTask.ContinueWith((task) ->
		{
					// NOTE: See details code in above method.
					task.Wait();

					return _adapter.ProcessActivityAsync((Activity)userActivity, _callback, new CancellationToken());
		}).Unwrap(), this);
	}

	/** 
	 Adds a delay in the conversation.
	 
	 @param ms The delay length in milliseconds.
	 @return A new <see cref="TestFlow"/> object that appends a delay to the modeled exchange.
	 This method does not modify the original <see cref="TestFlow"/> object.
	*/
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public TestFlow Delay(uint ms)
	public final TestFlow Delay(int ms)
	{
		return new TestFlow(_testTask.ContinueWith((task) ->
		{
					// NOTE: See details code in above method.
					task.Wait();

					return Task.Delay((int)ms);
		}), this);
	}

	/** 
	 Adds an assertion that the turn processing logic responds as expected.
	 
	 @param expected The expected text of a message from the bot.
	 @param description A message to send if the actual response is not as expected.
	 @param timeout The amount of time in milliseconds within which a response is expected.
	 @return A new <see cref="TestFlow"/> object that appends this assertion to the modeled exchange.
	 This method does not modify the original <see cref="TestFlow"/> object.
	 @exception Exception The bot did not respond as expected.
	*/

	public final TestFlow AssertReply(String expected, String description)
	{
		return AssertReply(expected, description, 3000);
	}

	public final TestFlow AssertReply(String expected)
	{
		return AssertReply(expected, null, 3000);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public TestFlow AssertReply(string expected, string description = null, uint timeout = 3000)
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
	public final TestFlow AssertReply(String expected, String description, int timeout)
	{
		return AssertReply(_adapter.MakeActivity(expected), description, timeout);
	}

	/** 
	 Adds an assertion that the turn processing logic responds as expected.
	 
	 @param expected The expected activity from the bot.
	 @param description A message to send if the actual response is not as expected.
	 @param timeout The amount of time in milliseconds within which a response is expected.
	 @return A new <see cref="TestFlow"/> object that appends this assertion to the modeled exchange.
	 This method does not modify the original <see cref="TestFlow"/> object.
	 @exception Exception The bot did not respond as expected.
	*/

	public final TestFlow AssertReply(IActivity expected, String description)
	{
		return AssertReply(expected, description, 3000);
	}

	public final TestFlow AssertReply(IActivity expected)
	{
		return AssertReply(expected, null, 3000);
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: public TestFlow AssertReply(IActivity expected, [CallerMemberName] string description = null, uint timeout = 3000)
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
	public final TestFlow AssertReply(IActivity expected, String description, int timeout)
	{
		return AssertReply((reply) ->
		{
					if (expected.Type != reply.Type)
					{
						throw new RuntimeException(String.format("%1$s: Type should match", description));
					}

					if (expected.AsMessageActivity().Text != reply.AsMessageActivity().Text)
					{
						if (description == null)
						{
							throw new RuntimeException(String.format("Expected:%1$s\nReceived:%2$s", expected.AsMessageActivity().Text, reply.AsMessageActivity().Text));
						}
						else
						{
							throw new RuntimeException(String.format("%1$s:\nExpected:%2$s\nReceived:%3$s", description, expected.AsMessageActivity().Text, reply.AsMessageActivity().Text));
						}
					}
		}, description, timeout);
	}

	/** 
	 Adds an assertion that the turn processing logic responds as expected.
	 
	 @param validateActivity A validation method to apply to an activity from the bot.
	 This activity should throw an exception if validation fails.
	 @param description A message to send if the actual response is not as expected.
	 @param timeout The amount of time in milliseconds within which a response is expected.
	 @return A new <see cref="TestFlow"/> object that appends this assertion to the modeled exchange.
	 This method does not modify the original <see cref="TestFlow"/> object.
	*/

	public final TestFlow AssertReply(Action<Activity> validateActivity, String description)
	{
		return AssertReply(validateActivity, description, 3000);
	}

	public final TestFlow AssertReply(Action<Activity> validateActivity)
	{
		return AssertReply(validateActivity, null, 3000);
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: public TestFlow AssertReply(Action<Activity> validateActivity, [CallerMemberName] string description = null, uint timeout = 3000)
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
	public final TestFlow AssertReply(tangible.Action1Param<Activity> validateActivity, String description, int timeout)
	{
		return new TestFlow(_testTask.ContinueWith((task) ->
		{
					// NOTE: See details code in above method.
					task.Wait();

					if (System.Diagnostics.Debugger.IsAttached)
					{
						timeout = Integer.MAX_VALUE;
					}

					LocalDateTime start = LocalDateTime.UtcNow;
					while (true)
					{
						LocalDateTime current = LocalDateTime.UtcNow;

						if ((current - start).TotalMilliseconds > timeout)
						{
							throw new TimeoutException(String.format("%1$sms Timed out waiting for:'%2$s'", timeout, description));
						}

						IActivity replyActivity = _adapter.GetNextReply();
						if (replyActivity != null)
						{
							// if we have a reply
							validateActivity.invoke(replyActivity);
							return;
						}
					}
		}), this);
	}

	/** 
	 Shortcut for calling <see cref="Send(string)"/> followed by <see cref="AssertReply(string, string, uint)"/>.
	 
	 @param userSays The text of the message to send.
	 @param expected The expected text of a message from the bot.
	 @param description A message to send if the actual response is not as expected.
	 @param timeout The amount of time in milliseconds within which a response is expected.
	 @return A new <see cref="TestFlow"/> object that appends this exchange to the modeled exchange.
	 This method does not modify the original <see cref="TestFlow"/> object.
	 @exception Exception The bot did not respond as expected.
	*/

	public final TestFlow Test(String userSays, String expected, String description)
	{
		return Test(userSays, expected, description, 3000);
	}

	public final TestFlow Test(String userSays, String expected)
	{
		return Test(userSays, expected, null, 3000);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public TestFlow Test(string userSays, string expected, string description = null, uint timeout = 3000)
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
	public final TestFlow Test(String userSays, String expected, String description, int timeout)
	{
		if (expected == null)
		{
			throw new NullPointerException("expected");
		}

		return Send(userSays).AssertReply(expected, description, timeout);
	}

	/** 
	 Shortcut for calling <see cref="Send(string)"/> followed by <see cref="AssertReply(IActivity, string, uint)"/>.
	 
	 @param userSays The text of the message to send.
	 @param expected The expected activity from the bot.
	 @param description A message to send if the actual response is not as expected.
	 @param timeout The amount of time in milliseconds within which a response is expected.
	 @return A new <see cref="TestFlow"/> object that appends this exchange to the modeled exchange.
	 This method does not modify the original <see cref="TestFlow"/> object.
	 @exception Exception The bot did not respond as expected.
	*/

	public final TestFlow Test(String userSays, Activity expected, String description)
	{
		return Test(userSays, expected, description, 3000);
	}

	public final TestFlow Test(String userSays, Activity expected)
	{
		return Test(userSays, expected, null, 3000);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public TestFlow Test(string userSays, Activity expected, string description = null, uint timeout = 3000)
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
	public final TestFlow Test(String userSays, Activity expected, String description, int timeout)
	{
		if (expected == null)
		{
			throw new NullPointerException("expected");
		}

		return Send(userSays).AssertReply(expected, description, timeout);
	}

	/** 
	 Shortcut for calling <see cref="Send(string)"/> followed by <see cref="AssertReply(Action{IActivity}, string, uint)"/>.
	 
	 @param userSays The text of the message to send.
	 @param validateActivity A validation method to apply to an activity from the bot.
	 This activity should throw an exception if validation fails.
	 @param description A message to send if the actual response is not as expected.
	 @param timeout The amount of time in milliseconds within which a response is expected.
	 @return A new <see cref="TestFlow"/> object that appends this exchange to the modeled exchange.
	 This method does not modify the original <see cref="TestFlow"/> object.
	 @exception Exception The bot did not respond as expected.
	*/

	public final TestFlow Test(String userSays, Action<Activity> validateActivity, String description)
	{
		return Test(userSays, validateActivity, description, 3000);
	}

	public final TestFlow Test(String userSays, Action<Activity> validateActivity)
	{
		return Test(userSays, validateActivity, null, 3000);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public TestFlow Test(string userSays, Action<Activity> validateActivity, string description = null, uint timeout = 3000)
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
	public final TestFlow Test(String userSays, tangible.Action1Param<Activity> validateActivity, String description, int timeout)
	{
		if (validateActivity == null)
		{
			throw new NullPointerException("validateActivity");
		}

		return Send(userSays).AssertReply(validateActivity, description, timeout);
	}

	/** 
	 Shorcut for adding an arbitray exchange between the user and bot.
	 Each activity with a <see cref="IActivity.From"/>.<see cref="ChannelAccount.Role"/> equals to "bot"
	 will be processed with the <see cref="AssertReply(IActivity, string, uint)"/> method.
	 Every other activity will be processed as user's message via the <see cref="Send(IActivity)"/> method.
	 
	 @param activities The list of activities to test.
	 @param description A message to send if the actual response is not as expected.
	 @param timeout The amount of time in milliseconds within which a response is expected.
	 @return A new <see cref="TestFlow"/> object that appends this exchange to the modeled exchange.
	 This method does not modify the original <see cref="TestFlow"/> object.
	 @exception Exception The bot did not respond as expected.
	*/

	public final TestFlow Test(java.lang.Iterable<Activity> activities, String description)
	{
		return Test(activities, description, 3000);
	}

	public final TestFlow Test(java.lang.Iterable<Activity> activities)
	{
		return Test(activities, null, 3000);
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: public TestFlow Test(IEnumerable<Activity> activities, [CallerMemberName] string description = null, uint timeout = 3000)
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
	public final TestFlow Test(java.lang.Iterable<Activity> activities, String description, int timeout)
	{
		if (activities == null)
		{
			throw new NullPointerException("activities");
		}

		// Chain all activities in a TestFlow, check if its a user message (send) or a bot reply (assert)
		return activities.Aggregate(this, (flow, activity) ->
		{
				return IsReply(activity) ? flow.AssertReply(activity, description, timeout) : flow.Send(activity);
		});
	}

	/** 
	 Shorcut for adding an arbitray exchange between the user and bot.
	 Each activity with a <see cref="IActivity.From"/>.<see cref="ChannelAccount.Role"/> equals to "bot"
	 will be processed with the <see cref="AssertReply(IActivity, string, uint)"/> method.
	 Every other activity will be processed as user's message via the <see cref="Send(IActivity)"/> method.
	 
	 @param activities The list of activities to test.
	 @param validateReply The delegate to call to validate responses from the bot.
	 @param description A message to send if the actual response is not as expected.
	 @param timeout The amount of time in milliseconds within which a response is expected.
	 @return A new <see cref="TestFlow"/> object that appends this exchange to the modeled exchange.
	 This method does not modify the original <see cref="TestFlow"/> object.
	 @exception Exception The bot did not respond as expected.
	*/

	public final TestFlow Test(java.lang.Iterable<Activity> activities, ValidateReply validateReply, String description)
	{
		return Test(activities, validateReply, description, 3000);
	}

	public final TestFlow Test(java.lang.Iterable<Activity> activities, ValidateReply validateReply)
	{
		return Test(activities, validateReply, null, 3000);
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: public TestFlow Test(IEnumerable<Activity> activities, ValidateReply validateReply, [CallerMemberName] string description = null, uint timeout = 3000)
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
	public final TestFlow Test(java.lang.Iterable<Activity> activities, ValidateReply validateReply, String description, int timeout)
	{
		if (activities == null)
		{
			throw new NullPointerException("activities");
		}

		// Chain all activities in a TestFlow, check if its a user message (send) or a bot reply (assert)
		return activities.Aggregate(this, (flow, activity) ->
		{
				if (IsReply(activity))
				{
					return flow.AssertReply((actual) -> validateReply(activity, actual), description, timeout);
				}
				else
				{
					return flow.Send(activity);
				}
		});
	}

	/** 
	 Adds an assertion that the bot's response is contained within a set of acceptable responses.
	 
	 @param candidates The set of acceptable messages.
	 @param description A message to send if the actual response is not as expected.
	 @param timeout The amount of time in milliseconds within which a response is expected.
	 @return A new <see cref="TestFlow"/> object that appends this assertion to the modeled exchange.
	 This method does not modify the original <see cref="TestFlow"/> object.
	 @exception Exception The bot did not respond as expected.
	*/

	public final TestFlow AssertReplyOneOf(String[] candidates, String description)
	{
		return AssertReplyOneOf(candidates, description, 3000);
	}

	public final TestFlow AssertReplyOneOf(String[] candidates)
	{
		return AssertReplyOneOf(candidates, null, 3000);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public TestFlow AssertReplyOneOf(string[] candidates, string description = null, uint timeout = 3000)
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
	public final TestFlow AssertReplyOneOf(String[] candidates, String description, int timeout)
	{
		if (candidates == null)
		{
			throw new NullPointerException("candidates");
		}

		return AssertReply((reply) ->
		{
					for (String candidate : candidates)
					{
						if (candidate.equals(reply.AsMessageActivity().Text))
						{
							return;
						}
					}

					throw new RuntimeException((description != null) ? description : String.format("Not one of candidates: %1$s", tangible.StringHelper.join("\n", candidates)));
		}, description, timeout);
	}

	private boolean IsReply(IActivity activity)
	{
		return String.equals("bot", activity.From == null ? null : activity.From.Role, StringComparison.InvariantCultureIgnoreCase);
	}
}