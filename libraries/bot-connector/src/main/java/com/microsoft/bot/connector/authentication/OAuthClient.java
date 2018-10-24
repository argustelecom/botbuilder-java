package com.microsoft.bot.connector.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.bot.connector.UserAgent;
import com.microsoft.bot.connector.implementation.ConnectorClientImpl;
import com.microsoft.bot.schema.AadResourceUrls;
import com.microsoft.bot.schema.TokenExchangeState;
import com.microsoft.bot.schema.TokenStatus;
import com.microsoft.bot.schema.models.*;
import com.microsoft.rest.ServiceClient;
import com.sun.jndi.toolkit.url.Uri;
import okhttp3.*;
import okio.BufferedSink;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.microsoft.bot.connector.authentication.MicrosoftAppCredentials.JSON;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.stream.Collectors.joining;


/**
 * Service client to handle requests to the botframework api service.
 * <p>
 * Uses the MicrosoftInterceptor class to add Authorization header from idp.
 */
public class OAuthClient extends ServiceClient {
    private final ConnectorClientImpl client;
    private final String uri;

    private ObjectMapper mapper;


    public OAuthClient(ConnectorClientImpl client, String uri) throws URISyntaxException, MalformedURLException {
        super(client.restClient());
        URI uriResult = new URI(uri);

        // Sanity check our url
        uriResult.toURL();
        String scheme = uriResult.getScheme();
        if (!scheme.toLowerCase().equals("https"))
            throw new IllegalArgumentException("Please supply a valid https uri");
        if (client == null)
            throw new NullPointerException("client");
        this.client = client;
        this.uri = uri + (uri.endsWith("/") ? "" : "/");
        this.mapper = new ObjectMapper();
    }

    /**
     The default endpoint that is used for API requests.
     */
    private static String OAuthEndpoint = AuthenticationConstants.OAuthUrl;
    public static String getOAuthEndpoint()
    {
        return OAuthEndpoint;
    }
    public static void setOAuthEndpoint(String value)
    {
        OAuthEndpoint = value;
    }

    /**
     When using the Emulator, whether to emulate the OAuthCard behavior or use connected flows
     */
    private static boolean EmulateOAuthCards = false;
    public static boolean getEmulateOAuthCards()
    {
        return EmulateOAuthCards;
    }
    public static void setEmulateOAuthCards(boolean value)
    {
        EmulateOAuthCards = value;
    }

    /**
     Gets a user token for a given user and connection.

     @param userId The user's ID.
     @param connectionName Name of the auth connection to use.
     @param magicCode The user entered code to validate.
     @param customHeaders
     @return A task that represents the work queued to execute.
     If the task completes successfully, the <see cref="TokenResponse"/> contains the user token.
     */
    public final CompletableFuture<TokenResponse> GetUserTokenAsync(String userId, String connectionName, String magicCode) throws URISyntaxException, ExecutionException, InterruptedException, IOException {
        return GetUserTokenAsync(userId, connectionName, magicCode, null);
    }

