package rpc.server.controllers.play;

import play.libs.F;
import play.mvc.WebSocket;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

import rpc.shared.call.CallRequest;
import rpc.shared.call.CallResponse;
import rpc.server.GlobalHandler;

public class BaseWebSocketController extends BaseController {
    public static class WebSocketActor extends UntypedActor {
        public static Props props(ActorRef out) {
            return Props.create(WebSocketActor.class, out);
        }

        private final ActorRef out;

        public WebSocketActor(ActorRef out) {
            this.out = out;
        }

        public void onReceive(Object message) throws Exception {
            if (message instanceof String) {
                CallRequest request =
                    requestSerializer.deserialize((String) message);
                CallResponse response = GlobalHandler.handle(request);

                String payload = responseSerializer.serialize(response);

                out.tell(payload, self());
            }
        }
    }

    public static WebSocket<String> socket() {
        return WebSocket.withActor(new F.Function<ActorRef, Props>() {
            public Props apply(ActorRef out) throws Throwable {
                return WebSocketActor.props(out);
            }
        });
    }
}
