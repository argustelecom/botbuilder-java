package Microsoft.Bot.Builder;

import java.util.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Contains utility methods for various message types a bot can return.
 
 <example>
 <code>
 // Create and send a message.
 var message = MessageFactory.Text("Hello World");
 await context.SendActivity(message);
 </code>
 </example>
 The following apply to message actions in general.
 <p>See the channel's documentation for limits imposed upon the contents of
 the text of the message to send.</p>
 <p>To control various characteristics of your bot's speech such as voice,
 rate, volume, pronunciation, and pitch, specify test to speak in
 Speech Synthesis Markup Language (SSML) format.</p>
 <p>
 Channels decide how each card action manifests in their user experience.
 In most cases, the cards are clickable. In others, they may be selected by speech
 input. In cases where the channel does not offer an interactive activation
 experience (e.g., when interacting over SMS), the channel may not support
 activation whatsoever. The decision about how to render actions is controlled by
 normative requirements elsewhere in this document (e.g. within the card format,
 or within the suggested actions definition).</p>
 
*/
public final class MessageFactory
{
	/** 
	 Returns a simple text message.
	 
	 <example>
	 <code>
	 // Create and send a message.
	 var message = MessageFactory.Text("Hello World");
	 await context.SendActivity(message);
	 </code>
	 </example>
	 @param text The text of the message to send.
	 @param ssml Optional, text to be spoken by your bot on a speech-enabled
	 channel.
	 @param inputHint Optional, indicates whether your bot is accepting,
	 expecting, or ignoring user input after the message is delivered to the client.
	 One of: "acceptingInput", "ignoringInput", or "expectingInput".
	 Default is "acceptingInput".
	 @return A message activity containing the text.
	*/

	public static Activity Text(String text, String ssml)
	{
		return Text(text, ssml, null);
	}

