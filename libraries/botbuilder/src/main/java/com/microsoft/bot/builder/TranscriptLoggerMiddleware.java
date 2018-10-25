package com.microsoft.bot.builder;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.microsoft.bot.schema.ActivityImpl;
import com.microsoft.bot.schema.models.Activity;
import com.microsoft.bot.schema.models.ActivityTypes;
import com.microsoft.bot.schema.models.ResourceResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;


/**
 * When added, this middleware will log incoming and outgoing activitites to a ITranscriptStore.
 */
public class TranscriptLoggerMiddleware implements Middleware {
	// https://github.com/FasterXML/jackson-databind/wiki/Serialization-Features
	private static ObjectMapper mapper;

	static {
		mapper = new ObjectMapper()
				.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.findAndRegisterModules();
	}

	private ITranscriptLogger logger;
	private static final Logger log4j = LogManager.getLogger("BotFx");

	private LinkedList<Activity> transcript = new LinkedList<Activity>();

	/**
	 * Initializes a new instance of the <see cref="TranscriptLoggerMiddleware"/> class.
	 *
	 * @param transcriptLogger The transcript logger to use.
	 */
	public TranscriptLoggerMiddleware(ITranscriptLogger transcriptLogger) {
		if (transcriptLogger == null)
			throw new NullPointerException("TranscriptLoggerMiddleware requires a ITranscriptLogger implementation.  ");

		this.logger = transcriptLogger;

	}

	/**
	 * initialization for middleware turn.
	 *
	 * @param context
	 * @param next
	 * @return
	 */
	@Override
	public CompletableFuture OnTurnAsync(TurnContext context, NextDelegate next)  {
	    return CompletableFuture.runAsync(() -> {
            // log incoming activity at beginning of turn
            if (context.activity() != null) {
                JsonNode role = null;
                if (context.activity().from() == null) {
                    throw new RuntimeException("Activity does not contain From field");
                }
                if (context.activity().from().properties().containsKey("role")) {
                    role = context.activity().from().properties().get("role");
                }

                if (role == null || StringUtils.isBlank(role.asText())) {
                    context.activity().from().properties().put("role", mapper.createObjectNode().with("user"));
                }
                Activity activityTemp = ActivityImpl.CloneActity(context.activity());

                LogActivity(ActivityImpl.CloneActity(context.activity()));
            }

            // hook up onSend pipeline
            context.OnSendActivities((ctx, activities, nextSend) ->
            {

                return CompletableFuture.supplyAsync(() -> {
                    // run full pipeline
                    ResourceResponse[] responses = new ResourceResponse[0];
                    try {
                        if (nextSend != null) {
                            responses = nextSend.call().join();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    for (Activity activity : activities) {
                        LogActivity(ActivityImpl.CloneActity(activity));
                    }

                    return responses;
                });
            });

            // hook up update activity pipeline
            context.OnUpdateActivity((ctx, activity, nextUpdate) ->
            {
                return CompletableFuture.supplyAsync(() -> {
                    // run full pipeline
                    ResourceResponse response = null;
                    try {
                        if (nextUpdate != null) {
                            response = nextUpdate.call().join();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(String.format("Error on Logging.OnUpdateActivity : %s", e.toString()));
                    }

                    // add Message Update activity
                    Activity updateActivity = ActivityImpl.CloneActity(activity);
                    updateActivity.withType(ActivityTypes.MESSAGE_UPDATE.toString());
                    LogActivity(updateActivity);
                    return response;
                });
            });

            // hook up delete activity pipeline
            context.OnDeleteActivity((ctxt, reference, nextDel) -> {
                return CompletableFuture.runAsync(() -> {
                    // run full pipeline

                    try {
                        if (nextDel != null) {
                            log4j.error(String.format("Transcript logActivity next delegate: %s)", nextDel));
                            nextDel.call();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log4j.error(String.format("Transcript logActivity failed with %s (next delegate: %s)", e.toString(), nextDel));
                        throw new RuntimeException(String.format("Transcript logActivity failed with %s", e.getMessage()));

                    }

                    // add MessageDelete activity
                    // log as MessageDelete activity
                    Activity deleteActivity = new Activity()
                            .withType(ActivityTypes.MESSAGE_DELETE.toString())
                            .withId(reference.activityId())
                            .applyConversationReference(reference, false);

                    LogActivity(deleteActivity);
                    return;
                });

            });


            // process bot logic
            try {
                next.invoke().get();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(String.format("Error on Logging.next : %s", e.toString()));
            }

            // flush transcript at end of turn
            while (!transcript.isEmpty()) {
                Activity activity = transcript.poll();
                try {
                    this.logger.LogActivityAsync(activity).get();
                } catch (RuntimeException|JsonProcessingException|InterruptedException|ExecutionException err) {
                    err.printStackTrace();
                    log4j.error(String.format("Transcript poll failed : %1$s", err));
                    throw new CompletionException(err);
                }
            }
        });

	}


	private void LogActivity(Activity activity) {
		synchronized (transcript) {
			if (activity.timestamp() == null) {
				activity.withTimestamp(OffsetDateTime.now());
			}
			transcript.offer(activity);
		}
	}

}


