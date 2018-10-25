package Microsoft.Bot.Builder.Dialogs;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

/** 
 Result returned to the caller of one of the various stack manipulation methods and used to
 return the result from a final call to `DialogContext.end()` to the bots logic.
*/
public class DialogTurnResult
{

	public DialogTurnResult(DialogTurnStatus status)
	{
		this(status, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public DialogTurnResult(DialogTurnStatus status, object result = null)
	public DialogTurnResult(DialogTurnStatus status, Object result)
	{
		setStatus(status);
		setResult(result);
	}

	/** 
	 Gets or sets the current status of the stack.
	 
	 <value>
	 The current status of the stack.
	 </value>
	*/
	private DialogTurnStatus Status = DialogTurnStatus.values()[0];
	public final DialogTurnStatus getStatus()
	{
		return Status;
	}
	public final void setStatus(DialogTurnStatus value)
	{
		Status = value;
	}

	/** 
	 Gets or sets the result returned by a dialog that was just ended.
	 This will only be populated in certain cases:
	
	 - The bot calls `dc.begin()` to start a new dialog and the dialog ends immediately.
	 - The bot calls `dc.continue()` and a dialog that was active ends.
	
	 In all cases where it's populated, [active](#active) will be `false`.
	 
	 <value>
	 The result returned by a dialog that was just ended.
	 </value>
	*/
	private Object Result;
	public final Object getResult()
	{
		return Result;
	}
	public final void setResult(Object value)
	{
		Result = value;
	}
}