    public CompletableFuture<TokenResponse> GetUserTokenAsync(String userId, String connectionName, String magicCode, Map<String, ArrayList<String>> customHeaders) throws IllegalArgumentException {
        if (StringUtils.isBlank(userId)) {
            throw new NullPointerException("userId");
        }
        if (StringUtils.isBlank(connectionName)) {
            throw new NullPointerException("connectionName");
        }

        return CompletableFuture.supplyAsync(() -> {
            // Construct URL
            HashMap<String, String> qstrings = new HashMap<>();
            qstrings.put("userId", userId);
            qstrings.put("connectionName", connectionName);
            if (!StringUtils.isBlank(magicCode)) {
                qstrings.put("code", magicCode);
            }
            String strUri = String.format("%sapi/usertoken/GetToken", this.uri);
            URI tokenUrl = null;
            try {
                tokenUrl = MakeUri(strUri, qstrings);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return null;
            }

            // add botframework api service url to the list of trusted service url's for these app credentials.
            MicrosoftAppCredentials.trustServiceUrl(tokenUrl.toString());

            // Set Credentials and make call
            MicrosoftAppCredentials appCredentials = (MicrosoftAppCredentials) client.restClient().credentials();

            // Later: Use client in clientimpl?
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new MicrosoftAppCredentialsInterceptor(appCredentials))
                    .build();

            Request request = new Request.Builder()
                    .url(tokenUrl.toString())
                    .header("User-Agent", UserAgent.value())
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                int statusCode = response.code();
                if (statusCode == HTTP_OK) {
                    return this.mapper.readValue(response.body().string(), TokenResponse.class);
                } else if (statusCode == HTTP_NOT_FOUND) {
                    return null;
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (response != null)
                    response.body().close();
            }
            return null;
        });
    }

    /**
     * Signs Out the User for the given ConnectionName.
     *
     * @param userId
     * @param connectionName
     * @return True on successful sign-out; False otherwise.
     */
    public CompletableFuture<Boolean> SignOutUserAsync(String userId, String connectionName) throws URISyntaxException, IOException {
        return CompletableFuture.supplyAsync(() -> {
            if (StringUtils.isEmpty(userId)) {
                throw new IllegalArgumentException("userId");
            }
            if (StringUtils.isEmpty(connectionName)) {
                throw new IllegalArgumentException("connectionName");
            }

            String invocationId = null;

            // Construct URL
            HashMap<String, String> qstrings = new HashMap<>();
            qstrings.put("userId", userId);
            qstrings.put("connectionName", connectionName);
            String strUri = String.format("%sapi/usertoken/SignOut", this.uri);
            URI tokenUrl = null;
            try {
                tokenUrl = MakeUri(strUri, qstrings);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return false;
            }

            // add botframework api service url to the list of trusted service url's for these app credentials.
            MicrosoftAppCredentials.trustServiceUrl(tokenUrl);

            // Set Credentials and make call
            MicrosoftAppCredentials appCredentials = (MicrosoftAppCredentials) client.restClient().credentials();

            // Later: Use client in clientimpl?
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new MicrosoftAppCredentialsInterceptor(appCredentials))
                    .build();

            Request request = new Request.Builder()
                    .delete()
                    .url(tokenUrl.toString())
                    .header("User-Agent", UserAgent.value())
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                int statusCode = response.code();
                if (statusCode == HTTP_OK)
                    return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;

        });
    }

    /**
     * Gets the Link to be sent to the user for signin into the given ConnectionName
     *
     * @param activity
     * @param connectionName
     * @param finalRedirect The endpoint URL for the final page of a succesful login attempt.
     * @return A task that represents the work queued to execute.
     * If the task completes successfully and the call to the OAuth client is successful,
     * the result contains the signin link.     */
    public CompletableFuture<String> GetSignInLinkAsync(Activity activity, String connectionName, String userId, String finalRedirect) throws IllegalArgumentException, URISyntaxException, JsonProcessingException {
        if (StringUtils.isBlank(connectionName)) {
            throw new IllegalArgumentException("connectionName");
        }
        if (activity == null) {
            throw new IllegalArgumentException("activity");
        }
        final MicrosoftAppCredentials creds = (MicrosoftAppCredentials) this.client.restClient().credentials();

        // Handle User
        ChannelAccount user = null;
        if (userId != null) {
            user.withRole(RoleTypes.USER);
            user.withId(userId);
        }
        else {
            user = activity.from();
        }

        TokenExchangeState tokenExchangeState = new TokenExchangeState()
                .withConnectionName(connectionName)
                .withConversation(new ConversationReference()
                        .withActivityId(activity.id())
                        .withBot(activity.recipient())
                        .withChannelId(activity.channelId())
                        .withConversation(activity.conversation())
                        .withServiceUrl(activity.serviceUrl())
                        .withUser(user))
                .withMsAppId((creds == null) ? null : creds.microsoftAppId());

        String serializedState = this.mapper.writeValueAsString(tokenExchangeState);

        // Construct URL
        String encoded = Base64.getEncoder().encodeToString(serializedState.getBytes(StandardCharsets.UTF_8));
        HashMap<String, String> qstrings = new HashMap<>();
        qstrings.put("state", encoded);
        qstrings.put("finalRedirect", finalRedirect);

        String strUri = String.format("%sapi/botsignin/getsigninurl", this.uri);
        final URI tokenUrl = MakeUri(strUri, qstrings);

        return CompletableFuture.supplyAsync(() -> {

            // add botframework api service url to the list of trusted service url's for these app credentials.
            MicrosoftAppCredentials.trustServiceUrl(tokenUrl);


            // Later: Use client in clientimpl?
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new MicrosoftAppCredentialsInterceptor(creds))
                    .build();

            Request request = new Request.Builder()
                    .url(tokenUrl.toString())
                    .header("User-Agent", UserAgent.value())
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                int statusCode = response.code();
                if (statusCode == HTTP_OK)
                    return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }


    /**
     Get the status of tokens for connections for this bot for a particular user

     @param userId
     @param includeFilter A comma seperated list of connections to include. If null, then all connections are returned
     @param customHeaders
     @return
     */

    public final CompletableFuture<TokenStatus[]> GetTokenStatusAsync(String userId) throws URISyntaxException {
        return GetTokenStatusAsync(userId, null);
    }

    public final CompletableFuture<TokenStatus[]> GetTokenStatusAsync(String userId, String includeFilter) throws URISyntaxException {
        if (StringUtils.isBlank(userId))
        {
            throw new NullPointerException("userId");
        }

        String invocationId = null;
        final MicrosoftAppCredentials creds = (MicrosoftAppCredentials) this.client.restClient().credentials();

        // Construct URL
        HashMap<String, String> qstrings = new HashMap<>();
        qstrings.put("userId", userId);
        qstrings.put("include", includeFilter);

        String strUri = String.format("%sapi/usertoken/gettokenstatus", this.uri);
        final URI tokenUrl = MakeUri(strUri, qstrings);

        return CompletableFuture.supplyAsync(() -> {

            // add botframework api service url to the list of trusted service url's for these app credentials.
            MicrosoftAppCredentials.trustServiceUrl(tokenUrl);


            // Later: Use client in clientimpl?
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new MicrosoftAppCredentialsInterceptor(creds))
                    .build();

            Request request = new Request.Builder()
                    .url(tokenUrl.toString())
                    .header("User-Agent", UserAgent.value())
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                int statusCode = response.code();
                if (statusCode == HTTP_OK) {
                    return mapper.readValue(response.body().string(), TokenStatus[].class);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    /**
     Retrieve an Azure Active Directory token for particular AAD resources.

     @param userId The user's ID.
     @param connectionName Name of the auth connection to use for AAD token exchange.
     @param resourceUrls The collection of resource URLs for which to get tokens
     @return A task that represents the work queued to execute.
     If the task completes successfully, the response includes a collection of TokenResponse
     objects with the resourceUrl and its corresponding TokenResponse.
     */

    public final CompletableFuture<HashMap<String, TokenResponse>> GetAadTokensAsync(String userId, String connectionName, String[] resourceUrls) throws URISyntaxException {
        if (StringUtils.isBlank(userId)) {
            throw new NullPointerException("userId");
        }

        if (StringUtils.isBlank(connectionName)) {
            throw new NullPointerException("connectionName");
        }

        if (resourceUrls == null) {
            throw new NullPointerException("resourceUrls");
        }

        if (resourceUrls.length == 0) {
            throw new IllegalArgumentException("Collection cannot be empty", new Throwable("resourceUrls"));
        }

        if (Arrays.stream(resourceUrls).anyMatch(s -> StringUtils.isBlank(s))) {
            throw new IllegalArgumentException("Resource URLs must have a value", new Throwable("resourceUrls"));
        }

        String invocationId = null;
        final MicrosoftAppCredentials creds = (MicrosoftAppCredentials) this.client.restClient().credentials();

        // Construct URL
        HashMap<String, String> qstrings = new HashMap<>();
        qstrings.put("userId", userId);
        qstrings.put("connectionName", connectionName);

        String strUri = String.format("%sapi/usertoken/GetAadTokens", this.uri);
        final URI tokenUrl = MakeUri(strUri, qstrings);


        return CompletableFuture.supplyAsync(() -> {

            // add botframework api service url to the list of trusted service url's for these app credentials.
            MicrosoftAppCredentials.trustServiceUrl(tokenUrl);


            // Later: Use client in clientimpl?
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new MicrosoftAppCredentialsInterceptor(creds))
                    .build();

            // Serialize Request
            AadResourceUrls tempVar = new AadResourceUrls();
            tempVar.withResourceUrls(resourceUrls);
            String postBody = null;
            try {
                postBody = mapper.writeValueAsString(tempVar);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), postBody);
            Request request = new Request.Builder()
                    .method("POST", body)
                    .url(tokenUrl.toString())
                    .header("User-Agent", UserAgent.value())
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                int statusCode = response.code();
                if (statusCode == HTTP_OK) {
                    TypeReference<HashMap<String, TokenResponse>> typeRef
                            = new TypeReference<HashMap<String, TokenResponse>>() {
                    };
                    return mapper.readValue(response.body().string(), typeRef);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }


    /*=========================================== START OLD CODE GetSignInLinkAsync === */

    /**
     * Gets the Link to be sent to the user for signin into the given ConnectionName
     *
     * @param activity
     * @param connectionName
     * @return Sign in link on success; null otherwise.
     */
    public CompletableFuture<String> GetSignInLinkAsync(Activity activity, String connectionName) throws IllegalArgumentException, URISyntaxException, JsonProcessingException {
        if (StringUtils.isEmpty(connectionName)) {
            throw new IllegalArgumentException("connectionName");
        }
        if (activity == null) {
            throw new IllegalArgumentException("activity");
        }
        final MicrosoftAppCredentials creds = (MicrosoftAppCredentials) this.client.restClient().credentials();
        TokenExchangeState tokenExchangeState = new TokenExchangeState()
                .withConnectionName(connectionName)
                .withConversation(new ConversationReference()
                        .withActivityId(activity.id())
                        .withBot(activity.recipient())
                        .withChannelId(activity.channelId())
                        .withConversation(activity.conversation())
                        .withServiceUrl(activity.serviceUrl())
                        .withUser(activity.from()))
                .withMsAppId((creds == null) ? null : creds.microsoftAppId());

        String serializedState = this.mapper.writeValueAsString(tokenExchangeState);

        // Construct URL
        String encoded = Base64.getEncoder().encodeToString(serializedState.getBytes(StandardCharsets.UTF_8));
        HashMap<String, String> qstrings = new HashMap<>();
        qstrings.put("state", encoded);

        String strUri = String.format("%sapi/botsignin/getsigninurl", this.uri);
        final URI tokenUrl = MakeUri(strUri, qstrings);

        return CompletableFuture.supplyAsync(() -> {

            // add botframework api service url to the list of trusted service url's for these app credentials.
            MicrosoftAppCredentials.trustServiceUrl(tokenUrl);


            // Later: Use client in clientimpl?
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new MicrosoftAppCredentialsInterceptor(creds))
                    .build();

            Request request = new Request.Builder()
                    .url(tokenUrl.toString())
                    .header("User-Agent", UserAgent.value())
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                int statusCode = response.code();
                if (statusCode == HTTP_OK)
                    return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    /**
     * Send a dummy OAuth card when the bot is being used on the emulator for testing without fetching a real token.
     *
     * @param emulateOAuthCards
     * @return void with no result code
     */
    public CompletableFuture<Void> SendEmulateOAuthCardsAsync(Boolean emulateOAuthCards) throws URISyntaxException, IOException {

        // Construct URL
        HashMap<String, String> qstrings = new HashMap<>();
        qstrings.put("emulate", emulateOAuthCards.toString());
        String strUri = String.format("%sapi/usertoken/emulateOAuthCards", this.uri);
        URI tokenUrl = MakeUri(strUri, qstrings);

        // add botframework api service url to the list of trusted service url's for these app credentials.
        MicrosoftAppCredentials.trustServiceUrl(tokenUrl);

        return CompletableFuture.runAsync(() -> {
            // Construct dummy body
            RequestBody body = RequestBody.create(JSON, "{}");

            // Set Credentials and make call
            MicrosoftAppCredentials appCredentials = (MicrosoftAppCredentials) client.restClient().credentials();

            // Later: Use client in clientimpl?
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new MicrosoftAppCredentialsInterceptor(appCredentials))
                    .build();

            Request request = new Request.Builder()
                    .url(tokenUrl.toString())
                    .header("User-Agent", UserAgent.value())
                    .post(body)
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                int statusCode = response.code();
                if (statusCode == HTTP_OK)
                    return;
            } catch (IOException e) {
                e.printStackTrace();
            }


            // Apparently swallow any results
            return;

        });
    }

    protected URI MakeUri(String uri, HashMap<String, String> queryStrings) throws URISyntaxException {
        String newUri = queryStrings.keySet().stream()
                .map(key -> {
                    try {
                        return key + "=" + URLEncoder.encode(queryStrings.get(key), StandardCharsets.UTF_8.toString());
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(joining("&", (uri.endsWith("?") ? uri : uri + "?"), ""));
        return new URI(newUri);


    }


}
