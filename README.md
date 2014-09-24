IrcFramework
============

IRC framework for Java. Did one myself as I found the existing ones lacking simplicity and efficient extendability by plugins.

Look at the readme of repository SimpleThreadModules from this github user for installation instructions.

## Simple example

First look at this example:

    import com.fridenmf.ircframework.core.events.OnConnectEvent;
    import com.fridenmf.ircframework.core.events.OnPrivateMessageEvent;
    import com.fridenmf.ircframework.core.extendable.SimpleBot;

    public class HelloSimpleBot extends SimpleBot {

      public static void main(String[] arg){
        new HelloSimpleBot("irc.server.org", 6667, "BotNick", "BotUser");
      }

      public HelloSimpleBot(String host, int port, String nick, String user) {
        super(host, port, nick, user, null);
      }

      @Override
      public void onConnect(OnConnectEvent e) {
        join("#test123");
      }

      @Override
      public void onPrivateMessage(OnPrivateMessageEvent e) {
        sendPrivateMessage(e.getNick(), "I hear you");
        System.out.println("Got a message from "+e.getNick()+": "+e.getMessage());
      }
    }

This is all the code that is needed for a bot that joins the server with url irc.server.org, using port 6667, with a bot named BotNick and as the user BotUser. When the bot has connected to the server, it will join the channel #test123, and when it receives private messages, it will respond "I hear you" back to the user, and the received message to the console.

There is a lot more commands than join and sendPRivateMessage available to use. For a complete list, look at the source of IrcEventListener in com.fridenmf.ircframework.core.events.

## Module Example

If you want to be able to separate behavior into modules, extends IrcBot instead of SimpleBot. There are only two neccesary things to do to make things work, and that is to add modules, and call connect. Look at this code for a small example:

    import com.fridenmf.ircframework.core.extendable.IrcBot;

    public class HelloBot extends IrcBot {

      public static void main(String[] arg){
        new HelloBot();
      }

      public HelloBot() {
        addPlugin(new HelloVerbosePlugin(this, "VerbosePlugin"));
        addPlugin(new HelloEmptyPlugin(this, "EmptyPlugin"));

        connect("irc.server.org", 6667, "BotNick", "BotUser", "BotDescription");
      }
    }

And

    import com.fridenmf.ircframework.core.events.OnMessageEvent;
    import com.fridenmf.ircframework.core.extendable.IrcBot;
    import com.fridenmf.ircframework.core.extendable.IrcPlugin;

    public class HelloEmptyPlugin extends IrcPlugin {

      public HelloEmptyPlugin(IrcBot ircBot, String pluginName) {
        super(ircBot, pluginName);
      }

      @Override
      public void onMessage(OnMessageEvent e) {
        System.out.println("Got a message in channel "+e.getChannel()+": "+e.getMessage());
      }
    }

You can make any number of these plugins, and they will all get get the same events. HelloVerbosePlugin in com.fridenmf.ircframework.examples contains implementations of all events that a plugin can listen on. Check the source of that class for inspiration.

The IRC protocol is stateless, meaning you can't call names() (for listing nicks in a channel) and expect a result right away. A request will be made to the server and the response will trigger onNames(OnNamesEvent e);

The plugins, network readers, writers, and all demanding tasks run in different threads, and all with proper concurrency and sleeps when no events occur. Expect great performance and easy expandability due to the modules. Happy coding.
