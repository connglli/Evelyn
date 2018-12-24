package io.github.leetsong.evelynofficial;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.List;

public class EvelynProtocol {

    public enum ProtocolScheme {
        evelyn, http, https
    }

    public enum ProtocolTarget {
        toclient, tohost
    }

    public static final String EVELYN_PROTOCOL_TEMPLATE = "%s/%s/%s"; // {to}/{bridge}/{message}
    public static final String EVELYN_PROTOCOL_TEMPLATE_EVELYN =
            ProtocolScheme.evelyn.name() + "://evelyn.com/" + EVELYN_PROTOCOL_TEMPLATE;
    public static final String EVELYN_PROTOCOL_TEMPLATE_HTTP =
            ProtocolScheme.http.name() + "://localhost:31115/evelyn/" + EVELYN_PROTOCOL_TEMPLATE;
    public static final String EVELYN_PROTOCOL_TEMPLATE_HTTPS =
            ProtocolScheme.https.name() + "://localhost:31115/evelyn/" + EVELYN_PROTOCOL_TEMPLATE;

    public static class EvelynMessage {
        private ProtocolScheme scheme;
        private ProtocolTarget to;
        private String bridge;
        private String message;
        private long timestamp;

        private EvelynMessage() {}

        public String getScheme() {
            return scheme.name();
        }

        public String getTo() {
            return to.name();
        }

        public String getBridge() {
            return bridge;
        }

        public String getMessage() {
            return message;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String toUrl() {
            switch (scheme) {
            case evelyn:
                return String.format(EVELYN_PROTOCOL_TEMPLATE_EVELYN,
                        to.name(), bridge, Uri.encode(message));
            case http:
                return String.format(EVELYN_PROTOCOL_TEMPLATE_HTTP,
                        to.name(), bridge, Uri.encode(message));
            case https:
                return String.format(EVELYN_PROTOCOL_TEMPLATE_HTTPS,
                        to.name(), bridge, Uri.encode(message));
            }

            return null;
        }

        @Override
        public String toString() {
            return this.toUrl();
        }

        public static EvelynMessage fromUrl(@NonNull String url) {
            try {
                EvelynMessage evelynMessage = new EvelynMessage();

                Uri uri = Uri.parse(url);
                ProtocolScheme scheme = ProtocolScheme.valueOf(uri.getScheme());
                List<String> pathSegments = uri.getPathSegments();

                int index;
                switch (scheme) {
                    case evelyn:
                        evelynMessage.scheme = ProtocolScheme.evelyn;
                        index = 0;
                        break;
                    case http:
                        evelynMessage.scheme = ProtocolScheme.http;
                        index = 1;
                        break;
                    case https:
                        evelynMessage.scheme = ProtocolScheme.https;
                        index = 1;
                        break;
                    default:
                        return null;
                }

                evelynMessage.to = ProtocolTarget.valueOf(pathSegments.get(index));
                evelynMessage.bridge = pathSegments.get(index + 1);
                evelynMessage.message = Uri.decode(pathSegments.get(index + 2));
                evelynMessage.timestamp = System.currentTimeMillis();

                return evelynMessage;
            } catch (Exception e) {
                // for those that cannot successfully converted to ProtocolScheme, or ProtocolTarget,
                // or any other exceptions, directly return null
                return null;
            }
        }

        public static EvelynMessage fromClientUrl(@NonNull String url) {
            EvelynMessage evelynMessage = fromUrl(url);
            return evelynMessage != null && ProtocolTarget.tohost.equals(evelynMessage.to)
                    ? evelynMessage
                    : null;
        }
    }
}