	public static Activity Text(String text)
	{
		return Text(text, null, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static Activity Text(string text, string ssml = null, string inputHint = null)
	public static Activity Text(String text, String ssml, String inputHint)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
		var ma = Activity.CreateMessageActivity();
		SetTextAndSpeak(ma, text, ssml, inputHint);
		return (Activity)ma;
	}

	/** 
	 Returns a message that includes a set of suggested actions and optional text.
	 
	 <example>
	 <code>
	 // Create the activity and add suggested actions.
	 var activity = MessageFactory.SuggestedActions(
		 new string[] { "red", "green", "blue" },
		 text: "Choose a color");
	
	 // Send the activity as a reply to the user.
	 await context.SendActivity(activity);
	 </code>
	 </example>
	 @param actions
	 The text of the actions to create.
	 
	 @param text The text of the message to send.
	 @param ssml Optional, text to be spoken by your bot on a speech-enabled
	 channel.
	 @param inputHint Optional, indicates whether your bot is accepting,
	 expecting, or ignoring user input after the message is delivered to the client.
	 One of: "acceptingInput", "ignoringInput", or "expectingInput".
	 Default is "acceptingInput".
	 @return A message activity containing the suggested actions.
	 @exception ArgumentNullException
	 <paramref name="actions"/> is <c>null</c>.
	 This method creates a suggested action for each string in <paramref name="actions"/>.
	 The created action uses the text for the <see cref="CardAction.Value"/> and
	 <see cref="CardAction.Title"/> and sets the <see cref="CardAction.Type"/> to
	 <see cref="Microsoft.Bot.Schema.ActionTypes.ImBack"/>.
	 
	 {@link SuggestedActions(IEnumerable{CardAction}, string, string, string)}
	*/

	public static IMessageActivity SuggestedActions(java.lang.Iterable<String> actions, String text, String ssml)
	{
		return SuggestedActions(actions, text, ssml, null);
	}

	public static IMessageActivity SuggestedActions(java.lang.Iterable<String> actions, String text)
	{
		return SuggestedActions(actions, text, null, null);
	}

	public static IMessageActivity SuggestedActions(java.lang.Iterable<String> actions)
	{
		return SuggestedActions(actions, null, null, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static IMessageActivity SuggestedActions(IEnumerable<string> actions, string text = null, string ssml = null, string inputHint = null)
	public static IMessageActivity SuggestedActions(java.lang.Iterable<String> actions, String text, String ssml, String inputHint)
	{
		if (actions == null)
		{
			throw new NullPointerException("actions");
		}

		ArrayList<CardAction> cardActions = new ArrayList<CardAction>();
		for (String s : actions)
		{
			CardAction ca = new CardAction();
			ca.Type = ActionTypes.ImBack;
			ca.Value = s;
			ca.Title = s;

			cardActions.add(ca);
		}

		return SuggestedActions(cardActions, text, ssml, inputHint);
	}

	/** 
	 Returns a message that includes a set of suggested actions and optional text.
	 
	 <example>
	 <code>
	 // Create the activity and add suggested actions.
	 var activity = MessageFactory.SuggestedActions(
		 new CardAction[]
		 {
			 new CardAction(title: "red", type: ActionTypes.ImBack, value: "red"),
			 new CardAction( title: "green", type: ActionTypes.ImBack, value: "green"),
			 new CardAction(title: "blue", type: ActionTypes.ImBack, value: "blue")
		 }, text: "Choose a color");
	
	 // Send the activity as a reply to the user.
	 await context.SendActivity(activity);
	 </code>
	 </example>
	 @param cardActions
	 The card actions to include.
	 
	 @param text Optional, the text of the message to send.
	 @param ssml Optional, text to be spoken by your bot on a speech-enabled
	 channel.
	 @param inputHint Optional, indicates whether your bot is accepting,
	 expecting, or ignoring user input after the message is delivered to the client.
	 One of: "acceptingInput", "ignoringInput", or "expectingInput".
	 Default is "acceptingInput".
	 @return A message activity that contains the suggested actions.
	 @exception ArgumentNullException
	 <paramref name="cardActions"/> is <c>null</c>.
	 {@link SuggestedActions(IEnumerable{string}, string, string, string)}
	*/

	public static IMessageActivity SuggestedActions(java.lang.Iterable<CardAction> cardActions, String text, String ssml)
	{
		return SuggestedActions(cardActions, text, ssml, null);
	}

	public static IMessageActivity SuggestedActions(java.lang.Iterable<CardAction> cardActions, String text)
	{
		return SuggestedActions(cardActions, text, null, null);
	}

	public static IMessageActivity SuggestedActions(java.lang.Iterable<CardAction> cardActions)
	{
		return SuggestedActions(cardActions, null, null, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static IMessageActivity SuggestedActions(IEnumerable<CardAction> cardActions, string text = null, string ssml = null, string inputHint = null)
	public static IMessageActivity SuggestedActions(java.lang.Iterable<CardAction> cardActions, String text, String ssml, String inputHint)
	{
		if (cardActions == null)
		{
			throw new NullPointerException("cardActions");
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
		var ma = Activity.CreateMessageActivity();
		SetTextAndSpeak(ma, text, ssml, inputHint);

		ma.SuggestedActions = new SuggestedActions();
		ma.SuggestedActions.Actions = cardActions.ToList();

		return ma;
	}

	/** 
	 Returns a message activity that contains an attachment.
	 
	 @param attachment Attachment to include in the message.
	 @param text Optional, the text of the message to send.
	 @param ssml Optional, text to be spoken by your bot on a speech-enabled
	 channel.
	 @param inputHint Optional, indicates whether your bot is accepting,
	 expecting, or ignoring user input after the message is delivered to the client.
	 One of: "acceptingInput", "ignoringInput", or "expectingInput".
	 Default is "acceptingInput".
	 @return A message activity containing the attachment.
	 @exception ArgumentNullException
	 <paramref name="attachment"/> is <c>null</c>.
	 {@link Attachment(IEnumerable{Attachment}, string, string, string)}
	 {@link Carousel(IEnumerable{Attachment}, string, string, string)}
	*/

	public static IMessageActivity Attachment(Attachment attachment, String text, String ssml)
	{
		return Attachment(attachment, text, ssml, null);
	}

	public static IMessageActivity Attachment(Attachment attachment, String text)
	{
		return Attachment(attachment, text, null, null);
	}

	public static IMessageActivity Attachment(Attachment attachment)
	{
		return Attachment(attachment, null, null, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static IMessageActivity Attachment(Attachment attachment, string text = null, string ssml = null, string inputHint = null)
	public static IMessageActivity Attachment(Attachment attachment, String text, String ssml, String inputHint)
	{
		if (attachment == null)
		{
			throw new NullPointerException("attachment");
		}

		return Attachment(new ArrayList<Attachment>(Arrays.asList(attachment)), text, ssml, inputHint);
	}

	/** 
	 Returns a message activity that contains a collection of attachments, in a list.
	 
	 @param attachments The attachments to include in the message.
	 @param text Optional, the text of the message to send.
	 @param ssml Optional, text to be spoken by your bot on a speech-enabled
	 channel.
	 @param inputHint Optional, indicates whether your bot is accepting,
	 expecting, or ignoring user input after the message is delivered to the client.
	 One of: "acceptingInput", "ignoringInput", or "expectingInput".
	 Default is "acceptingInput".
	 @return A message activity containing the attachment.
	 @exception ArgumentNullException
	 <paramref name="attachments"/> is <c>null</c>.
	 {@link Carousel(IEnumerable{Attachment}, string, string, string)}
	 {@link Attachment(Schema.Attachment, string, string, string)}
	*/

	public static IMessageActivity Attachment(java.lang.Iterable<Attachment> attachments, String text, String ssml)
	{
		return Attachment(attachments, text, ssml, null);
	}

	public static IMessageActivity Attachment(java.lang.Iterable<Attachment> attachments, String text)
	{
		return Attachment(attachments, text, null, null);
	}

	public static IMessageActivity Attachment(java.lang.Iterable<Attachment> attachments)
	{
		return Attachment(attachments, null, null, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static IMessageActivity Attachment(IEnumerable<Attachment> attachments, string text = null, string ssml = null, string inputHint = null)
	public static IMessageActivity Attachment(java.lang.Iterable<Attachment> attachments, String text, String ssml, String inputHint)
	{
		if (attachments == null)
		{
			throw new NullPointerException("attachments");
		}

		return AttachmentActivity(AttachmentLayoutTypes.List, attachments, text, ssml, inputHint);
	}

	/** 
	 Returns a message activity that contains a collection of attachments, as a carousel.
	 
	 @param attachments The attachments to include in the message.
	 @param text Optional, the text of the message to send.
	 @param ssml Optional, text to be spoken by your bot on a speech-enabled
	 channel.
	 @param inputHint Optional, indicates whether your bot is accepting,
	 expecting, or ignoring user input after the message is delivered to the client.
	 One of: "acceptingInput", "ignoringInput", or "expectingInput".
	 Default is "acceptingInput".
	 @return A message activity containing the attachment.
	 @exception ArgumentNullException
	 <paramref name="attachments"/> is <c>null</c>.
	 <example>This code creates and sends a carousel of HeroCards.
	 <code>
	 // Create the activity and attach a set of Hero cards.
	 var activity = MessageFactory.Carousel(
	 new Attachment[]
	 {
		 new HeroCard(
			 title: "title1",
			 images: new CardImage[] { new CardImage(url: "imageUrl1.png") },
			 buttons: new CardAction[]
			 {
				 new CardAction(title: "button1", type: ActionTypes.ImBack, value: "item1")
			 })
		 .ToAttachment(),
		 new HeroCard(
			 title: "title2",
			 images: new CardImage[] { new CardImage(url: "imageUrl2.png") },
			 buttons: new CardAction[]
			 {
				 new CardAction(title: "button2", type: ActionTypes.ImBack, value: "item2")
			 })
		 .ToAttachment(),
		 new HeroCard(
			 title: "title3",
			 images: new CardImage[] { new CardImage(url: "imageUrl3.png") },
			 buttons: new CardAction[]
			 {
				 new CardAction(title: "button3", type: ActionTypes.ImBack, value: "item3")
			 })
		 .ToAttachment()
	 });
	
	 // Send the activity as a reply to the user.
	 await context.SendActivity(activity);
	 </code>
	 </example>
	 {@link Attachment(IEnumerable{Attachment}, string, string, string)}
	*/

	public static IMessageActivity Carousel(java.lang.Iterable<Attachment> attachments, String text, String ssml)
	{
		return Carousel(attachments, text, ssml, null);
	}

	public static IMessageActivity Carousel(java.lang.Iterable<Attachment> attachments, String text)
	{
		return Carousel(attachments, text, null, null);
	}

	public static IMessageActivity Carousel(java.lang.Iterable<Attachment> attachments)
	{
		return Carousel(attachments, null, null, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static IMessageActivity Carousel(IEnumerable<Attachment> attachments, string text = null, string ssml = null, string inputHint = null)
	public static IMessageActivity Carousel(java.lang.Iterable<Attachment> attachments, String text, String ssml, String inputHint)
	{
		if (attachments == null)
		{
			throw new NullPointerException("attachments");
		}

		return AttachmentActivity(AttachmentLayoutTypes.Carousel, attachments, text, ssml, inputHint);
	}

	/** 
	 Returns a message activity that contains a single image or video.
	 
	 @param url The URL of the image or video to send.
	 @param contentType The MIME type of the image or video.
	 @param name Optional, the name of the image or video file.
	 @param text Optional, the text of the message to send.
	 @param ssml Optional, text to be spoken by your bot on a speech-enabled
	 channel.
	 @param inputHint Optional, indicates whether your bot is accepting,
	 expecting, or ignoring user input after the message is delivered to the client.
	 One of: "acceptingInput", "ignoringInput", or "expectingInput".
	 Default is null.
	 @return A message activity containing the attachment.
	 @exception ArgumentNullException
	 <paramref name="url"/> or <paramref name="contentType"/> is <c>null</c>,
	 empty, or white space.
	 <example>This code creates a message activity that contains an image.
	 <code>
	 IMessageActivity message =
		 MessageFactory.ContentUrl("https://{domainName}/cat.jpg", MediaTypeNames.Image.Jpeg, "Cat Picture");
	 </code>
	 </example>
	*/

	public static IMessageActivity ContentUrl(String url, String contentType, String name, String text, String ssml)
	{
		return ContentUrl(url, contentType, name, text, ssml, null);
	}

	public static IMessageActivity ContentUrl(String url, String contentType, String name, String text)
	{
		return ContentUrl(url, contentType, name, text, null, null);
	}

	public static IMessageActivity ContentUrl(String url, String contentType, String name)
	{
		return ContentUrl(url, contentType, name, null, null, null);
	}

	public static IMessageActivity ContentUrl(String url, String contentType)
	{
		return ContentUrl(url, contentType, null, null, null, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static IMessageActivity ContentUrl(string url, string contentType, string name = null, string text = null, string ssml = null, string inputHint = null)
	public static IMessageActivity ContentUrl(String url, String contentType, String name, String text, String ssml, String inputHint)
	{
		if (tangible.StringHelper.isNullOrWhiteSpace(url))
		{
			throw new NullPointerException("url");
		}

		if (tangible.StringHelper.isNullOrWhiteSpace(contentType))
		{
			throw new NullPointerException("contentType");
		}

		Attachment a = new Attachment();
		a.setContentType(contentType);
		a.ContentUrl = url;
		a.setName(!tangible.StringHelper.isNullOrWhiteSpace(name) ? name : "");

		return AttachmentActivity(AttachmentLayoutTypes.List, new ArrayList<Attachment>(Arrays.asList(a)), text, ssml, inputHint);
	}


	private static IMessageActivity AttachmentActivity(String attachmentLayout, java.lang.Iterable<Attachment> attachments, String text, String ssml)
	{
		return AttachmentActivity(attachmentLayout, attachments, text, ssml, null);
	}

	private static IMessageActivity AttachmentActivity(String attachmentLayout, java.lang.Iterable<Attachment> attachments, String text)
	{
		return AttachmentActivity(attachmentLayout, attachments, text, null, null);
	}

	private static IMessageActivity AttachmentActivity(String attachmentLayout, java.lang.Iterable<Attachment> attachments)
	{
		return AttachmentActivity(attachmentLayout, attachments, null, null, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: private static IMessageActivity AttachmentActivity(string attachmentLayout, IEnumerable<Attachment> attachments, string text = null, string ssml = null, string inputHint = null)
	private static IMessageActivity AttachmentActivity(String attachmentLayout, java.lang.Iterable<Attachment> attachments, String text, String ssml, String inputHint)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
		var ma = Activity.CreateMessageActivity();
		ma.AttachmentLayout = attachmentLayout;
		ma.Attachments = attachments.ToList();
		SetTextAndSpeak(ma, text, ssml, inputHint);
		return ma;
	}


	private static void SetTextAndSpeak(IMessageActivity ma, String text, String ssml)
	{
		SetTextAndSpeak(ma, text, ssml, null);
	}

	private static void SetTextAndSpeak(IMessageActivity ma, String text)
	{
		SetTextAndSpeak(ma, text, null, null);
	}

	private static void SetTextAndSpeak(IMessageActivity ma)
	{
		SetTextAndSpeak(ma, null, null, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: private static void SetTextAndSpeak(IMessageActivity ma, string text = null, string ssml = null, string inputHint = null)
	private static void SetTextAndSpeak(IMessageActivity ma, String text, String ssml, String inputHint)
	{
		// Note: we must put NULL in the fields, as the clients will happily render
		// an empty string, which is not the behavior people expect to see.
		ma.Text = !tangible.StringHelper.isNullOrWhiteSpace(text) ? text : null;
		ma.Speak = !tangible.StringHelper.isNullOrWhiteSpace(ssml) ? ssml : null;
		ma.InputHint = (inputHint != null) ? inputHint : InputHints.AcceptingInput;
	}
}